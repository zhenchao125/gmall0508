package com.atguigu.canal

import java.net.{InetSocketAddress, SocketAddress}
import java.util

import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, RowChange}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.atguigu.canal.utils.CanalHandler
import com.google.protobuf.ByteString

/**
  * Author lzc
  * Date 2019-10-09 16:38
  */
object CanalClient {
    def main(args: Array[String]): Unit = {
        // 1. 建立连接
        val address: SocketAddress = new InetSocketAddress("hadoop201", 11111)
        val conn: CanalConnector = CanalConnectors.newSingleConnector(address, "example", "", "")
        conn.connect() // 连接到canal
        conn.subscribe("gmall.*") // 订阅要读取的数据的表
        // 2. 读数据
        while (true) {
            val msg: Message = conn.get(100)
            val entries: util.List[CanalEntry.Entry] = msg.getEntries
            import scala.collection.JavaConversions._
            if (entries != null && !entries.isEmpty) {
                for (entry <- entries) {
                    if (entry.getEntryType == EntryType.ROWDATA) {  // 如果entry类型是涉及到行的变化
                        
                        val storeValue: ByteString = entry.getStoreValue
                        val rowChange: RowChange = RowChange.parseFrom(storeValue)
                        val rowDataList: util.List[CanalEntry.RowData] = rowChange.getRowDatasList
                        CanalHandler.handler(entry.getHeader.getTableName, rowDataList, rowChange.getEventType)
                    }
                }
            }else{
                println("没有获取到数据, 2s之后重新拉取...")
                Thread.sleep(2000)
            }
        }
    }
}

/*
 // 1. 建立连接

2. 通过连接读数据

    Message get一次获取一个Message, 一个Message表示一批数据, 看出多条sql语句执行的结果
    Entry  一个Message封装多个Entry, 一个Entry可以看出一条sql语句执行的多行结果
    StoreValue 一个 Entry封装一个 storeValue, 可以看到是是数据序列化形式
    RowChange 从StoreValue里面解析出来的数据类型
    RowData 一个rowChage会有多个RowData, 一个RowData封装了一行数据
    Column 列



 */