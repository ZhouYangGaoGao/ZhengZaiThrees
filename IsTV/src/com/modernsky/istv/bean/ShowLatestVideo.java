package com.modernsky.istv.bean;

/**
 * Created by zhengzai_zxm on 16/3/28.
 */
public class ShowLatestVideo extends LatestVideo {
    private UserInfo userinfo;
    public UserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserInfo userinfo) {
        this.userinfo = userinfo;
    }


    public class UserInfo extends BaseBean {
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        private String userId;

    }

}
