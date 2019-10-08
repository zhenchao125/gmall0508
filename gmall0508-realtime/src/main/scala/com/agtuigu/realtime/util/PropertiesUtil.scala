package com.agtuigu.realtime.util

import java.io.InputStream
import java.util.Properties

import scala.collection.mutable

/**
  * Author lzc
  * Date 2019-10-08 09:11
  */
object PropertiesUtil {
    /*val is: InputStream = ClassLoader.getSystemResourceAsStream("config.properties")
    val props = new Properties
    props.load(is)
    
    def getProperty(propName: String) = {
        props.getProperty(propName)
    }*/
    
    val map = mutable.Map[String, Properties]()
    
    def getProperty(propFile: String, propName: String) = {
        val props = map.getOrElseUpdate(propFile, {
            val is: InputStream = ClassLoader.getSystemResourceAsStream(propFile)
            val props = new Properties
            props.load(is)
            props
        })
        
        props.getProperty(propName)
    }
    
    def main(args: Array[String]): Unit = {
        println(getProperty("config.properties", "kafka.broker.list"))
    }
}
