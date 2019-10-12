package com.atguigu.gmall0508publisher.util

/**
  * Author lzc
  * Date 2019-10-12 10:44
  */
object ESDSLUtil {
    def getDSL(date: String, startPage: Long, size: Long, field: String, keyWord: String, aggSize: Long): String ={
        s"""
           |{
           |  "from": ${(startPage - 1) * size},
           |  "size": $size,
           |  "query": {
           |    "bool": {
           |      "filter": {
           |        "term": {
           |          "dt": "$date"
           |        }
           |      },
           |      "must": [
           |        {"match": {
           |          "sku_name": {
           |            "query": "$keyWord"
           |            , "operator": "and"
           |          }
           |        }}
           |      ]
           |    }
           |  },
           |  "aggs": {
           |    "goupby_$field": {
           |      "terms": {
           |        "field": "$field",
           |        "size": $aggSize
           |      }
           |    }
           |  }
           |}
         """.stripMargin
    }
}
