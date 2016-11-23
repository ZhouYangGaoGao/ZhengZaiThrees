package com.modernsky.istv.service;

import java.util.List;

import com.modernsky.istv.bean.GiftBean;

/**
 * @author rendy 秀场数据处理
 */
public class XiuchangService {

    private XiuchangService() {
    }

    static XiuchangService LOGIC;

    public static XiuchangService getInstance() {
        return LOGIC = LOGIC == null ? new XiuchangService() : LOGIC;
    }

    private List<GiftBean> dataGift;

    public List<GiftBean> getDataGift() {
        return dataGift;
    }

    public void setDataGift(List<GiftBean> dataGift) {
        this.dataGift = dataGift;
    }

}
