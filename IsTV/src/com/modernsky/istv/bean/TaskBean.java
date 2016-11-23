package com.modernsky.istv.bean;

/**
 * @author mufaith
 */
public class TaskBean extends BaseBean {


    /**
     * isCan : 0
     * isDone : 1
     * mbCount : 0
     * title : 每日签到 送1个草莓
     * type : 3
     */

    private int isCan;
    private int isDone;
    private int mbCount;
    private String title;
    private int type;

    public void setIsCan(int isCan) {
        this.isCan = isCan;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    public void setMbCount(int mbCount) {
        this.mbCount = mbCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsCan() {
        return isCan;
    }

    public int getIsDone() {
        return isDone;
    }

    public int getMbCount() {
        return mbCount;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }
}
