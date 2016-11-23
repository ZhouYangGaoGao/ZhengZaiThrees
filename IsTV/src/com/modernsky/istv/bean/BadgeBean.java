package com.modernsky.istv.bean;

public class BadgeBean extends BaseBean {
    private String id;
    private String name;
    private String imgUrl;
    private String grayImgUrl;
    private int sort_;
    private int type;
    private int data;
    private int isHave;
    private String remark;
    private int disPlay;
    private long buildTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGrayImgUrl() {
        return grayImgUrl;
    }

    public void setGrayImgUrl(String grayImgUrl) {
        this.grayImgUrl = grayImgUrl;
    }

    public int getSort_() {
        return sort_;
    }

    public void setSort_(int sort_) {
        this.sort_ = sort_;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getIsHave() {
        return isHave;
    }

    public void setIsHave(int isHave) {
        this.isHave = isHave;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getDisPlay() {
        return disPlay;
    }

    public void setDisPlay(int disPlay) {
        this.disPlay = disPlay;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

}
