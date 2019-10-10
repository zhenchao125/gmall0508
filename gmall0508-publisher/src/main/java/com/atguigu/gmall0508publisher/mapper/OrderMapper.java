package com.atguigu.gmall0508publisher.mapper;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    /**
    指定日期总的销售额
     */
    Double getTotalAmount(String date);

    /**
     * 销售额的小时明细
     * @param date
     * @return
     */
    List<Map> getHourAmount(String date);
}
