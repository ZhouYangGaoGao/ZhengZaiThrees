/**
 *
 */
package com.modernsky.istv.bean;

import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-15 下午3:18:17
 * @类说明： 用户信息实体类
 */
public class UserEntity extends BaseBean {


    /**
     * activation : 0
     * address :
     * attentionCount : 0
     * attentionTime : 0
     * badge : {"add":0,"count":0,"grayImgUrl":"","imgUrl":"","isSpecial":0,"maxRank":3,"minRank":1,"name":"小学生",
     * "type":1}
     * birthday :
     * buildTime : 1457080157010
     * email :
     * exper : 0
     * faceUrl : http://pic.zhengzai.tv/default_h.png
     * fansCount : 0
     * giftCount : 0
     * id : 56d9475de4b06c3f2c92cfa7
     * isAttention : 0
     * isReplySend : 1
     * lastLoginTime : 1457080157010
     * lastUpdateTime : 1457080157010
     * mbCount : 0
     * mobile : 15010374543
     * openUser : []
     * praiseCount : 0
     * rank : {"buildTime":0,"grayImgUrl":"","imgUrl":"http://img.zhengzai
     * .tv/common/album/20160302/1456902522449_87x54.png","isHave":1,"maxValue":100,"minValue":0,"name":"","rank":1,
     * "type":1}
     * ranking : 0
     * region :
     * rongcloudToken : 8Tih1SkxaALquPQEhh8gfwxTdAbkl+yGmRb7BZyM5D02X44S
     * /r4m6tN2FEsQ6zFt1B25zsyz0y859JRJXcQw5IWcmNLIYaiIzJ/Yui91hMR2ceeBpFSGcw
     * sex : -1
     * sign :
     * status : 1
     * strawCount : 0
     * userName : zz_6ke1eq6ev6
     */

    private String address;
    private long attentionTime;
    private int attentionCount;
    private int videoCount;
    private Video video;

    /**
     * add : 0
     * count : 0
     * grayImgUrl :
     * imgUrl :
     * isSpecial : 0
     * maxRank : 3
     * minRank : 1
     * name : 小学生
     * type : 1
     */

    private BadgeEntity badge;
    private String birthday;
    private long buildTime;
    private String email;
    private long exper;
    private String faceUrl;
    private int fansCount;
    private int giftCount;
    private String id;
    private int isAttention;
    private int isReplySend;
    private long lastLoginTime;
    private long lastUpdateTime;
    private int mbCount;
    private String mobile;
    private int praiseCount;

    //zwz 排行的图标
    private int positionImage;

    public void setPositionImage(int positionImage) {
        this.positionImage = positionImage;
    }

    public int getPositionImage() {
        return positionImage;
    }


    /**
     * buildTime : 0
     * grayImgUrl :
     * imgUrl : http://img.zhengzai.tv/common/album/20160302/1456902522449_87x54.png
     * isHave : 1
     * maxValue : 100
     * minValue : 0
     * <p/>
     * name :
     * rank : 1
     * type : 1
     */

    private RankEntity rank;
    private int ranking;
    private String region;
    private String rongcloudToken;
    private int sex;//1:男,0:女, -1:保密
    private String sign;
    private int status;


    public Herald getHerald() {
        return herald;
    }

    public void setHerald(Herald herald) {
        this.herald = herald;
    }

    private int strawCount;
    private String userName;
    private Herald herald;//预告/直播信息   没有预告的时候为空
    private List<OpenInfoBean> openUser;

    public void setAddress(String address) {
        this.address = address;
    }


    public void setAttentionCount(int attentionCount) {
        this.attentionCount = attentionCount;
    }

    public void setAttentionTime(long attentionTime) {
        this.attentionTime = attentionTime;
    }

