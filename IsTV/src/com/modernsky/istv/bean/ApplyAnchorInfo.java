package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/7.
 */
public class ApplyAnchorInfo extends BaseBean {


    /**
     * userId : 56d7f200e4b05c1e689c51c3
     * faceUrl : http://pic.zhengzai.tv/479892738.868005CC24EBE96832E878915FB72E8461F97E.png
     * userName : 清清
     * islive : 0
     * remarks : 粉丝0
     * rank : {"buildTime":1458141652544,"grayImgUrl":"","imgUrl":"","isHave":1,"maxValue":1500000,"minValue":1000000,"name":"","rank":22,"type":2}
     */

    private String userId;
    private String faceUrl;
    private String userName;
    private String islive;
    private String remarks;
    /**
     * buildTime : 1458141652544
     * grayImgUrl :
     * imgUrl :
     * isHave : 1
     * maxValue : 1500000
     * minValue : 1000000
     * name :
     * rank : 22
     * type : 2
     */

    private RankEntity rank;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setIslive(String islive) {
        this.islive = islive;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setRank(RankEntity rank) {
        this.rank = rank;
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

    public String getIslive() {
        return islive;
    }

    public String getRemarks() {
        return remarks;
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
