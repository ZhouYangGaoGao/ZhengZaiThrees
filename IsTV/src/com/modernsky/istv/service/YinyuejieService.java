package com.modernsky.istv.service;

import com.modernsky.istv.bean.Data;

/**
 * @author rendy 首页逻辑数据处理
 */
public class YinyuejieService {
    private static YinyuejieService LOGIC;
    // 广告数据
    private Data advertisementsDatas;
    private Data dujiaData;

    private YinyuejieService() {
    }

    public static YinyuejieService getInstance() {
        return LOGIC = LOGIC == null ? new YinyuejieService() : LOGIC;
    }

    public Data getAdvertisementsDatas() {
        return advertisementsDatas;
    }

    public void setAdvertisementsDatas(Data advertisementsDatas) {
        this.advertisementsDatas = advertisementsDatas;
    }

    public void setDujiaData(Data dujiaData) {
        this.dujiaData = dujiaData;
    }

    public Data getDujiaData() {
        return dujiaData;
    }

    public boolean isHaveDate() {
        return dujiaData != null || advertisementsDatas != null;
    }
}
