package com.modernsky.istv.bean;


/**
 * @author rendy
 *         <p/>
 *         展示图片的信息
 */
public class FocusPictureModel extends BaseBean {
    private String name;
    private String type;
    private String category;
    private String pic;
    private String albumId;
    private String videoId;
    private String objectId;
    private String userId;
    private int width;
    private int height;
    private String url;
    private long showtime;
    private int isSubscribe;
    private String duration;
    private String onlineCount;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getShowtime() {
        return showtime;
    }

    public void setShowtime(long showtime) {
        this.showtime = showtime;
    }

    public int getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(int isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(String onlineCount) {
        this.onlineCount = onlineCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
