package com.modernsky.istv.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.modernsky.istv.bean.ShowInfoVo;

public class CalendarService {
    private static CalendarService LOGIC;

    public static CalendarService getInstance() {
        return LOGIC = LOGIC == null ? new CalendarService() : LOGIC;
    }

    private Map<String, List<ShowInfoVo>> map = new HashMap<String, List<ShowInfoVo>>();// 当前月
    private String year = Calendar.getInstance().get(Calendar.YEAR) + "";// 年
    private String month = Calendar.getInstance().get(Calendar.MONTH) + 1 + "";// 月

    public Map<String, List<ShowInfoVo>> getMap() {
        return map;
    }

    public void setMap(String str, List<ShowInfoVo> mlive) {
        if (mlive == null || mlive.size() == 0) {
            return;
        }
        map.put(str, mlive);
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

}
