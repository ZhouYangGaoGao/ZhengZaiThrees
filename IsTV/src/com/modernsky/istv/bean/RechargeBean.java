package com.modernsky.istv.bean;

/**
 * @author rendy 秀场套餐列表
 */
public class RechargeBean extends BaseBean {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String imgUrl;
    private String money;
    private String mb;
    private String giveMB;
    private int type;
    private int canBuy;
    private String remark;
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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMb() {
        return mb;
    }

    public void setMb(String mb) {
        this.mb = mb;
    }

    public String getGiveMB() {
        return giveMB;
    }

    public void setGiveMB(String giveMB) {
        this.giveMB = giveMB;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(int canBuy) {
        this.canBuy = canBuy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

}
