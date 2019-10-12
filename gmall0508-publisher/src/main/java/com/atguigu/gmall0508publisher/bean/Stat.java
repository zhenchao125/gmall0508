package com.atguigu.gmall0508publisher.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lzc
 * @Date 2019-10-12 13:06
 */
public class Stat {
    private List<Option> options = new ArrayList<>();

    private String title;

    public void addOption(Option option){
        options.add(option);
    }
    public List<Option> getOptions() {
        return options;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