    public void setBadge(BadgeEntity badge) {
        this.badge = badge;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExper(long exper) {
        this.exper = exper;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public void setIsReplySend(int isReplySend) {
        this.isReplySend = isReplySend;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setMbCount(int mbCount) {
        this.mbCount = mbCount;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public void setRank(RankEntity rank) {
        this.rank = rank;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setRongcloudToken(String rongcloudToken) {
        this.rongcloudToken = rongcloudToken;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStrawCount(int strawCount) {
        this.strawCount = strawCount;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOpenUser(List<OpenInfoBean> openUser) {
        this.openUser = openUser;
    }


    public String getAddress() {
        return address;
    }

    public int getAttentionCount() {
        return attentionCount;
    }

    public long getAttentionTime() {
        return attentionTime;
    }

    public BadgeEntity getBadge() {
        return badge;
    }

    public String getBirthday() {
        return birthday;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public String getEmail() {
        return email;
    }

    public long getExper() {
        return exper;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public int getFansCount() {
        return fansCount;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public String getId() {
        return id;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public int getIsReplySend() {
        return isReplySend;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public int getMbCount() {
        return mbCount;
    }

    public String getMobile() {
        return mobile;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public RankEntity getRank() {
        return rank;
    }

    public int getRanking() {
        return ranking;
    }

    public String getRegion() {
        return region;
    }

    public String getRongcloudToken() {
        return rongcloudToken;
    }

    public int getSex() {
        return sex;
    }

    public String getSign() {
        return sign;
    }

    public int getStatus() {
        return status;
    }

    public int getStrawCount() {
        return strawCount;
    }

    public String getUserName() {
        return userName;
    }

    public List<OpenInfoBean> getOpenUser() {
        return openUser;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public static class BadgeEntity extends BaseBean {
        private int add;
        private int count;
        private String grayImgUrl;
        private String imgUrl;
        private int isSpecial;
        private int maxRank;
        private int minRank;
        private String name;
        private int type;

        public void setAdd(int add) {
            this.add = add;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setGrayImgUrl(String grayImgUrl) {
            this.grayImgUrl = grayImgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public void setIsSpecial(int isSpecial) {
            this.isSpecial = isSpecial;
        }

        public void setMaxRank(int maxRank) {
            this.maxRank = maxRank;
        }

        public void setMinRank(int minRank) {
            this.minRank = minRank;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getAdd() {
            return add;
        }

        public int getCount() {
            return count;
        }

        public String getGrayImgUrl() {
            return grayImgUrl;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public int getIsSpecial() {
            return isSpecial;
        }

        public int getMaxRank() {
            return maxRank;
        }

        public int getMinRank() {
            return minRank;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }
    }

    public static class Herald extends BaseBean {
        private String id;
        private String name;
        private String type;
        private String videoId;
        private String posterImg;
        private String url;
        private long startTime;
        private long endTime;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String userId;
        private String location;
        private String liveProfile;
        private String chatroomId;

        public String getChatroomId() {
            return chatroomId;
        }

        public void setChatroomId(String chatroomId) {
            this.chatroomId = chatroomId;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getPosterImg() {
            return posterImg;
        }

        public void setPosterImg(String posterImg) {
            this.posterImg = posterImg;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLiveProfile() {
            return liveProfile;
        }

        public void setLiveProfile(String liveProfile) {
            this.liveProfile = liveProfile;
        }
    }

    public static class RankEntity extends BaseBean {
        private long buildTime;
        private String grayImgUrl;
        private String imgUrl;
        private int isHave;
        private int maxValue = 1;
        private int minValue;
        private String name;
        private String rank;
        private int type;

        public void setBuildTime(long buildTime) {
            this.buildTime = buildTime;
        }

        public void setGrayImgUrl(String grayImgUrl) {
            this.grayImgUrl = grayImgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public void setIsHave(int isHave) {
            this.isHave = isHave;
        }

        public void setMaxValue(int maxValue) {
            this.maxValue = maxValue;
        }

        public void setMinValue(int minValue) {
            this.minValue = minValue;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getBuildTime() {
            return buildTime;
        }

        public String getGrayImgUrl() {
            return grayImgUrl;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public int getIsHave() {
            return isHave;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public int getMinValue() {
            return minValue;
        }

        public String getName() {
            return name;
        }

        public String getRank() {
            return rank;
        }

        public int getType() {
            return type;
        }
    }
}
