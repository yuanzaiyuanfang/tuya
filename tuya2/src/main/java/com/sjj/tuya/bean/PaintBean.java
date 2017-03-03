package com.sjj.tuya.bean;


public class PaintBean {
    public String title;
    public   String msg;

    public PaintBean(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    public static final String[]  titles = {"笔画","颜色"};
    public static final String [] msgs   = {"笔画","颜色"};
}
