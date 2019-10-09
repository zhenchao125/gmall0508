package com.atguigu.gmall0508publisher.service;

import java.util.Map;

public interface PublisherService {

    long getDau(String date);

    // List[Map[loghour->01, count->100]] -> Map["01"->100, "02"->200]
    Map<String, Long> getHourDau(String date);
}
