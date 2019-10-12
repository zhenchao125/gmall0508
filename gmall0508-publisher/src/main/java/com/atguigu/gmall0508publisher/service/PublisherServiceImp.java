package com.atguigu.gmall0508publisher.service;

import com.atguigu.gmall0508.common.util.ESUtil;
import com.atguigu.gmall0508publisher.mapper.DauMapper;
import com.atguigu.gmall0508publisher.mapper.OrderMapper;
import com.atguigu.gmall0508publisher.util.ESDSLUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lzc
 * @Date 2019-10-09 10:41
 */
@Service
public class PublisherServiceImp implements PublisherService {
    @Autowired
    public DauMapper dauMapper;

    @Override
    public long getDau(String date) {
        return dauMapper.getDau(date);
    }

    /**
     * @param date
     * @return
     */
    @Override
    public Map<String, Long> getHourDau(String date) {
        List<Map> list = dauMapper.getHourDau(date);
        // List[Map[loghour->01, count->100]] -> Map["01"->100, "02"->200]
        HashMap<String, Long> resultMap = new HashMap<>();
        for (Map map : list) {
            String hour = (String) map.get("LOGHOUR");
            Long count = (Long) map.get("COUNT");
            resultMap.put(hour, count);
        }
        return resultMap;
    }

    @Autowired
    OrderMapper orderMapper;

    @Override
    public double getTotalAmount(String date) {
        Double totalAmount = orderMapper.getTotalAmount(date);
        return totalAmount == null ? 0 : totalAmount;
    }

    // List[Map[loghour->01, sum->100.1], Map[...], Map[...]] -> Map["01"->100, "02"->200]
    @Override
    public Map<String, BigDecimal> getHourAmount(String date) {
        List<Map> hourAmountList = orderMapper.getHourAmount(date);

        Map<String, BigDecimal> resultMap = new HashMap<>();
        for (Map map : hourAmountList) {
            String hour = (String) map.get("CREATE_HOUR");
            BigDecimal sum = (BigDecimal) map.get("SUM");
            resultMap.put(hour, sum);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getSaleDetailAndAggResultByField(String date, long startPage, long size, String field, String keyWord, long aggSize) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();

        // 1. 先获取合适的json格式的数据
        String dsl = ESDSLUtil.getDSL(date, startPage, size, field, keyWord, aggSize);

        // 2. 获取es的客户端
        JestClient client = ESUtil.getClient();
        // 3. 查询对象
        Search search = new Search.Builder(dsl)
                .addIndex("gmall0508_sale_detail")
                .addType("_doc")
                .build();
        // 查询的结果
        SearchResult result = client.execute(search);

        // 1. 先获取到总的document的数量
        Integer total = result.getTotal();
        resultMap.put("total", total);
        // 2. 获取清单
        List<HashMap> details = new ArrayList<>();
        List<SearchResult.Hit<HashMap, Void>> hits = result.getHits(HashMap.class);
        for (SearchResult.Hit<HashMap, Void> hit : hits) {
            HashMap source = hit.source;  // 真的数据清单
            details.add(source);
        }
        resultMap.put("detail", details);
        // 3. 聚合的数据
        Map<String, Long> aggMap = new HashMap<>();
        List<TermsAggregation.Entry> buckets = result.getAggregations()
                .getTermsAggregation("goupby_" + field).getBuckets();
        for (TermsAggregation.Entry bucket : buckets) {
            String key = bucket.getKey();
            Long value = bucket.getCount();
            aggMap.put(key, value);
        }
        resultMap.put("aggMap", aggMap);
        return resultMap;
    }
}
