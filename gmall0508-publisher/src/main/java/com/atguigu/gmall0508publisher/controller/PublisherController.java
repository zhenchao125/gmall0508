package com.atguigu.gmall0508publisher.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0508publisher.bean.Option;
import com.atguigu.gmall0508publisher.bean.SaleInfo;
import com.atguigu.gmall0508publisher.bean.Stat;
import com.atguigu.gmall0508publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

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

        } else if ("order_amount".equals(id)) {
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

    @GetMapping("/sale_detail")
    public String getSaleDetal(@RequestParam("date") String date,
                               @RequestParam("startpage") long startpage,
                               @RequestParam("size") long size,
                               @RequestParam("keyword") String keyword) throws IOException {

        DecimalFormat formatter = new DecimalFormat(".0000");
        // 1. 获取按照性别聚合的数据
        /*
            {"total":200,
             "aggMap":{"F":99,"M":101},
             "detail":[{"sku_num":34.0,"order_count":13.0,"sku_category1_name":"手机","es_metadata_id":"9eifvW0BkhLVie7ysQFu","user_age":13.0,"sku_id":"4","user_gender":"F","order_price":1442.0,"sku_category2_name":"手机通讯","dt":"2019-05-20","user_id":"105","sku_category3_id":"61","order_amount":49028.0,"sku_category2_id":"13","user_level":"3","sku_tm_id":"1","sku_category1_id":"2","sku_name":"小米Play 流光渐变AI双摄 4GB+64GB 梦幻蓝 全网通4G 双卡双待 小水滴全面屏拍照游戏智能手机","spu_id":"1","sku_category3_name":"手机"}]}

         */
        Map<String, Object> genderMap = service.getSaleDetailAndAggResultByField(date, startpage, size, "user_gender", keyword, 2);
        // 2. 获取按照年龄聚合的数据

        Map<String, Object> ageMap = service.getSaleDetailAndAggResultByField(date, startpage, size, "user_age", keyword, 100);

        System.out.println(JSON.toJSONString(genderMap));
        System.out.println(JSON.toJSONString(ageMap));
        // 1. 最终的封装的数据对象
        SaleInfo saleInfo = new SaleInfo();

        // 2. 设置总数
        long total = (Integer) genderMap.get("total");
        saleInfo.setTotal(total);

        // 3. 设置明细
        List<HashMap> detail = (List<HashMap>)genderMap.get("detail");
        saleInfo.setDetail(detail);

        // 4. 设置饼图
        // 4.1 性别分组的饼图
        Stat genderStat = new Stat();
        genderStat.setTitle("用户性别占比");

        Map<String, Long> genderAggMap = (Map<String, Long>)genderMap.get("aggMap");
        Option maleOption = new Option();
        maleOption.setName("男");
        System.out.println(genderAggMap);
        maleOption.setValue(genderAggMap.get("M"));
        genderStat.addOption(maleOption);

        Option femaleOption = new Option();
        femaleOption.setName("女");
        femaleOption.setValue(genderAggMap.get("F"));
        genderStat.addOption(femaleOption);

        saleInfo.addStat(genderStat);

        // 4.2 年龄的饼图
        /*
        {"total":200,
         "aggMap":{"44":5,"45":1,"46":5,"47":4,"48":5,"49":3,"50":2,"51":5,"52":4,"53":2,"54":6,"10":5,"55":6,"11":1,"12":6,"56":3,"13":6,"57":3,"14":5,"58":4,"15":3,"59":1,"16":4,"17":7,"18":2,"19":2,"9":4,"20":1,"21":2,"22":5,"23":6,"24":4,"25":4,"26":4,"27":3,"28":3,"29":6,"30":2,"31":6,"32":3,"33":1,"34":4,"35":3,"36":6,"37":3,"38":5,"39":3,"40":2,"41":6,"42":5,"43":9},
         "detail":[{"sku_num":34.0,"order_count":13.0,"sku_category1_name":"手机","es_metadata_id":"9eifvW0BkhLVie7ysQFu","user_age":13.0,"sku_id":"4","user_gender":"F","order_price":1442.0,"sku_category2_name":"手机通讯","dt":"2019-05-20","user_id":"105","sku_category3_id":"61","order_amount":49028.0,"sku_category2_id":"13","user_level":"3","sku_tm_id":"1","sku_category1_id":"2","sku_name":"小米Play 流光渐变AI双摄 4GB+64GB 梦幻蓝 全网通4G 双卡双待 小水滴全面屏拍照游戏智能手机","spu_id":"1","sku_category3_name":"手机"}]}

         */
        Stat ageStat = new Stat();
        ageStat.setTitle("用户年龄占比");

        ageStat.addOption(new Option("20岁以下", 0));
        ageStat.addOption(new Option("20岁到30岁", 0));
        ageStat.addOption(new Option("30岁及30岁以上", 0));

        Map<String, Long> ageAggMap = (Map<String, Long>)ageMap.get("aggMap");
        Set<Map.Entry<String, Long>> entries = ageAggMap.entrySet();
        for (Map.Entry<String, Long> entry : entries) {
            if(Integer.parseInt(entry.getKey()) < 20){
                double value = ageStat.getOptions().get(0).getValue() + entry.getValue();
                ageStat.getOptions().get(0).setValue(value);

            }else if(Integer.parseInt(entry.getKey()) < 30 ){
                double value = ageStat.getOptions().get(1).getValue() + entry.getValue();
                ageStat.getOptions().get(1).setValue(value);
            }else {
                double value = ageStat.getOptions().get(2).getValue() + entry.getValue();
                ageStat.getOptions().get(2).setValue(value);
            }
        } // 10%
        saleInfo.addStat(ageStat);
        System.out.println(JSON.toJSONString(saleInfo));
        return JSON.toJSONString(saleInfo);

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


http://localhost:8070/sale_detail?date=2019-05-20&&startpage=1&&size=5&&keyword=手机小米
 */