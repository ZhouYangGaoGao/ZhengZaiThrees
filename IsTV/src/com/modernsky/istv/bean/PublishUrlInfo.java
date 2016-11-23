package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/12.
 */
public class PublishUrlInfo extends BaseBean {

    /**
     * machine : 1
     * pushUrl : rtmp://w.gslb.lecloud.com/live/201603043000001lr99
     * status : 0
     */

    private String machine;
    private String pushUrl;
    private int status;

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMachine() {
        return machine;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public int getStatus() {
        return status;
    }
}
