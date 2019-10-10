package com.agtuigu.realtime.app

import java.text.SimpleDateFormat
import java.util.Date

import com.agtuigu.realtime.bean.EventLog
import com.agtuigu.realtime.util.MyKafkaUtil
import com.alibaba.fastjson.JSON
import com.atguigu.gmall0508.common.ConstantUtil
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Author lzc
  * Date 2019-10-10 14:37
  * {"logType":"event","area":"beijing","uid":"285","eventId":"addCart","itemId":42,
  * "os":"android","nextPageId":22,"appId":"gmall0508","mid":"mid_261","pageId":25,"ts":1570689866703}
  */
object AlertApp {
    def main(args: Array[String]): Unit = {
        val dateFormatter = new SimpleDateFormat("yyyy-MM-dd")
        val hourFormatter = new SimpleDateFormat("HH:mm")
        
        val conf = new SparkConf().setAppName("DauApp").setMaster("local[2]")
        val ssc = new StreamingContext(conf, Seconds(5))
        val sourceDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaSteam(ssc, ConstantUtil.EVENT_TOPIC)
        val eventLogDStream: DStream[EventLog] = sourceDStream.map {
            case (_, jsonValue) => {
                val eventLog = JSON.parseObject(jsonValue, classOf[EventLog])
                val date = new Date(eventLog.ts)
                eventLog.logDate = dateFormatter.format(date)
                eventLog.logHour = hourFormatter.format(date)
                eventLog
            }
        }
        
        
        
        ssc.start()
        ssc.awaitTermination()
    }
}

/*
1. 把数据从kafka读出来

2. 预警分析
 */