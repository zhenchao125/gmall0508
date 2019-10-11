package com.atguigu.gmall0508.common.util

import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, Index}

/**
  * Author lzc
  * Date 2019-10-11 15:06
  */
object ESUtil {
    val esUrl = "http://hadoop201:9200"
    // 1. 创建es的客户端工厂
    val factory = new JestClientFactory
    val config: HttpClientConfig = new HttpClientConfig.Builder(esUrl)
        .multiThreaded(true)
        .maxTotalConnection(100)
        .connTimeout(10000)
        .readTimeout(10000)
        .build()
    factory.setHttpClientConfig(config)
    
    def getClient(): JestClient = factory.getObject
    
    /**
      * 插入单个 document
      *
      * @param indexName
      * @param source
      */
    def insertSingle(indexName: String, source: Any): Unit = {
        val client: JestClient = getClient()
        val index = new Index.Builder(source) // 1. json格式的字符串  2.  普通的bean对象
            .index(indexName)
            .`type`("_doc")
            .build()
        client.execute(index)
        client.close()
    }
    
    def insertBulk(indexName: String, sources: Iterator[Any]): Unit = {
        val client: JestClient = getClient()
        val bulkBuilder: Bulk.Builder = new Bulk.Builder()
            .defaultIndex(indexName)
            .defaultType("_doc")
        
        sources.foreach {
            case (id: String, source) =>
                val index: Index = new Index.Builder(source).id(id).build()
                bulkBuilder.addAction(index)
            case source =>
                val index: Index = new Index.Builder(source).build()
                bulkBuilder.addAction(index)
        }
        
        client.execute(bulkBuilder.build())
        
        client.shutdownClient()
    }
    
    
    def main(args: Array[String]): Unit = {
        
        
        // json
        /*val source: String =
            """
              |{
              | "name": "lisi",
              | "age": 10,
              | "sex": "male"
              |}
            """.stripMargin*/
        // 对象
        /*val source1 = User("lisi", 301, "female")
        val source2 = User("lisi", 302, "female")*/
        
        // 2. 调用客户端的相关的方法, 插入数据
        //        insertBulk("user0508", List(source1, source2).toIterator)
        
    }
}

/*
case class User(name: String, age: Int, sex: String)*/
