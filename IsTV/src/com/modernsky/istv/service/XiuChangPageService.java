package com.modernsky.istv.service;

import com.modernsky.istv.bean.ShowPageInfo;

import java.util.List;

/**
 * @author rendy 直播数据缓存
 */
public class XiuChangPageService {
    private static XiuChangPageService LOGIC;
    private List<ShowPageInfo> showPageInfos;

    private XiuChangPageService() {
    }

    public List<ShowPageInfo> getShowPageInfos() {
        return showPageInfos;
    }

    public void setShowPageInfos(List<ShowPageInfo> showPageInfos) {
        this.showPageInfos = showPageInfos;
    }

    public static XiuChangPageService getInstance() {
        return LOGIC = LOGIC == null ? new XiuChangPageService() : LOGIC;
    }

    public boolean isHasData() {
        if (showPageInfos != null)
            return true;
        return false;
    }
}
