package com.atguigu.gmall0508publisher.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public interface PublisherService {

    long getDau(String date);

    // List[Map[loghour->01, count->100]] -> Map["01"->100, "02"->200]
    Map<String, Long> getHourDau(String date);


    // 当天总的销售额
    double getTotalAmount(String date);

    // 销售额的小时明细
    Map<String, BigDecimal> getHourAmount(String date);



    // 获取灵活查询的结果  既可以按照年龄去查集合  也可以按照性别

    Map<String, Object> getSaleDetailAndAggResultByField(String date,
                                          long startPage,
                                          long size,
                                          String field,
                                          String keyWord,
                                          long aggSize) throws IOException;


}
