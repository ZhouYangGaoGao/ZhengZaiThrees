package com.modernsky.istv.bean;

/**
 * Created by zqg on 2016/3/7.
 */
public class ApplyStateInfo extends BaseBean {

    /**
     * title : 主播要干什么？
     * content : 正在推出的app 直播联调
     */

    private String title;
    private String content;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
