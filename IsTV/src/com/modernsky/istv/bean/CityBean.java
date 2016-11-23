package com.modernsky.istv.bean;


/**
 * Created by zhengzai_zxm on 16/3/12.
 */
public class CityBean extends BaseBean {
    private String cityId;
    private String cityName;

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }
}
