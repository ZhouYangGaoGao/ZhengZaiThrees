package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.OpenInfoBean;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author rendy 绑定界面设置
 */
public class BindingActivity extends BaseActivity {
    private TextView textVersion;
    private String uid;
    private UMShareAPI mShareAPI;
    //	UMSocialService mController = UMServiceFactory
//			.getUMSocialService("com.umeng.share");
    private OpenInfoBean openInfo = new OpenInfoBean();
    private ImageView imgQQ, imgWeixin, imgWeibo;
    private TextView tetQQ, tetWeixin, tetWeibo;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_outBtn:
                // outPush();
                outUser();
                break;
            case R.id.img_live:
                finish();
                break;
            case R.id.binding_imgQQ:

                if (!UserService.getInatance().isBingQQ()) {
                    login(SHARE_MEDIA.QQ);
                } else {
                    Utils.toast(getApplicationContext(), "已经绑定QQ登录");

                }
                break;
            case R.id.binding_ImgWeiBo:

                if (!UserService.getInatance().isBingWB()) {
                    login(SHARE_MEDIA.SINA);
                } else {
                    Utils.toast(getApplicationContext(), "已经绑定微博登录");

                }
                break;
            case R.id.binding_imgWeiXin:

                if (!UserService.getInatance().isBingWX()) {
                    getWeiXinInfo();
                } else {
                    Utils.toast(getApplicationContext(), "已经绑定微信登录");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//				uid = value.getString("uid");
                uid = map.get("uid");
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    Toast.makeText(BindingActivity.this, "授权失败...",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
//		mController.doOauthVerify(this, platform, new UMAuthListener() {
//
//			@Override
//			public void onStart(SHARE_MEDIA platform) {
//			}
//
//			@Override
//			public void onError(SocializeException e, SHARE_MEDIA platform) {
//			}
//
//			@Override
//			public void onComplete(Bundle value, SHARE_MEDIA platform) {
//				uid = value.getString("uid");
//				if (!TextUtils.isEmpty(uid)) {
//					getUserInfo(platform);
//				} else {
//					Toast.makeText(BindingActivity.this, "授权失败...",
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//
//			@Override
//			public void onCancel(SHARE_MEDIA platform) {
//			}
//		});
    }

    /**
     * 添加qq 微信的配置
     */
    private void addQqWinxiPlatformConfilg() {
        // // 添加QQ支持, 并且设置QQ分享内容的target url
        PlatformConfig.setWeixin(ThreeAppParams.WX_APP_ID, ThreeAppParams.WX_APP_KEY);
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone(ThreeAppParams.QQ_APP_ID, ThreeAppParams.QQ_APP_KEY);

//		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
//				ThreeAppParams.QQ_APP_ID, ThreeAppParams.QQ_APP_KEY);
//		qqSsoHandler.setTargetUrl("http://www.umeng.com");
//		qqSsoHandler.addToSocialSDK();
//		// 添加微信平台
//		UMWXHandler wxHandler = new UMWXHandler(this, ThreeAppParams.WX_APP_ID,
//				ThreeAppParams.WX_APP_KEY);
//		wxHandler.addToSocialSDK();

    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform) {
        mShareAPI.getPlatformInfo(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                if (map != null) {
                    LogUtils.t("getUseInfo", map.toString());
                    sendUserInfo(platform, map);
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
//		mController.getPlatformInfo(this, platform, new UMDataListener() {
//			@Override
//			public void onStart() {
//
//			}
//
//			@Override
//			public void onComplete(int status, Map<String, Object> info) {
//				// String showText = "";
//				// if (status == StatusCode.ST_CODE_SUCCESSED) {
//				// showText = "用户名：" + info.get("screen_name").toString();
//				// Log.d("#########", "##########" + info.toString());
//				// } else {
//				// showText = "获取用户信息失败";
//				// }
//				if (info != null) {
//					LogUtils.t("getUseInfo", info.toString());
//					sendUserInfo(platform, info);
//				}
//			}
//
//		});
    }

    private void sendUserInfo(SHARE_MEDIA platform, Map<String, String> info) {
        String sex = null;
        String city = null;
        switch (platform) {
            case QQ:
                sex = info.get("gender") + "";
                city = info.get("city") + "";
                if (sex.equals("男")) {
                    sex = "1";
                } else {
                    sex = "0";
                }
                openInfo.setSourse("QQ");
                openInfo.setSex(sex);
                openInfo.setLocation(city);
                openInfo.setName(info.get("screen_name"));
                openInfo.setUserFace(info.get("profile_image_url"));
                openInfo.setOpenId(uid);
                break;
            case SINA:
                sex = info.get("gender") + "";
                city = info.get("location") + "";
                openInfo.setSourse("WB");
                openInfo.setSex(sex);
                openInfo.setLocation(city);
                openInfo.setName(info.get("screen_name"));
                openInfo.setUserFace(info.get("profile_image_url"));
                openInfo.setOpenId(uid);
                break;
            default:
                break;
        }
        sendOpenInfo();
    }

    // 执行绑定
    private void sendOpenInfo() {
        UserAction.Action_BINDING.value = openInfo.getSourse();
        SendActtionTool.post(
                UserParams.URL_USER_BINDING,
                ServiceAction.Action_User,
                UserAction.Action_BINDING,
                this,
                UrlTool.getPostParams(Constants.USER_ID, UserService.getInatance()
                                .getUserBean(this).getId(), UserParams.openId,
                        openInfo.getOpenId(), UserParams.openName,
                        openInfo.getName(), UserParams.faceUrl,
                        openInfo.getUserFace(), UserParams.sex,
                        openInfo.getSex(), UserParams.location,
                        openInfo.getLocation(), UserParams.SOURCE,
                        openInfo.getSourse()));
        showLoadingDialog();
    }

    /**
     * 得到微信 信息
     */
    private void getWeiXinInfo() {
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                Toast.makeText(BindingActivity.this, "授权完成",
                        Toast.LENGTH_SHORT).show();
                mShareAPI.getPlatformInfo(BindingActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        if (i == 2 && map != null) {
//							StringBuilder sb = new StringBuilder();
//							Set<String> keys = map.keySet();
//							for (String key : keys) {
//								sb.append(key
//										+ "="
//										+ map.get(key)
//										.toString()
//										+ "\r\n");
//							}
//							LogUtils.t("TestData",
//									sb.toString());
                            sendWinxininfo(map);
                        } else {
                            LogUtils.t("TestData",
                                    "发生错误：" + i);
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {

                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Toast.makeText(BindingActivity.this, "授权错误",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Toast.makeText(BindingActivity.this, "授权取消",
                        Toast.LENGTH_SHORT).show();
            }
        });
//		mController.doOauthVerify(this, SHARE_MEDIA.WEIXIN,
//				new UMAuthListener() {
//					@Override
//					public void onStart(SHARE_MEDIA platform) {
//						Toast.makeText(BindingActivity.this, "授权开始",
//								Toast.LENGTH_SHORT).show();
//					}
//
//					@Override
//					public void onError(SocializeException e,
//							SHARE_MEDIA platform) {
//						Toast.makeText(BindingActivity.this, "授权错误",
//								Toast.LENGTH_SHORT).show();
//					}
//
//					@Override
//					public void onComplete(Bundle value, SHARE_MEDIA platform) {
//						Toast.makeText(BindingActivity.this, "授权完成",
//								Toast.LENGTH_SHORT).show();
//						// 获取相关授权信
//						mController.getPlatformInfo(BindingActivity.this,
//								SHARE_MEDIA.WEIXIN, new UMDataListener() {
//									@Override
//									public void onStart() {
//										Toast.makeText(BindingActivity.this,
//												"获取平台数据开始...",
//												Toast.LENGTH_SHORT).show();
//									}
//
//									@Override
//									public void onComplete(int status,
//											Map<String, Object> info) {
//										if (status == 200 && info != null) {
//											StringBuilder sb = new StringBuilder();
//											Set<String> keys = info.keySet();
//											for (String key : keys) {
//												sb.append(key
//														+ "="
//														+ info.get(key)
//																.toString()
//														+ "\r\n");
//											}
//											LogUtils.t("TestData",
//													sb.toString());
//											sendWinxininfo(info);
//										} else {
//											LogUtils.t("TestData",
//													"发生错误：" + status);
//										}
//									}
//
//								});
//					}
//
//					@Override
//					public void onCancel(SHARE_MEDIA platform) {
//						Toast.makeText(BindingActivity.this, "授权取消",
//								Toast.LENGTH_SHORT).show();
//					}
//				});
    }

    /**
     * @param info 发送微信信息
     */
    private void sendWinxininfo(Map<String, String> info) {
        Object city = info.get("city");
        city = city == null ? "" : city;
        String sex = info.get("sex").toString() + "";
        openInfo.setSourse("WX");
        openInfo.setSex(sex);
        openInfo.setLocation(city.toString());
        openInfo.setName(info.get("nickname"));
        openInfo.setUserFace(info.get("headimgurl"));
        openInfo.setOpenId(info.get("openid"));
        sendOpenInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    private void outUser() {
        SendActtionTool.get(
                UserParams.URL_REGISTER_LOGOUT,
                ServiceAction.Action_User,
                UserAction.Action_Login_OUT,
                this,
                UrlTool.getParams(Constants.USER_ID, UserService.getInatance()
                        .getUserBean(this).getId()));
        PreferencesUtils.savePreferences(this, Constants.U_I, "");
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_binding);
        mShareAPI = UMShareAPI.get(this);
    }


    @Override
    public void findViewById() {
        findViewById(R.id.img_live).setOnClickListener(this);

        imgQQ = getView(R.id.binding_imgQQ);
        imgQQ.setOnClickListener(this);
        imgWeibo = getView(R.id.binding_ImgWeiBo);
        imgWeibo.setOnClickListener(this);
        imgWeixin = getView(R.id.binding_imgWeiXin);
        imgWeixin.setOnClickListener(this);

        tetQQ = getView(R.id.binding_imgQQTet);
        tetWeibo = getView(R.id.binding_imgWeiboTet);
        tetWeixin = getView(R.id.binding_imgWeiXinTet);
        addQqWinxiPlatformConfilg();
        SendActtionTool.post(
                UserParams.URL_GET_ONE,
                ServiceAction.Action_User,
                UserAction.Action_CHECK_ONE,
                this,
                UrlTool.getPostParams(Constants.USER_ID, UserService.getInatance()
                        .getUserBean(this).getId()));
        showLoadingDialog();

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        switch (service) {
            case Action_User:
                anayUserback((UserAction) action, value);
                break;
            default:
                break;
        }
    }

    private void anayUserback(UserAction action, Object value) {
        switch (action) {
            case Action_Push_Acton_Out:
                LogUtils.t("SetActivity.anayUserback()", "退出服务器推送");
                break;
            case Action_Login_OUT:
                toLoginOut();
                break;
//            case Action_CHECK_VERSION:
//                JSONObject object = (JSONObject) value;
//                try {
//                    object = object.getJSONObject("androidVersion");
//                    String version = object.getString("version");
//                    String url = object.getString("url");
//                    if (!version.equals(BaseApplication.mVersionName)) {
//                        DialogTool.createCheckDialog(this, url);
//                    } else {
//                        LogUtils.t("版本更新", "不用更新");
//                    }
//                } catch (JSONException e) {
//                    LogUtils.t("版本更新", e.toString());
//                    e.printStackTrace();
//                }
//                break;
            case Action_CHECK_ONE:
                JSONObject baseObj = (JSONObject) value;
                JSONArray openUser;
                try {
                    openUser = baseObj.getJSONObject(Constants.USER_ENTITY)
                            .getJSONArray("openUser");
                    if (openUser == null) {
                        return;
                    }
                    for (int i = 0; i < openUser.length(); i++) {
                        String source = openUser.getJSONObject(i).getString(
                                UserParams.SOURCE);
                        if (source.equals("QQ")) {
                            UserService.getInatance().setBingQQ(true);
                            imgQQ.setBackgroundResource(R.drawable.icon_choose_1_selected);
                            tetQQ.setText("已经绑定");
                        } else if (source.equals("WX")) {
                            UserService.getInatance().setBingWX(true);
                            imgWeixin
                                    .setBackgroundResource(R.drawable.icon_choose_1_selected);
                            tetWeixin.setText("已经绑定");

                        } else if (source.equals("WB")) {
                            UserService.getInatance().setBingWB(true);
                            imgWeibo.setBackgroundResource(R.drawable.icon_choose_1_selected);
                            tetWeibo.setText("已经绑定");

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Action_BINDING:
                if ("QQ".equals(action.value)) {
                    UserService.getInatance().setBingQQ(true);
                    Utils.toast(getApplicationContext(), "QQ登录绑定成功");
                    imgQQ.setBackgroundResource(R.drawable.icon_choose_1_selected);
                    tetQQ.setText("已经绑定");
                } else if ("WX".equals(action.value)) {
                    Utils.toast(getApplicationContext(), "微信登录绑定成功");
                    UserService.getInatance().setBingWX(true);
                    imgWeixin
                            .setBackgroundResource(R.drawable.icon_choose_1_selected);
                    tetWeixin.setText("已经绑定");

                } else if ("WB".equals(action.value)) {
                    Utils.toast(getApplicationContext(), "微博绑定登录绑定成功");
                    UserService.getInatance().setBingWB(true);
                    imgWeibo.setBackgroundResource(R.drawable.icon_choose_1_selected);
                    tetWeibo.setText("已经绑定");

                }
                action.value = null;
                break;
            default:
                break;
        }
    }

    private void toLoginOut() {
        UserService.getInatance().setUserBean(null, this);
        Utils.toast(getApplicationContext(), "退出成功");
        DavikActivityManager.getScreenManager().showTargetAty(
                MainActivity.class.getName());
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        switch ((UserAction) action) {
            case Action_Login_OUT:
                toLoginOut();
                break;
            default:
                Utils.toast(getApplicationContext(), value.toString());
                break;
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        dismissDialog();
    }

}
