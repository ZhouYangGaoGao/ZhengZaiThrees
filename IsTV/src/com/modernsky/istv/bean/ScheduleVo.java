/**
 * @Copyright 北京正在映画科技有限公司
 */
package com.modernsky.istv.bean;

import java.util.List;


/**
 * @ClassName: ScheduleVo
 * @Description: 日程
 * @author: lixin
 * @date: 2015年10月29日 下午2:49:21
 */
public class ScheduleVo extends BaseBean {
    private String date;//日期
    private List<ShowInfoVo> data;//演出信息

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ShowInfoVo> getData() {
        return data;
    }

    public void setData(List<ShowInfoVo> data) {
        this.data = data;
    }

}
