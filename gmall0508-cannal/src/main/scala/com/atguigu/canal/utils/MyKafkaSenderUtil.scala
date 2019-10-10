package com.atguigu.canal.utils

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Author lzc
  * Date 2019-10-10 09:39
  */
object MyKafkaSenderUtil {
    val props = new Properties()
    // Kafka服务端的主机名和端口号
    props.put("bootstrap.servers", "hadoop201:9092,hadoop202:9092,hadoop203:9092")
    // key序列化
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // value序列化
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    
    private val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](props)
    
    def send(topic: String, content: String) = {
        producer.send(new ProducerRecord[String, String](topic, content))
    }
    
    def main(args: Array[String]): Unit = {
        
    }
}
