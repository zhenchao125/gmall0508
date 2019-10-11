package com.agtuigu.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.agtuigu.realtime.bean.{AlertInfo, EventLog}
import com.agtuigu.realtime.util.MyKafkaUtil
import com.alibaba.fastjson.JSON
import com.atguigu.gmall0508.common.ConstantUtil
import com.atguigu.gmall0508.common.util.ESUtil
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.util.control.Breaks._

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
        val eventLogDStream: DStream[(String, EventLog)] = sourceDStream
            .window(Seconds(5 * 60), Seconds(5))
            .map {
                case (_, jsonValue) => {
                    val eventLog = JSON.parseObject(jsonValue, classOf[EventLog])
                    val date = new Date(eventLog.ts)
                    eventLog.logDate = dateFormatter.format(date)
                    eventLog.logHour = hourFormatter.format(date)
                    (eventLog.mid, eventLog)
                }
            }
        // 同一设备(按照设备分组)
        val alertInfoDStream: DStream[(Boolean, AlertInfo)] = eventLogDStream
            .groupByKey()
            .map {
                case (mid, eventLogIt) => {
                    val uidSet: util.Set[String] = new util.HashSet[String]() // 记录领优惠券的用户
                    val itemSet: util.Set[String] = new util.HashSet[String]() // 记录优惠券对应的商品的id
                    
                    val eventSet: util.Set[String] = new util.HashSet[String]() // 用户的操作
                    
                    var isClickItem = false // 是否浏览商品\
                    breakable {
                        eventLogIt.foreach(eventLog => {
                            eventSet.add(eventLog.eventId) // 这个设备所有用户的操作记录
                            if (eventLog.eventId == "coupon") {
                                uidSet.add(eventLog.uid) // 领优惠券的用户
                                itemSet.add(eventLog.itemId) // 优惠券对应的商品
                            } else if (eventLog.eventId == "clickItem") { // 如果有浏览商品, 则该设备不会产生预警
                                isClickItem = true
                                break
                            }
                        })
                    }
                    println((!isClickItem && uidSet.size() >= 3, AlertInfo(mid, uidSet, itemSet, eventSet, System.currentTimeMillis())))
                    // (是否预警, 预警样例对象)
                    (!isClickItem && uidSet.size() >= 3, AlertInfo(mid, uidSet, itemSet, eventSet, System.currentTimeMillis()))
                }
            }
        
        //
        val resultAlertInfoDStream: DStream[AlertInfo] = alertInfoDStream.filter(_._1).map(_._2) //
        resultAlertInfoDStream.foreachRDD(rdd => {
            rdd.foreachPartition(alertInfoIt => {
                // 为了实现一分钟只产生一条预警信息, 把mid和分钟组成一个一个最终的id
                ESUtil.insertBulk("gmall0508_coupon_alert", alertInfoIt.map(info=> (info.mid + "_" + info.ts / 1000 / 60, info)))  // Iterator[Any] -> Iterator[AlterInfo]
            })
        })
        
        
        ssc.start()
        ssc.awaitTermination()
    }
}

/*
1. 把数据从kafka读出来

2. 预警分析

    1.	同一设备(按照设备分组)
    2.	5分钟内(window)
    3.	三个不同账号登录
    4.	领取优惠券
    5.	并且没有浏览商品
    6.	同一设备每分钟只预警一次(同一设备, 每分钟只向 es 写一次记录)

 */