package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/12.
 */
public class ShowAnchorItemInfo extends BaseBean {

    /**
     * type :
     * name :
     * videoId :
     * url :
     * pic :
     * islive : 0
     * remarks : 粉丝20W+
     * userId : 56d90609e4b06c3f2c92cf29
     * faceUrl : http://q.qlogo.cn/qqapp/1104373827/E02259C542608E65B9F99F5F83EC88A2/100
     * userName : zz_i9glhrft54
     * rank : {"buildTime":0,"grayImgUrl":"","imgUrl":"http://img.zhengzai.tv/common/album/20160302/1456902522449_87x54.png","isHave":1,"maxValue":1000,"minValue":100,"name":"","rank":2,"type":2}
     */

    private String type;
    private String name;
    private String videoId;
    private String url;
    private String pic;
    private String islive;
    private String remarks;
    private String userId;
    private String faceUrl;
    private String userName;
    /**
     * buildTime : 0
     * grayImgUrl :
     * imgUrl : http://img.zhengzai.tv/common/album/20160302/1456902522449_87x54.png
     * isHave : 1
     * maxValue : 1000
     * minValue : 100
     * name :
     * rank : 2
     * type : 2
     */

    private RankEntity rank;

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setIslive(String islive) {
        this.islive = islive;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRank(RankEntity rank) {
        this.rank = rank;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getUrl() {
        return url;
    }

    public String getPic() {
        return pic;
    }

    public String getIslive() {
        return islive;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getUserId() {
        return userId;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public String getUserName() {
        return userName;
    }

    public RankEntity getRank() {
        return rank;
    }

    public static class RankEntity {
        private long buildTime;
        private String grayImgUrl;
        private String imgUrl;
        private int isHave;
        private int maxValue;
        private int minValue;
        private String name;
        private int rank;
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

        public void setRank(int rank) {
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

        public int getRank() {
            return rank;
        }

        public int getType() {
            return type;
        }
    }
}
