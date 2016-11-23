package com.modernsky.istv.bean;

import java.io.Serializable;

/**
 * Created by zhengzai on 16/9/18.
 */
public class TimeShaftBean implements Serializable {
    /**
     * time : 1474167721694
     */

    private DataBean data;
    /**
     * data : {"time":1474167721694}
     * message : success
     * page : 1
     * size : 1
     * status : 1
     * timestamp : 1474167721694
     * total : 1
     */

    private String message;
    private int page;
    private int size;
    private int status;
    private String timestamp;
    private int total;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static class DataBean {
        private long time;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
