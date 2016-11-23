package com.modernsky.istv.bean;

import java.util.List;

/**
 * Created by zhengzai_zxm on 16/3/9.
 */
public class TranscriptsBean extends BaseBean {


    /**
     * exper : 0
     * isExceed : 1
     * mbCount : 0
     * praiseCount : 0
     * strawCount : 0
     * viewCount : 0
     */

    private int exper = 0;
    private int isExceed;
    private int mbCount = 0;
    private int praiseCount = 0;
    private int strawCount = 0;
    private String videoId;
    private int viewCount = 0;
    private List<PaihangBean> rank;

    public void setExper(int exper) {
        this.exper = exper;
    }

    public void setIsExceed(int isExceed) {
        this.isExceed = isExceed;
    }

    public void setMbCount(int mbCount) {
        this.mbCount = mbCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public void setStrawCount(int strawCount) {
        this.strawCount = strawCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getExper() {
        return exper;
    }

    public int getIsExceed() {
        return isExceed;
    }

    public int getMbCount() {
        return mbCount;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public int getStrawCount() {
        return strawCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public List<PaihangBean> getRank() {
        return rank;
    }

    public void setRank(List<PaihangBean> rank) {
        this.rank = rank;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

}
