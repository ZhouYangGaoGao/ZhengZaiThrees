package com.modernsky.istv.tool;

import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.listener.CommonListener;
import com.modernsky.istv.manager.BaseApplication;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ThreeAppParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaoweiChuang
 * @2015年4月1日
 * @descripte 发送网络请求
 */
public class SendActtionTool {
    /**
     * 2015年4月1日
     *
     * @param url      带参数字段的 url xx.com?name=a&psd=chuagn123
     * @param service  业务分类
     * @param action   业务指令信号
     * @param listener 回调业务
     */
    public static void post(final String url, final ServiceAction service,
                            final Object action, final CommonListener listener) {
        RequestParams params = new RequestParams();
        post(url, service, action, listener, params);

    }

    /**
     * @param url      网络地址
     * @param service  业务
     * @param action   业务指令信号
     * @param listener 回调
     * @param params   参数字段
     */
    public static void post(final String url, final ServiceAction service,
                            final Object action, final CommonListener listener,
                            RequestParams params) {
        showSendLog(url, action, params);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        http.configCurrentHttpCacheExpiry(0);
        params.addHeader(Constants.VERSION, BaseApplication.mVersionName);
        params.addHeader(Constants.SOURCE, Constants.ANDROID_MOBILE);
        params.addHeader(Constants.U_I, PreferencesUtils.getPreferences(DavikActivityManager.getScreenManager().currentActivity(), Constants.U_I));
//        LogUtils.d("ceshi","U-I: "+PreferencesUtils.getPreferences(DavikActivityManager.getScreenManager().currentActivity(), Constants.U_I));
        http.send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        listener.onStart(service, action);
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

//                         Utils.toast(DavikActivityManager.getScreenManager()
//                                 .currentActivity(), "网络异常，请您检查网络后再试");
                        listener.onException(service, action, msg);
                        listener.onFinish(service, action);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> response) {
                        if (response == null || TextUtils.isEmpty(response.result.trim()))
                            listener.onException(service, action, "");
                        else
                            try {
                                JSONObject object = new JSONObject(response.result);
                                // 请求成功
                                if (object.optInt(Constants.STATUS, -1) == 1) {
                                    saveHeader(response);
                                    listener.onSuccess(service, action, object);
                                    // 更新推送信息
                                    updatePush(service, action);
                                    // 请求失败
                                } else {
                                    listener.onFaile(service, action,
                                            object.optString(Constants.MESSAGE, ""));
                                }
                            } catch (JSONException e) {
                                // 服务器出现异常
                                listener.onException(service, action, "");
                                e.printStackTrace();
                            }
                        listener.onFinish(service, action);
                    }
                });
    }

    /**
     * 保存头信息
     *
     * @param response
     */
    private static void saveHeader(ResponseInfo<String> response) {
        if (response == null || response.getLastHeader(Constants.U_I) == null)
            return;
        String u_i = response.getLastHeader(Constants.U_I).toString();
        if (!u_i.contains(Constants.U_I + ":"))
            return;
        u_i = u_i.substring(u_i.indexOf(":") + 1).trim();
        PreferencesUtils.savePreferences(DavikActivityManager.getScreenManager().currentActivity(), Constants.U_I, u_i);
//        LogUtils.d("ceshi","u_i: "+ u_i);
    }

    /**
     * 关闭更新到自己服务器的推送
     *
     * @param service
     * @param action
     */
    private static void updatePush(ServiceAction service, Object action) {
        if (service == null || action == null) {
            return;
        }
        switch (service) {
            case Action_User:
                switch ((UserAction) action) {
                    // 关闭推送服务
                    case Action_Push_Acton:
                        ThreeAppParams.isNeedToken = false;
                        LogUtils.t("SendActionTool.updatePush()", "更新推送功能到服务器成功");
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * post后不对返回结果进行校验
     *
     * @param url      网络地址
     * @param service  业务
     * @param action   业务指令信号
     * @param listener 回调
     * @param params   参数字段
     */
    public static void postNoCheck(final String url,
                                   final ServiceAction service, final Object action,
                                   final CommonListener listener, RequestParams params) {
        showSendLog(url, action, params);
        HttpUtils http = new HttpUtils();
        params.addHeader(Constants.VERSION, BaseApplication.mVersionName);
        params.addHeader(Constants.U_I, PreferencesUtils.getPreferences(DavikActivityManager.getScreenManager().currentActivity(), Constants.U_I));
        params.addHeader(Constants.SOURCE, Constants.ANDROID_MOBILE);
        http.send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        listener.onStart(service, action);
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

//                         Utils.toast(DavikActivityManager.getScreenManager()
//                         .currentActivity(), "网络异常，请您检查网络后再试");
                        listener.onException(service, action, msg);
                        listener.onFinish(service, action);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> response) {
                        try {
                            saveHeader(response);
                            JSONObject object = new JSONObject(response.result);
                            listener.onSuccess(service, action, object);
                        } catch (JSONException e) {
                            // 服务器出现异常
                            listener.onException(service, action, "");
                            e.printStackTrace();
                        }
                        listener.onFinish(service, action);
                    }
                });
    }

    /**
     * 2015年4月1日
     *
     * @param url      带参数字段的
     * @param service  业务分类
     * @param action   业务指令信号
     * @param listener 回调业务
     */
    public static void get(final String url, final ServiceAction service,
                           final Object action, final CommonListener listener) {
        RequestParams params = new RequestParams();
        get(url, service, action, listener, params);
    }

    /**
     * @param url
     * @param service
     * @param action
     * @param listener
     * @param params   请求参数
     */
    public static void get(final String url, final ServiceAction service,
                           final Object action, final CommonListener listener,
                           final RequestParams params) {
        showSendLog(url, action, params);
        HttpUtils http = new HttpUtils();
        http.configDefaultHttpCacheExpiry(0);
        http.configCurrentHttpCacheExpiry(0);
        params.addHeader(Constants.VERSION, BaseApplication.mVersionName);
        params.addHeader(Constants.SOURCE, Constants.ANDROID_MOBILE);
        params.addHeader(Constants.U_I, PreferencesUtils.getPreferences(DavikActivityManager.getScreenManager().currentActivity(), Constants.U_I));
        http.send(HttpRequest.HttpMethod.GET, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        listener.onStart(service, action);
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.t("onFailure", msg);
//                         Utils.toast(DavikActivityManager.getScreenManager()
//                         .currentActivity(), "网络异常，请您检查网络后再试");
                        listener.onException(service, action, msg);
                        listener.onFinish(service, action);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> response) {
                        LogUtils.t("onSuccess", response.result);
                        try {
                            JSONObject object = new JSONObject(response.result);
                            // 请求成功
                            if (object.optInt(Constants.STATUS, -1) == 1) {
                                saveHeader(response);
                                listener.onSuccess(service, action, object);
                                // 请求失败
                            } else {
                                listener.onFaile(service, action,
                                        object.optString(Constants.MESSAGE, ""));
                            }
                        } catch (JSONException e) {
                            // 服务器出现异常
                            listener.onException(service, action, "");
                            e.printStackTrace();
                        }
                        listener.onFinish(service, action);
                    }
                });
    }

    private static void showSendLog(String url, Object action, RequestParams params) {
        String str = "";
        if (action != null)
            str = action.toString();
        LogUtils.t("url+params:" + str, url + "?" + UrlTool.getParamsString(params));
    }

}
