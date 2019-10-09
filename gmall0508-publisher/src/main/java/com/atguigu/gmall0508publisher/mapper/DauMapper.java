package com.atguigu.gmall0508publisher.mapper;

import java.util.List;
import java.util.Map;

public interface DauMapper {
    /**
     * 查询指定日期的日活
     */
    Long getDau(String date);

    /**
     * 小时的日活
     */
    List<Map> getHourDau(String date);
}
