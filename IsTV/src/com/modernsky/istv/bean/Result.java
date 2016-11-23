package com.modernsky.istv.bean;

public class Result<T, R> extends BaseBean {
    public String message;
    public int status;
    public String timestamp;
    public int page;
    public int size;
    public int total;
    public SearchData<T, R> data;
}
