//package com.modernsky.istv.tool;
//
//import java.io.IOException;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Toast;
//
//import com.modernsky.istv.utils.ThreeAppParams;
//import com.weibo.sdk.android.Oauth2AccessToken;
//import com.weibo.sdk.android.Weibo;
//import com.weibo.sdk.android.WeiboAuthListener;
//import com.weibo.sdk.android.WeiboDialogError;
//import com.weibo.sdk.android.WeiboException;
//import com.weibo.sdk.android.api.StatusesAPI;
//import com.weibo.sdk.android.net.RequestListener;
//import com.weibo.sdk.android.sso.SsoHandler;
//
//public class SinaShareTool implements RequestListener {
//	private Activity mActivity;
//	private Weibo mWeibo;
//	private SsoHandler mSsoHandler;
//	private String mContent;
//	private String mUrl;
//
//	public SinaShareTool(Activity mActivity, String mContent, String url) {
//		super();
//		this.mActivity = mActivity;
//		this.mContent = mContent;
//		this.mUrl = url;
//		init();
//	}
//
//	public void init() {
//		try {
//			Class sso = Class.forName("com.weibo.sdk.android.sso.SsoHandler");
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		mWeibo = Weibo.getInstance(ThreeAppParams.WEIBO_APP_KEY,
//				ThreeAppParams.WEIBO_REDIRECT_URL);
//		mSsoHandler = new SsoHandler(mActivity, mWeibo);
//		mSsoHandler.authorize(new AuthDialogListener());
//	}
//
//	public SsoHandler getHandler() {
//		return mSsoHandler;
//	}
//
//	private void updateWeibo() {
//		StatusesAPI api = new StatusesAPI(ThreeAppParams.accessToken);
//		api.uploadUrlText("我正在看【" + mContent + "】" + mUrl + "下载正在现场App立即观看现场"
//				+ "@ModernSkyNow", "http://img.zhengzai.tv/common/zzshare.jpg",
//				"90.0", "90.0", this);
//		// api.upload(mContent,
//		// "file:///android_asset/share_pic.jpg", "90.0", "90.0",
//		// this);
//		// api.update("123", "90.0", "90.0", this);
//	}
//
//	class AuthDialogListener implements WeiboAuthListener {
//		@Override
//		public void onComplete(Bundle values) {
//			String token = values.getString("access_token");
//			String expires_in = values.getString("expires_in");
//			ThreeAppParams.accessToken = new Oauth2AccessToken(token,
//					expires_in);
//			if (ThreeAppParams.accessToken.isSessionValid()) {
//				try {
//					Class sso = Class
//							.forName("com.weibo.sdk.android.api.WeiboAPI");
//					// 获取到正确的token
//					if (!TextUtils.isEmpty(ThreeAppParams.accessToken
//							.getToken())) {
//						Toast.makeText(mActivity, "分享到微博", Toast.LENGTH_LONG)
//								.show();
//						updateWeibo();
//					} else {
//						Toast.makeText(mActivity, "请登录微博", Toast.LENGTH_LONG)
//								.show();
//					}
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//			} else {
//				Toast.makeText(mActivity, "token   失效", Toast.LENGTH_LONG)
//						.show();
//			}
//		}
//
//		@Override
//		public void onError(WeiboDialogError e) {
//			Toast.makeText(mActivity, "Auth error : " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
//		}
//
//		@Override
//		public void onCancel() {
//			Toast.makeText(mActivity, "Auth cancel", Toast.LENGTH_LONG).show();
//		}
//
//		@Override
//		public void onWeiboException(WeiboException e) {
//			Toast.makeText(mActivity, "Auth exception : " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
//		}
//
//	}
//
//	@Override
//	public void onComplete(String arg0) {
//		mActivity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(mActivity, "分享成功", 2000).show();
//
//			}
//		});
//	}
//
//	@Override
//	public void onError(WeiboException arg0) {
//		mActivity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(mActivity, "分享失败", 2000).show();
//
//			}
//		});
//	}
//
//	@Override
//	public void onIOException(IOException arg0) {
//		mActivity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(mActivity, "分享失败", 2000).show();
//
//			}
//		});
//	}
//}
