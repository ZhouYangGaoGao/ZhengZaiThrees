package com.modernsky.istv.service;

import com.modernsky.istv.bean.Data;

/**
 * @author rendy 直播数据缓存
 */
public class ZhiboPageService {
    private static ZhiboPageService LOGIC;
    // 广告数据
    private Data advertisementsDatas;
    private Data dujiaData;

    private ZhiboPageService() {
    }

    public static ZhiboPageService getInstance() {
        return LOGIC = LOGIC == null ? new ZhiboPageService() : LOGIC;
    }

    public Data getAdvertisementsDatas() {
        return advertisementsDatas;
    }

    public void setAdvertisementsDatas(Data advertisementsDatas) {
        this.advertisementsDatas = advertisementsDatas;
    }

    public Data getDujiaData() {
        return dujiaData;
    }

    public void setDujiaData(Data dujiaData) {
        this.dujiaData = dujiaData;
    }

    public boolean isHaveDate() {
        return dujiaData != null || advertisementsDatas != null;
    }
}
