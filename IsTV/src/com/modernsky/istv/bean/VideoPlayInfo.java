/**
 *
 */
package com.modernsky.istv.bean;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-4 下午3:22:29
 * @类说明：视频播放信息
 */
public class VideoPlayInfo extends BaseBean {
    private int videoId;
    private String foreignVid;
    private String foreignUnique;
    private String videoUrl;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getForeignUnique() {
        return foreignUnique;
    }

    public void setForeignUnique(String foreignUnique) {
        this.foreignUnique = foreignUnique;
    }

    public String getForeignVid() {
        return foreignVid;
    }

    public void setForeignVid(String foreignVid) {
        this.foreignVid = foreignVid;
    }

}
