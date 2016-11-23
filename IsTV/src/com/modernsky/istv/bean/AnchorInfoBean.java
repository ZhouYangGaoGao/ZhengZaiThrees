package com.modernsky.istv.bean;

import java.util.List;

/**
 * Created by zqg on 2016/3/6.
 */
public class AnchorInfoBean extends BaseBean {

    /**
     * buildTime : 1457159763828
     * city : 宁
     * dorm : 啊
     * id : 56da7e53e4b041085cd4fb7d
     * idImg : ["http://pic.zhengzai.tv/img_1457159577913_temp_photo.jpg","http://pic.zhengzai.tv/img_1457159588037_temp_photo.jpg","http://pic.zhengzai.tv/img_1457159598144_temp_photo.jpg","http://pic.zhengzai.tv/img_1457159609527_temp_photo.jpg"]
     * isPass : 0
     * major : 丽丽第非得
     * message :
     * personlProfile : 老婆婆您泥泞莫哦婆婆你
     * personlUrl : 里哦民工
     * posterImg : http://pic.zhengzai.tv/img_1457159523461_temp_photo.jpg
     * schoolId : 123
     * schoolName : 北京大学
     * userId : 56d7b0aee4b05b5cfa40534e
     * workCount : 2
     * works : ["24106049"]
     */

    private long buildTime;
    private String city;
    private String dorm;
    private String id;
    private int isPass;
    private String major;
    private String message;
    private String personlProfile;
    private String personlUrl;
    private String posterImg;
    private String schoolId;
    private String schoolName;
    private String userId;
    private int workCount;
    private List<String> idImg;
    private List<String> works;

    public void setBuildTime(long buildTime) {
        this.buildTime = buildTime;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDorm(String dorm) {
        this.dorm = dorm;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsPass(int isPass) {
        this.isPass = isPass;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPersonlProfile(String personlProfile) {
        this.personlProfile = personlProfile;
    }

    public void setPersonlUrl(String personlUrl) {
        this.personlUrl = personlUrl;
    }

    public void setPosterImg(String posterImg) {
        this.posterImg = posterImg;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWorkCount(int workCount) {
        this.workCount = workCount;
    }

    public void setIdImg(List<String> idImg) {
        this.idImg = idImg;
    }

    public void setWorks(List<String> works) {
        this.works = works;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public String getCity() {
        return city;
    }

    public String getDorm() {
        return dorm;
    }

    public String getId() {
        return id;
    }

    public int getIsPass() {
        return isPass;
    }

    public String getMajor() {
        return major;
    }

    public String getMessage() {
        return message;
    }

    public String getPersonlProfile() {
        return personlProfile;
    }

    public String getPersonlUrl() {
        return personlUrl;
    }

    public String getPosterImg() {
        return posterImg;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getUserId() {
        return userId;
    }

    public int getWorkCount() {
        return workCount;
    }

    public List<String> getIdImg() {
        return idImg;
    }

    public List<String> getWorks() {
        return works;
    }
}
