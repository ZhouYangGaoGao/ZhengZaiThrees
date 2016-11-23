package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/7.
 */
public class ApplyVideoInfo extends BaseBean {

    /**
     * videoId : 241
     * pic : http://img.zhengzai.tv/common/video/20150623/e895f8e3bb2df580cdcf23a4012b4fc3_original.jpg
     * name : 2015武汉-万能青年旅店-秦皇岛
     * videoPlayInfo : {"duration":"402","foreignUnique":"f361f76e4d","foreignVid":"15595858","fsize":"1264897242","md5":"","thumbnail":"http://i0.letvimg.com/lc03_yunzhuanma/201509/06/22/31/18e3ce37f67b174129dd0b780792fe71_34513167/thumb/2.jpg","type":"1","videoId":241,"videoUrl":"f361f76e4d"}
     */

    private int videoId;
    private String pic;
    private String name;
    /**
     * duration : 402
     * foreignUnique : f361f76e4d
     * foreignVid : 15595858
     * fsize : 1264897242
     * md5 :
     * thumbnail : http://i0.letvimg.com/lc03_yunzhuanma/201509/06/22/31/18e3ce37f67b174129dd0b780792fe71_34513167/thumb/2.jpg
     * type : 1
     * videoId : 241
     * videoUrl : f361f76e4d
     */

    private VideoPlayInfoEntity videoPlayInfo;

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVideoPlayInfo(VideoPlayInfoEntity videoPlayInfo) {
        this.videoPlayInfo = videoPlayInfo;
    }

    public int getVideoId() {
        return videoId;
    }

    public String getPic() {
        return pic;
    }

    public String getName() {
        return name;
    }

    public VideoPlayInfoEntity getVideoPlayInfo() {
        return videoPlayInfo;
    }

    public static class VideoPlayInfoEntity {
        private String duration;
        private String foreignUnique;
        private String foreignVid;
        private String fsize;
        private String md5;
        private String thumbnail;
        private String type;
        private int videoId;
        private String videoUrl;

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public void setForeignUnique(String foreignUnique) {
            this.foreignUnique = foreignUnique;
        }

        public void setForeignVid(String foreignVid) {
            this.foreignVid = foreignVid;
        }

        public void setFsize(String fsize) {
            this.fsize = fsize;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setVideoId(int videoId) {
            this.videoId = videoId;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getDuration() {
            return duration;
        }

        public String getForeignUnique() {
            return foreignUnique;
        }

        public String getForeignVid() {
            return foreignVid;
        }

        public String getFsize() {
            return fsize;
        }

        public String getMd5() {
            return md5;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public String getType() {
            return type;
        }

        public int getVideoId() {
            return videoId;
        }

        public String getVideoUrl() {
            return videoUrl;
        }
    }
}
