package com.agtuigu.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.agtuigu.realtime.bean.StartupLog
import com.agtuigu.realtime.util.{MyKafkaUtil, RedisUtil}
import com.alibaba.fastjson.JSON
import com.atguigu.gmall0508.common.ConstantUtil
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

/**
  * Author lzc
  * Date 2019-10-08 09:03
  */
object DauApp {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setAppName("DauApp").setMaster("local[2]")
        val ssc = new StreamingContext(conf, Seconds(3))
        val sourceDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaSteam(ssc, ConstantUtil.STARTUP_TOPIC)
        // 1. 封装数据
        val startupLogDStream = sourceDStream.map {
            case (_, value) =>
                val log = JSON.parseObject(value, classOf[StartupLog])
                log.logDate = new SimpleDateFormat("yyyy-MM-dd").format(log.ts)
                log.logHour = new SimpleDateFormat("HH").format(log.ts)
                log
        }
        
        // 2. redis 去重
        // 2.1 从redis读取已经启动过的设备, 启动过的设备给过滤掉
        var filteredDSteam: DStream[StartupLog] = startupLogDStream.transform(rdd => {
            // ...
            // 2.1.1 读取到已经启动过的设备
            val client: Jedis = RedisUtil.getJedisClient
            val uidSet: util.Set[String] = client.smembers(ConstantUtil.STARTUP_TOPIC + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
            val uidSetBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(uidSet)
            client.close()
            
            // 2.1.2 过滤掉已经启动过的
            rdd.filter(log => {
                !uidSetBC.value.contains(log.uid)
            })
        })
        // 2.1.3 考虑到某个用户第一次启动所在的批次中出现该用户多次启动
        filteredDSteam = filteredDSteam
            .map(log => (log.uid, log))
            .groupByKey
            .map {
                case (_, logIt) => logIt.toList.sortBy(_.ts).head
//                case (_, logIt) => logIt.toList.minBy(_.ts)
            }
        
        // 2.2 写入到redis中 只写mid  (表示已经启动过的设备)
        filteredDSteam.foreachRDD(rdd => {
            rdd.foreachPartition(logIt => {
                val client: Jedis = RedisUtil.getJedisClient
                logIt.foreach(log => {
                    client.sadd(ConstantUtil.STARTUP_TOPIC + ":" + log.logDate, log.uid)
                })
                client.close()
            })
        })
        
        import org.apache.phoenix.spark._
        // 3. 写到 hbase
        filteredDSteam.foreachRDD(rdd => {
            rdd.saveToPhoenix(
                "GMALL0508_DAU",
                Seq("MID", "UID", "APPID", "AREA", "OS", "CH", "TYPE", "VS", "TS", "LOGDATE", "LOGHOUR"),
                zkUrl = Some("hadoop201,hadoop202,hadoop203:2181")
            )
        })
        
        ssc.start()
        ssc.awaitTermination()
    }
}

/*
1. 数据封装
2. 使用 redis 做去重
    一个设备每天会启动多次, 向hbase存储的时候, 应该只存第一条启动
    
    写到redis
    
    key   value
    "startuplog:" + date       set(mid, mid, ...)

3. 第一条启动数据存入的hbase

 */