package com.modernsky.istv.bean;

import java.io.Serializable;

/**
 * Created by zhengzai on 16/9/18.
 */
public class WeeksBean implements Serializable {

    /**
     * week1 : 1
     * week2 : 0
     * week3 : 0
     * week4 : 0
     * month1 : 0
     * week5 : 0
     * week6 : 0
     * week7 : 0
     * week8 : 0
     * month2 : 0
     * week9 : 0
     * week10 : 0
     * week11 : 0
     * week12 : 0
     * month3 : 0
     * quarter1 : 0
     */

    private DataBean data;
    /**
     * data : {"week1":1,"week2":0,"week3":0,"week4":0,"month1":0,"week5":0,"week6":0,"week7":0,"week8":0,"month2":0,"week9":0,"week10":0,"week11":0,"week12":0,"month3":0,"quarter1":0}
     * message : success
     * page : 1
     * size : 1
     * status : 1
     * timestamp : 1474175351481
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
        private int week1;
        private int week2;
        private int week3;
        private int week4;
        private int month1;
        private int week5;
        private int week6;
        private int week7;
        private int week8;
        private int month2;
        private int week9;
        private int week10;
        private int week11;
        private int week12;
        private int month3;
        private int quarter1;

        public int getWeek1() {
            return week1;
        }

        public void setWeek1(int week1) {
            this.week1 = week1;
        }

        public int getWeek2() {
            return week2;
        }

        public void setWeek2(int week2) {
            this.week2 = week2;
        }

        public int getWeek3() {
            return week3;
        }

        public void setWeek3(int week3) {
            this.week3 = week3;
        }

        public int getWeek4() {
            return week4;
        }

        public void setWeek4(int week4) {
            this.week4 = week4;
        }

        public int getMonth1() {
            return month1;
        }

        public void setMonth1(int month1) {
            this.month1 = month1;
        }

        public int getWeek5() {
            return week5;
        }

        public void setWeek5(int week5) {
            this.week5 = week5;
        }

        public int getWeek6() {
            return week6;
        }

        public void setWeek6(int week6) {
            this.week6 = week6;
        }

        public int getWeek7() {
            return week7;
        }

        public void setWeek7(int week7) {
            this.week7 = week7;
        }

        public int getWeek8() {
            return week8;
        }

        public void setWeek8(int week8) {
            this.week8 = week8;
        }

        public int getMonth2() {
            return month2;
        }

        public void setMonth2(int month2) {
            this.month2 = month2;
        }

        public int getWeek9() {
            return week9;
        }

        public void setWeek9(int week9) {
            this.week9 = week9;
        }

        public int getWeek10() {
            return week10;
        }

        public void setWeek10(int week10) {
            this.week10 = week10;
        }

        public int getWeek11() {
            return week11;
        }

        public void setWeek11(int week11) {
            this.week11 = week11;
        }

        public int getWeek12() {
            return week12;
        }

        public void setWeek12(int week12) {
            this.week12 = week12;
        }

        public int getMonth3() {
            return month3;
        }

        public void setMonth3(int month3) {
            this.month3 = month3;
        }

        public int getQuarter1() {
            return quarter1;
        }

        public void setQuarter1(int quarter1) {
            this.quarter1 = quarter1;
        }
    }
}
