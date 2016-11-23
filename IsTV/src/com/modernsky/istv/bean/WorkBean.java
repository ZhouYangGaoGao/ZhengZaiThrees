package com.modernsky.istv.bean;

import android.support.annotation.Nullable;

/**
 * 主播作品bean
 *
 * @author mufaith
 */
public class WorkBean extends BaseBean {


    private String userId;
    @Nullable
    private String userName;
    private String faceUrl;
    private String url;
    private String location;
    private UserEntity.RankEntity rank;
    private long videoId = 0;
    private long viewCount = 0;
    private long onlineCount = 0;
    private long subscribeCount = 0;
    private long showTime = 0;
    private long endTime = 0;
    private int videoType;
    private String videoName;
    private String videoPic;
    private long albumId;
    private String albumName;

    private int isDisplay;

    private int praiseCount;

    public long getAlbumId() {
        return albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public int getIsDisplay() {
        return isDisplay;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public long getVideoId() {
        return videoId;
    }

    public long getViewCount() {
        return viewCount;
    }

    public long getOnlineCount() {
        return onlineCount;
    }

    public long getSubscribeCount() {
        return subscribeCount;
    }

    public long getShowTime() {
        return showTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getVideoType() {
        return videoType;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public void setOnlineCount(long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public void setSubscribeCount(long subscribeCount) {
        this.subscribeCount = subscribeCount;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserEntity.RankEntity getRank() {
        return rank;
    }

    public void setRank(UserEntity.RankEntity rank) {
        this.rank = rank;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setIsDisplay(int isDisplay) {
        this.isDisplay = isDisplay;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
