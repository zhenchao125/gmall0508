package com.atguigu.gmall0508publisher.bean;

/**
 * @Author lzc
 * @Date 2019-10-12 13:03
 */
public class Option {
    private String name;
    private double value;

    public Option(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public Option() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
