package com.atguigu.gmall0508.hive2es

import com.atguigu.gmall0508.common.util.ESUtil
import com.atguigu.gmall0508.hive2es.bean.SaleDetailDayCount
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Author lzc
  * Date 2019-10-12 09:17
  */
object Hive2ESApp {
    def main(args: Array[String]): Unit = {
       val date: String = if(args.isEmpty)  "2019-05-20" else args(0)
        
        val spark: SparkSession = SparkSession.builder()
            .master("local[*]")
            .appName("Hive2ESApp")
            .enableHiveSupport()
            .getOrCreate()
        import spark.implicits._
        
        val sql =
            s"""
               |select
               |    user_id,
               |    sku_id,
               |    user_gender,
               |    cast(user_age as int) user_age,
               |    user_level,
               |    cast(order_price as double) order_price,
               |    sku_name,
               |    sku_tm_id,
               |    sku_category3_id,
               |    sku_category2_id,
               |    sku_category1_id,
               |    sku_category3_name,
               |    sku_category2_name,
               |    sku_category1_name,
               |    spu_id,
               |    sku_num,
               |    cast(order_count as bigint) order_count,
               |    cast(order_amount as double) order_amount,
               |    dt
               |from dws_sale_detail_daycount
               |where dt='$date'
             """.stripMargin
    
        spark.sql("use gmall")
        val df = spark.sql(sql)
        
        // 2. 写入到es
        df.as[SaleDetailDayCount].foreachPartition(it => {
            ESUtil.insertBulk("gmall0508_sale_detail", it)
        })
        
    }
}
/*
1. 从hive读数据
    1. 创建sparkSession
    
    2. val df = spark.sql("...")


2. 写到es

   df.foreach...
 */