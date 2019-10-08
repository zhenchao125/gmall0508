package com.agtuigu.realtime.app

import com.agtuigu.realtime.util.MyKafkaUtil
import com.atguigu.gmall0508.common.ConstantUtil
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Author lzc
  * Date 2019-10-08 09:03
  */
object DauApp {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setAppName("DauApp").setMaster("local[2]")
        val ssc = new StreamingContext(conf, Seconds(3))
        val sourceDStream: InputDStream[(String, String)] = MyKafkaUtil.getKafkaSteam(ssc, ConstantUtil.STARTUP_TOPIC)
        sourceDStream.print()
        
        ssc.start()
        ssc.awaitTermination()
    }
}
