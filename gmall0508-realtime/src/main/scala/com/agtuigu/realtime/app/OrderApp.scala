package com.agtuigu.realtime.app

import com.agtuigu.realtime.bean.OrderInfo
import com.agtuigu.realtime.util.MyKafkaUtil
import com.alibaba.fastjson.JSON
import com.atguigu.gmall0508.common.ConstantUtil
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Author lzc
  * Date 2019-10-10 10:14
  */
object OrderApp {
    def main(args: Array[String]): Unit = {
        // 从kafka消费数据:订单数据
        val conf = new SparkConf().setAppName("DauApp").setMaster("local[2]")
        val ssc = new StreamingContext(conf, Seconds(3))
        val sourceDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaSteam(ssc, ConstantUtil.ORDER_TOPIC)
        val orderInfoDSteam: DStream[OrderInfo] = sourceDStream.map {
            case (_, jsonValue) => {
                val orderInfo: OrderInfo = JSON.parseObject(jsonValue, classOf[OrderInfo])
                orderInfo.create_date = orderInfo.create_time.substring(0, 10)
                orderInfo.create_hour = orderInfo.create_time.substring(11, 13)
                // 某些字段做脱敏处理
                orderInfo.consignee = orderInfo.consignee.substring(0, 1) + "**"
                orderInfo.consignee_tel = orderInfo.consignee_tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")
                orderInfo
            }
        }
        import org.apache.phoenix.spark._
        // 写入到hbase
        orderInfoDSteam.foreachRDD(rdd => {
            rdd.saveToPhoenix(ConstantUtil.ORDER_TABLE_NAME,
                Seq("ID", "PROVINCE_ID", "CONSIGNEE", "ORDER_COMMENT", "CONSIGNEE_TEL", "ORDER_STATUS", "PAYMENT_WAY", "USER_ID", "IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME", "DELIVERY_ADDRESS", "CREATE_TIME", "OPERATE_TIME", "TRACKING_NO", "PARENT_ORDER_ID", "OUT_TRADE_NO", "TRADE_BODY", "CREATE_DATE", "CREATE_HOUR"),
                zkUrl = Some("hadoop201,hadoop202,hadoop203:2181")
            )
        })
        
        ssc.start()
        ssc.awaitTermination()
    }
}
