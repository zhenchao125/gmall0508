package com.atguigu.canal.utils

import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.otter.canal.protocol.CanalEntry
import com.alibaba.otter.canal.protocol.CanalEntry.EventType
import com.atguigu.gmall0508.common.ConstantUtil

/**
  * Author lzc
  * Date 2019-10-09 17:02
  */
object CanalHandler {
    def handler(tableName: String, rowDataList: util.List[CanalEntry.RowData], eventType: CanalEntry.EventType) = {
        import scala.collection.JavaConversions._
        if(tableName == "order_info" && eventType == EventType.INSERT && !rowDataList.isEmpty){
            for(rowData <- rowDataList){
                val jsonObj: JSONObject = new JSONObject()
                val columns: util.List[CanalEntry.Column] = rowData.getAfterColumnsList // 一行数据所有的列
                for(column <- columns){
                    val key: String = column.getName
                    val value: String = column.getValue
                    jsonObj.put(key, value)
                }
                // 写入到kafja
//                println(jsonObj.toJSONString)
                MyKafkaSenderUtil.send(ConstantUtil.ORDER_TOPIC, jsonObj.toJSONString)
            }
        }
    }
}
