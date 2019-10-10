package com.atguigu.gmall0508publisher.service;

import com.atguigu.gmall0508publisher.mapper.DauMapper;
import com.atguigu.gmall0508publisher.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
}
