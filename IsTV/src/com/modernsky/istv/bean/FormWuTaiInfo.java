package com.modernsky.istv.bean;

import java.util.List;

/**
 * Created by zqg on 2015/12/30.
 */
public class FormWuTaiInfo extends BaseBean {
    private String stageName;
    private List<FormWuTaiItemInfo> detail;


    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public List<FormWuTaiItemInfo> getDetail() {
        return detail;
    }

    public void setDetail(List<FormWuTaiItemInfo> detail) {
        this.detail = detail;
    }

    public class FormWuTaiItemInfo {
        private String time;
        private String starrName;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getStarrName() {
            return starrName;
        }

        public void setStarrName(String starrName) {
            this.starrName = starrName;
        }
    }
}
