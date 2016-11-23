package com.modernsky.istv.service;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.PreferencesUtils;

/**
 * @author rendy
 *         <p/>
 *         当前登录用户信息存储
 */
public class UserService {
    static UserService lOGIC;

    private UserService() {
    }

    private UserEntity userBean;
    private boolean isBingQQ;
    private boolean isBingWX;
    private boolean isBingWB;
    private String dianTaiToken;

    public String getDianTaiToken() {
        return dianTaiToken;
    }

    public void setDianTaiToken(String dianTaiToken) {
        this.dianTaiToken = dianTaiToken;
    }

    /**
     * @return 当前登录着消息
     */
    public UserEntity getUserBean(Context context) {
        String value = PreferencesUtils.getPreferences(context,
                PreferencesUtils.TYPE_USER_LOGIN);
        if (!GeneralTool.isEmpty(value)) {
            userBean = JSON.parseObject(value, UserEntity.class);
        } else userBean = null;
        return userBean;
    }

    public void setUserBean(UserEntity muser, Context context) {
        this.userBean = muser;
        if (muser != null) {
            String value = JSON.toJSONString(muser);
            if (!GeneralTool.isEmpty(value)) {
                if (getUserBean(context) == null) {
                    Intent intent = new Intent();
                    PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_LOGIN_IN_TIME, System
                            .currentTimeMillis());
                    intent.setAction(Constants.ACTION_LOGIN_CHANGE);
                    DavikActivityManager.getScreenManager().currentActivity()
                            .sendBroadcast(intent);
                }
            }
            PreferencesUtils.savePreferences(context,
                    PreferencesUtils.TYPE_USER_LOGIN, value);

        } else {
            isBingQQ = false;
            isBingWB = false;
            isBingWX = false;
            PreferencesUtils.savePreferences(context,
                    PreferencesUtils.TYPE_USER_LOGIN, "");
            PreferencesUtils.savePreferences(context,
                    Constants.U_I, "");
            PreferencesUtils.saveLongPreferences(context,
                    PreferencesUtils.TYPE_SHARE_TIME, 0);
            PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_LOGIN_IN_TIME, 0);


            PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_LOGIN_OUT_TIME, System
                    .currentTimeMillis());
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_LOGIN_CHANGE);
            DavikActivityManager.getScreenManager().currentActivity()
                    .sendBroadcast(intent);
        }

    }

    public static UserService getInatance() {

        return lOGIC = lOGIC == null ? new UserService() : lOGIC;
    }

    public boolean isNeedLogin(Context context) {
        if (getUserBean(context) == null) {
            // DialogTool.createLoginDialog(context,R.string.outPushlogin);
            return true;
        }
        return false;
    }


    public boolean isBingQQ() {
        return isBingQQ;
    }

    public void setBingQQ(boolean isBingQQ) {
        this.isBingQQ = isBingQQ;
    }

    public boolean isBingWX() {
        return isBingWX;
    }

    public void setBingWX(boolean isBingWX) {
        this.isBingWX = isBingWX;
    }

    public boolean isBingWB() {
        return isBingWB;
    }

    public void setBingWB(boolean isBingWB) {
        this.isBingWB = isBingWB;
    }

}
