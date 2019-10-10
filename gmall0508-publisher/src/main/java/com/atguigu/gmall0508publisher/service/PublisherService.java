package com.atguigu.gmall0508publisher.service;

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
}
