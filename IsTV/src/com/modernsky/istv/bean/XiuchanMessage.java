package com.modernsky.istv.bean;

/**
 * @author rendy 秀场消息封装
 */
public class XiuchanMessage extends BaseBean {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int type;    //  3 小礼物  4 大礼物    9  赞
    private String fromUserName;
    private String fromUserId;
    private String fromUserPic;
    private String toUserId;
    private String giftUserId = "";
    private String giftUserName = "";
    private String toUserName;
    private String msg;
    private int msgId;
    private int count;
    private long buildTime;
    private int giftPrice;
    private String pic;
    private String time;
    private String giftId;
    private String gifImgUrl;
    private long onceTime;
    private long addTime;
    private long praiseCount;
    private long strawCount;
    private long mbCount;
    private long exper;

    public long getExper() {
        return exper;
    }

    public void setExper(long exper) {
        this.exper = exper;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getOnceTime() {
        return onceTime;
    }

    public void setOnceTime(long onceTime) {
        this.onceTime = onceTime;
    }

    public long getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(long praiseCount) {
        this.praiseCount = praiseCount;
    }

    public long getStrawCount() {
        return strawCount;
    }

    public void setStrawCount(long strawCount) {
        this.strawCount = strawCount;
    }

    public long getMbCount() {
        return mbCount;
    }

    public void setMbCount(long mbCount) {
        this.mbCount = mbCount;
    }


    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGifImgUrl() {
        return gifImgUrl;
    }

    public void setGifImgUrl(String gifImgUrl) {
        this.gifImgUrl = gifImgUrl;
    }

    public String getFromUserPic() {
        return fromUserPic;
    }

    public void setFromUserPic(String fromUserPic) {
        this.fromUserPic = fromUserPic;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof XiuchanMessage)) {
            return false;
        }
        XiuchanMessage temp = (XiuchanMessage) other;
        if (getMsgId() == temp.getMsgId()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return msgId;
    }

    @Override
    public String toString() {
        return "XiuchanMessage [type=" + type //
                + ", fromUserId=" + fromUserId//
                + ", fromUserName=" + fromUserName//
                + ", toUserId=" + toUserId //
                + ", toUserName=" + toUserName//
                + ", msg=" + msg//
                + ", msgId=" + msgId//
                + ", buildTime=" + buildTime //
                + ", pic=" + pic//
                + ", time=" + time //
                + ", oncetime=" + onceTime //
                + ", gifeid=" + giftId //
                + ", gifturl=" + gifImgUrl //
                + ", pic=" + pic //
                + ", addtime=" + addTime //
                + "]";
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return 消息类型 1.普通消息 2.系统纯文字通知 3.聊天列表通知 4.顶部和底部都显示通知
     */
    public int getType() {
        return type;
    }

    /**
     * @return 消息类型 1.普通消息 2.系统纯文字通知 3.小礼物 4.大礼物  5 回复  6结束消息  7 刷新主播数据 8 进入聊天室 9 赞
     */
    public void setType(int type) {
        this.type = type;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public String getGiftUserId() {
        return giftUserId;
    }

    public void setGiftUserId(String giftUserId) {
        this.giftUserId = giftUserId;
    }

    public String getGiftUserName() {
        return giftUserName;
    }

    public void setGiftUserName(String giftUserName) {
        this.giftUserName = giftUserName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(int giftPrice) {
        this.giftPrice = giftPrice;
    }

}
