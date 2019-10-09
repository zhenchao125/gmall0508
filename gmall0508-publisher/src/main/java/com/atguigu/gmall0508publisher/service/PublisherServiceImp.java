package com.atguigu.gmall0508publisher.service;

import com.atguigu.gmall0508publisher.mapper.DauMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lzc
 * @Date 2019-10-09 10:41
 */
@Service
public class PublisherServiceImp implements PublisherService{
    @Autowired
    public DauMapper dauMapper;
    @Override
    public long getDau(String date) {
        return dauMapper.getDau(date);
    }

    /**
     *
     * @param date
     * @return
     */
    @Override
    public Map<String, Long> getHourDau(String date) {
        List<Map> list = dauMapper.getHourDau(date);
        // List[Map[loghour->01, count->100]] -> Map["01"->100, "02"->200]
        HashMap<String, Long> resultMap = new HashMap<>();
        for (Map map : list) {
            String hour = (String)map.get("LOGHOUR");
            Long count = (Long) map.get("COUNT");
            resultMap.put(hour, count);
        }
        return resultMap;
    }
}
