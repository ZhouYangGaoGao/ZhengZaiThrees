package com.modernsky.istv.service;

import com.modernsky.istv.bean.Data;

/**
 * @author rendy 直播数据缓存
 */
public class ShowListService {
    private static ShowListService LOGIC;
    // 广告数据
    private Data advertisementsDatas;
    private Data dujiaData;

    private ShowListService() {
    }

    public static ShowListService getInstance() {
        return LOGIC = LOGIC == null ? new ShowListService() : LOGIC;
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
