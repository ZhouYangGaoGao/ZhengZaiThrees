/**
 *
 */
package com.modernsky.istv.bean;

import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-17 上午11:42:08
 * @类说明：
 */
public class ShowBean extends BaseBean {
    /**
     *
     */
    private static final long serialVersionUID = 1302874834255422228L;
    private String name;
    private String videoId;
    private String endTime;
    private String chatroomId;
    private List<LiveInfo> liveInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public List<LiveInfo> getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(List<LiveInfo> liveInfo) {
        this.liveInfo = liveInfo;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }
}
