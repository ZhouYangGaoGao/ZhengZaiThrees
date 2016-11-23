package com.modernsky.istv.bean;

/**
 * @author rendy 秀场礼物bean
 */
public class GiftBean extends BaseBean {
    private String id;
    private String name;
    private String imgUrl;
    private int mb;
    private int type;
    private String count;
    private int userHaveCount;
    private String canBuy;
    private String remark;
    private String buildTime;
    private String gifImgUrl;
    public String getGifImgUrl() {
        return gifImgUrl;
    }

    public void setGifImgUrl(String gifImgUrl) {
        this.gifImgUrl = gifImgUrl;
    }
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

    public int getMb() {
        return mb;
    }

    public void setMb(int mb) {
        this.mb = mb;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getUserHaveCount() {
        return userHaveCount;
    }

    public void setUserHaveCount(int userHaveCount) {
        this.userHaveCount = userHaveCount;
    }

    public String getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(String canBuy) {
        this.canBuy = canBuy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

}
