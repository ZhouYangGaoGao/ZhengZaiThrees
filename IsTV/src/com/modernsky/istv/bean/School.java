package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/7.
 */
public class School extends BaseBean {


    /**
     * schoolId : 1441
     * schoolName : 星海音乐学院
     * cityId : 172
     * cityName : 广州市
     */

    private String schoolId;
    private String schoolName;
    private String cityId;
    private String cityName;

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }
}
