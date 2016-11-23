/**
 *
 */
package com.modernsky.istv.bean;

import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-4 下午3:28:52
 * @类说明：专辑详情中的最后一个视频信息
 */
public class LatestVideo {
    private List<LiveInfo> liveInfo;
    private String name;
    private String starringNames;
    private String showTime = "0";
    private String standardPic;
    private String chatroomId;
    private String isVRsource;
    private Album album;
    private int videoId;
    private String endTime;
    private int albumId;
    private int isPay;
    private int isNeedPay;
    private int videoType;
    private VideoPlayInfo videoPlayInfo;
    private Ticket throughTicket;
    private Ticket singleTicket;
    private List<FormWuTaiInfo> catalog;
    private String location;
    private String description;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public VideoPlayInfo getVideoPlayInfo() {
        return videoPlayInfo;
    }

    public void setVideoPlayInfo(VideoPlayInfo videoPlayInfo) {
        this.videoPlayInfo = videoPlayInfo;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandardPic() {
        return standardPic;
    }

    public void setStandardPic(String standardPic) {
        this.standardPic = standardPic;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    public int getIsNeedPay() {
        return isNeedPay;
    }

    public void setIsNeedPay(int isNeedPay) {
        this.isNeedPay = isNeedPay;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<LiveInfo> getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(List<LiveInfo> liveInfo) {
        this.liveInfo = liveInfo;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getStarringNames() {
        return starringNames;
    }

    public void setStarringNames(String starringNames) {
        this.starringNames = starringNames;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Ticket getThroughTicket() {
        return throughTicket;
    }

    public void setThroughTicket(Ticket throughTicket) {
        this.throughTicket = throughTicket;
    }

    public Ticket getSingleTicket() {
        return singleTicket;
    }

    public void setSingleTicket(Ticket singleTicket) {
        this.singleTicket = singleTicket;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public List<FormWuTaiInfo> getCatalog() {
        return catalog;
    }

    public void setCatalog(List<FormWuTaiInfo> catalog) {
        this.catalog = catalog;
    }

    public String getIsVRsource() {
        return isVRsource;
    }

    public void setIsVRsource(String isVRsource) {
        this.isVRsource = isVRsource;
    }
}
