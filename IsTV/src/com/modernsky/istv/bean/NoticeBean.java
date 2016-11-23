package com.modernsky.istv.bean;

/**
 * Created by FQY on 16/3/3.
 */
public class NoticeBean extends BaseBean {


    /**
     * id : 5
     * content : 马頔来啦
     * title : 马頔
     * type : 1
     * status : 1
     * isRead : 0
     * buildTime : 5
     */

    private String id;
    private String content;
    private String title;
    private int type;
    private int status;
    private int isRead;
    private long buildTime;

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public int getIsRead() {
        return isRead;
    }

    public long getBuildTime() {
        return buildTime;
    }
}
