package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/14.
 */
public class AnchorChatroomInfo extends BaseBean{

    /**
     * buildTime : 1457086653783
     * chatroomId : 56d960bde4b06c3f2c92d01b
     * exper : 0
     * mbCount : 0
     * name :
     * praiseCount : 0
     * remark :
     * status : 3
     * strawCount : 0
     * type : 0
     * userId :
     * videoId : 945
     * viewCount : 0
     */

    private int needStraw;  // 出事聊天室 增加时长需要的草莓



    private long addTime;  // 出事聊天室 增加时长需要的草莓
    private long canAddTime;  //初始聊天室 可以增加市场
    private int strawCount;

    private int isCan;       //初始化之前 判断主播是否可以开始直播
    private long buildTime;
    private String chatroomId;
    private int exper;
    private int mbCount;
    private String name;
    private int praiseCount;
    private String remark;
    private int status;
    private int type;
    private String userId;
    private int videoId;
    private int viewCount;

    public String getVids() {
        return vids;
    }

    public void setVids(String vids) {
        this.vids = vids;
    }

    private String vids;
    private String location;
    private UserEntity user;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
    public int getNeedStraw() {
        return needStraw;
    }

    public void setNeedStraw(int needStraw) {
        this.needStraw = needStraw;
    }

    public long getCanAddTime() {
        return canAddTime;
    }

    public void setCanAddTime(long canAddTime) {
        this.canAddTime = canAddTime;
    }

    public int getIsCan() {
        return isCan;
    }

    public void setIsCan(int isCan) {
        this.isCan = isCan;
    }



    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }



    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public void setExper(int exper) {
        this.exper = exper;
    }

    public void setMbCount(int mbCount) {
        this.mbCount = mbCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStrawCount(int strawCount) {
        this.strawCount = strawCount;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public int getExper() {
        return exper;
    }

    public int getMbCount() {
        return mbCount;
    }

    public String getName() {
        return name;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public String getRemark() {
        return remark;
    }

    public int getStatus() {
        return status;
    }

    public int getStrawCount() {
        return strawCount;
    }

    public int getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public int getVideoId() {
        return videoId;
    }

    public int getViewCount() {
        return viewCount;
    }
}
