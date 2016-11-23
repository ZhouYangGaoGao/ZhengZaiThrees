package com.modernsky.istv.service;

import java.util.List;

import com.modernsky.istv.bean.Data;
import com.modernsky.istv.bean.TempmMuiscBean;

/**
 * @author rendy 排行业务处理
 */
public class YinyuejiePageService {
    private static YinyuejiePageService LOGIC;
    // 广告数据
    private Data advertisementsDatas;
    private List<TempmMuiscBean> yuyueDatas;

    public List<TempmMuiscBean> getYuyueDatas() {
        return yuyueDatas;
    }

    public void setYuyueDatas(List<TempmMuiscBean> yuyueDatas) {
        this.yuyueDatas = yuyueDatas;
    }

    private YinyuejiePageService() {
    }

    public static YinyuejiePageService getInstance() {
        return LOGIC = LOGIC == null ? new YinyuejiePageService() : LOGIC;
    }

    public Data getAdvertisementsDatas() {
        return advertisementsDatas;
    }

    public void setAdvertisementsDatas(Data advertisementsDatas) {
        this.advertisementsDatas = advertisementsDatas;
    }

    public boolean isHaveDate() {
        return advertisementsDatas != null || yuyueDatas != null;
    }

}
