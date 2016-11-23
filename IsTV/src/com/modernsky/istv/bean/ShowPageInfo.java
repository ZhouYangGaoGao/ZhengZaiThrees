package com.modernsky.istv.bean;

import java.util.List;

/**
 * Created by zqg on 2016/3/9.
 */
public class ShowPageInfo {

    /**
     * group : 1
     * isdisplay : 1
     * total : 1
     * data : [{"type":"1","url":"是打发","infocode":"是打发","userId":"2","videoId":"4","pic":"http://img.zhengzai.tv/common/videoDetail/20160308/1457446901275_199x197.png"}]
     */

    private String group;
    private String isdisplay;
    private int total;
    /**
     * type : 1
     * url : 是打发
     * infocode : 是打发
     * userId : 2
     * videoId : 4
     * pic : http://img.zhengzai.tv/common/videoDetail/20160308/1457446901275_199x197.png
     */

    private List<ShowPageItemInfo> data;

    public void setGroup(String group) {
        this.group = group;
    }

    public void setIsdisplay(String isdisplay) {
        this.isdisplay = isdisplay;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setData(List<ShowPageItemInfo> data) {
        this.data = data;
    }

    public String getGroup() {
        return group;
    }

    public String getIsdisplay() {
        return isdisplay;
    }

    public int getTotal() {
        return total;
    }

    public List<ShowPageItemInfo> getData() {
        return data;
    }
}
