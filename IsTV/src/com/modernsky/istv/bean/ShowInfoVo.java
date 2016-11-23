/**
 * @Copyright 北京正在映画科技有限公司
 */
package com.modernsky.istv.bean;

/**
 * @ClassName: ShowInfoVo
 * @Description: 演出信息
 * @author: lixin
 * @date: 2015年10月29日 下午2:53:03
 */
public class ShowInfoVo extends BaseBean {
    private String albumId;// 专辑ID
    private String albumName;// 专辑名称
    private String videoId;// 视频ID
    private String videoName;// 视频名称
    private String startDate;// 开始日期 2015-09-20
    private String endDate;// 结束日期 2015-09-22
    private String today;// 今日 2015-09-21
    private String todaytime;// 今日时间起止时间 am10:00-pm23:30
    /**
     * 预约状态：0:表示没有预约 1：表示本天已经预约 2：表示已经预约全场所有天 3:表示已结束
     */
    private String opointStatus;
    private String isPay;//是否已经支付 0 未付费 1 已经付费

    @Override
    public String toString() {
        return "ShowInfoVo [albumId=" + albumId + ", albumName=" + albumName
                + ", videoId=" + videoId + ", videoName=" + videoName
                + ", startDate=" + startDate + ", endDate=" + endDate
                + ", today=" + today + ", todaytime=" + todaytime
                + ", opointStatus=" + opointStatus + ", isPay=" + isPay + "]";
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTodaytime() {
        return todaytime;
    }

    public void setTodaytime(String todaytime) {
        this.todaytime = todaytime;
    }

    public String getOpointStatus() {
        return opointStatus;
    }

    public void setOpointStatus(String opointStatus) {
        this.opointStatus = opointStatus;
    }

    public String getIsPay() {
        return isPay;
    }

    public void setIsPay(String isPay) {
        this.isPay = isPay;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

}
