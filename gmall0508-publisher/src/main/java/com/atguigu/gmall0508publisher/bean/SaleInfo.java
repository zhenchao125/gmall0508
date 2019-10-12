package com.atguigu.gmall0508publisher.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author lzc
 * @Date 2019-10-12 14:06
 */
public class SaleInfo {
    private long total;
    private List<Stat> stats = new ArrayList<>();
    private List<HashMap> detail;

    public void addStat(Stat stat) {
        stats.add(stat);
    }

    public void setDetail(List<HashMap> detail) {
        this.detail = detail;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public List<HashMap> getDetail() {
        return detail;
    }

}
