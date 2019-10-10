package com.atguigu.gmall0508publisher.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0508publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lzc
 * @Date 2019-10-09 10:45
 */
@RestController
public class PublisherController {

    @Autowired
    public PublisherService service;
    /*
    [{"id":"dau","name":"新增日活","value":1200},
        {"id":"new_mid","name":"新增设备","value":233 },
        {"id":"order_amount","name":"新增交易额","value":1000.2 }]

     */
    // http://localhost:8070/realtime-total?date=2019-09-20
    @GetMapping("/realtime-total")
    public String getTotal(@RequestParam("date") String date) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", "dau");
        map1.put("name", "新增日活");
        map1.put("value", service.getDau(date));
        resultList.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", "new_mid");
        map2.put("name", "新增设备");
        map2.put("value", 233);
        resultList.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", "order_amount");
        map3.put("name", "新增交易额");
        map3.put("value", service.getTotalAmount(date));
        resultList.add(map3);

        return JSON.toJSONString(resultList);
    }

    // http://localhost:8070/realtime-hour?id=dau&date=2019-09-20
    @GetMapping("/realtime-hour")
    public String getHourData(@RequestParam("id") String id, @RequestParam("date") String date) {
        if ("dau".equals(id)) {

            Map<String, Long> today = service.getHourDau(date);
            Map<String, Long> yesterday = service.getHourDau(getYesterday(date));

            HashMap<String, Map<String, Long>> resultMap = new HashMap<>();
            resultMap.put("today", today);
            resultMap.put("yesterday", yesterday);
            return JSON.toJSONString(resultMap);

        } else if("order_amount".equals(id)){
            Map<String, BigDecimal> today = service.getHourAmount(date);
            Map<String, BigDecimal> yesterday = service.getHourAmount(getYesterday(date));

            HashMap<String, Map<String, BigDecimal>> resultMap = new HashMap<>();
            resultMap.put("today", today);
            resultMap.put("yesterday", yesterday);
            return JSON.toJSONString(resultMap);
        } else {

            return "";
        }

    }

    private String getYesterday(String date) {
        LocalDate ld = LocalDate.parse(date);
        return ld.minusDays(1).toString();
    }

}
/*
[{"id":"dau","name":"新增日活","value":1200},
{"id":"new_mid","name":"新增设备","value":233} ]


{"yesterday":{"11":383,"12":123,"17":88,"19":200 },
"today":{"12":38,"13":1233,"17":123,"19":688 }}

 */