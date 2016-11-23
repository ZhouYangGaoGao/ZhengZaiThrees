package com.modernsky.istv.service;

import com.modernsky.istv.bean.Data;

/**
 * @author rendy 首页逻辑数据处理
 */
public class FirstPageService {
    private static FirstPageService LOGIC;
    // 广告数据
    private Data advertisementsDatas;
    private Data yuyueDatas;
    private Data dujiaData;
    private Data moreDatas;

    private FirstPageService() {
    }

    public static FirstPageService getInstance() {
        return LOGIC = LOGIC == null ? new FirstPageService() : LOGIC;
    }

    public Data getAdvertisementsDatas() {
        return advertisementsDatas;
    }

    public void setAdvertisementsDatas(Data advertisementsDatas) {
        this.advertisementsDatas = advertisementsDatas;
    }

    public void setMoreDatas(Data moreDatas) {
        this.moreDatas = moreDatas;
    }

    public void setYuyueDatas(Data yuyueDatas) {
        this.yuyueDatas = yuyueDatas;
    }

    public void setDujiaData(Data dujiaData) {
        this.dujiaData = dujiaData;
    }

    public Data getYuyueDatas() {
        return yuyueDatas;
    }

    public Data getDujiaData() {
        return dujiaData;
    }

    public Data getMoreDatas() {
        return moreDatas;
    }

    public boolean isHaveDujiaDate() {
        return dujiaData != null;
    }

    public boolean isHaveDate() {
        return moreDatas != null || dujiaData != null
                || advertisementsDatas != null || yuyueDatas != null;
    }
}
