package com.atguigu.gmall0508logger.controller

import com.alibaba.fastjson.{JSON, JSONObject}
import com.atguigu.gmall0508.common.ConstantUtil
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation._

/**
  * Author lzc
  * Date 2019-09-30 10:42
  */
//@Controller
@RestController   // @Controller + @ResponseBody
class LoggerController {
    // http://localhost:8080/log
    @PostMapping(Array("/log"))
    def doLog(@RequestParam("log") log: String) = {
        // 1. 添加ts
        val logWithTS: String = addTS(log)
        // 2. 落盘  使用日志的方式
        saveLog2File(logWithTS)
        // 3. 写入到kafka
        send2Kafka(logWithTS)
        "ok"
    }
    @Autowired
    var templete : KafkaTemplate[String, String] = _
    def send2Kafka(logWithTS: String) = {
        var topic: String = ConstantUtil.STARTUP_TOPIC
        if (JSON.parseObject(logWithTS).getString("logType") == "event") {
            topic = ConstantUtil.EVENT_TOPIC
        }
        templete.send(topic, logWithTS)
    }
    
    private val logger: Logger = LoggerFactory.getLogger(classOf[LoggerController])
    def saveLog2File(logWithTS:String) = {
        logger.info(logWithTS)
    }
    
    /**
    给日志添加时间戳
     */
    def addTS(log: String) = {
        val jsonObj: JSONObject = JSON.parseObject(log)
        jsonObj.put("ts", System.currentTimeMillis())
        jsonObj.toJSONString
    }
}
/*
java -cp ./gmall0508-logger-0.0.1-SNAPSHOT.jar:/opt/module/scala-2.11.8/lib/scala-library.jar org.springframework.boot.loade
r.JarLauncher

java -Djava.ext.dirs=/opt/module/scala-2.11.8/lib -jar gmall0508-logger-0.0.1-SNAPSHOT.jar

java -Djava.ext.dirs=/opt/module/scala-2.11.8/lib -cp ./gmall0508-logger-0.0.1-SNAPSHOT.jar org.springframework.boot.loader.
JarLauncher

 */
