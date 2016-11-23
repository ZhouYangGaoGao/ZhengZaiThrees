package com.modernsky.istv.bean;

public class ResultBean<T> extends BaseBean {
    public String message;
    public int status;
    public String timestamp;
    public int page;
    public int size;
    public int total;
    public int praiseCount;
    public int commentCount;
    public String commentId;
    public T data;
}
