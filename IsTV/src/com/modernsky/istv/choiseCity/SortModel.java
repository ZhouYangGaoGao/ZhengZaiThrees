package com.modernsky.istv.choiseCity;

import com.modernsky.istv.bean.BaseBean;

public class SortModel extends BaseBean {

    private String cityName;   //显示的数�?
    private String sortLetters;  //显示数据拼音的首字母
    private String cityId;  //显示数据拼音的首字母

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
