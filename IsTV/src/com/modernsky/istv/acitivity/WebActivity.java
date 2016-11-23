package com.modernsky.istv.acitivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.PayAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.alipay.AliPay;
import com.modernsky.istv.alipay.PayResult;
import com.modernsky.istv.bean.AliPayInfo;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.WeixinPayReq;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.DrageLayout;
import com.modernsky.istv.view.PopThreeShare;
import com.modernsky.istv.wxapi.MD5;
import com.ta.utdid2.android.utils.NetworkUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-25 下午2:51:40
 * @类说明： H5网页类
 */
public class WebActivity extends BaseActivity {

    private String mTitle = "正在现场";
    private WebView mWebView;
    private String url;
    private String faceUrl;
    private boolean isBackApp;
    private TextView tv_title;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    dismissDialog();
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    LogUtils.t("resultInfo", resultInfo);
                    String resultStatus = payResult.getResultStatus();
                    LogUtils.t("resultStatus", resultStatus);
                    // orderFragment.allowPay();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(WebActivity.this, "支付成功", Toast.LENGTH_SHORT)
                                .show();
                        toSentPayResult(aliPayInfo.getOut_trade_no(), 0);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(WebActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.equals(resultStatus, "6001")) {// 支付取消
                            Toast.makeText(WebActivity.this, "支付取消",
                                    Toast.LENGTH_SHORT).show();
                            toSentPayResult(aliPayInfo.getOut_trade_no(), -2);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            toSentPayResult(aliPayInfo.getOut_trade_no(), -1);
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(WebActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    final IWXAPI msgApi = WXAPIFactory.createWXAPI(WebActivity.this, null);
//    final IWXAPI msgApi = WXAPIFactory.createWXAPI(getApplicationContext(), null);
    private String userId = "";
//    StringBuffer sb;
    private WeixinPayReq payReq;
    PayReq req;
    private AliPayInfo aliPayInfo;

    private PopThreeShare popThreeShare;
    private View top;
    private boolean shareNowUrl;
    private boolean isFinished;
    private String sharetit;
    private String sharedes;
    private String shareimg;
    private String mContent = "正在现场";
    private int type;

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_share:
                mWebView.loadUrl("javascript:getShareInfoForAndroid()");
                break;

            default:
                break;
        }
    }

    private void shareMethod() {
        try {
            mContent = mTitle;
            if (type == 6 || type == 3) {//秀场直播预告详情页
                shareNowUrl = true;
                mContent = Utils.getRandomContent(this, R.array.shareContent_showyugao);
            } else if (type == 9) {//不看肾亏类型
                shareNowUrl = true;
                mContent = Utils.getRandomContent(this, R.array.shareContent_shenkui);
            } else if (type == 8) {//票务跳转
                shareNowUrl = true;
            }
            String shareUrl = url;
            if (shareNowUrl)
                shareUrl = mWebView.getUrl();
            if (shareUrl.contains("userId"))
                shareUrl = shareUrl.substring(0, shareUrl.indexOf("userId"));
            popThreeShare = new PopThreeShare(this);
            if (shareUrl.contains("?")) {
                shareUrl = shareUrl + "&if_share=1";
            } else
                shareUrl = shareUrl + "?if_share=1";
            if (!TextUtils.isEmpty(sharetit) && !TextUtils.isEmpty(sharedes) && !TextUtils.isEmpty(shareimg)) {
                LogUtils.t("shareUrl", "mTitle== " + sharetit + ",mContent== " + sharedes + ",shareUrl== " + shareUrl + ",shareimg== " + shareimg);
                popThreeShare.setShareUrlForAnchor(sharetit, sharedes, shareUrl, shareimg, "");
            } else if (type == 9) {
                LogUtils.t("shareUrl", "mTitle==" + mTitle + ",mContent==" + mContent + ",shareUrl==" + shareUrl);
                popThreeShare.setShareUrlForAnchor("正在现场", mContent, shareUrl, R.drawable.icon_shenkui);
            } else {
                LogUtils.t("shareUrl", "mTitle==" + mTitle + ",mContent==" + mContent + ",shareUrl==" + shareUrl);
                popThreeShare.setShareUrl("正在现场", mContent, faceUrl, shareUrl);
            }
            popThreeShare.showBototomPop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        LogUtils.d("isBackApp"+isBackApp+"mWebView.canGoBack()---"+mWebView.canGoBack());
        if (!isBackApp && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private DrageLayout mDrageLayout;
    private RelativeLayout mDrageView;
    RelativeLayout mDragButtomView;

    private void initDrager() {
        mDrageLayout = (DrageLayout) findViewById(R.id.drageLayout);
        mDrageView = (RelativeLayout) findViewById(R.id.draglayoutView);
        mDragButtomView = (RelativeLayout) findViewById(R.id.layoutButtom_drag);
        mDrageLayout.setView(mDrageView, mDragButtomView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrageLayout.initDrageLayoutPosition();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * @param b 通知H5 支付结果
     */
    protected void toReslut(boolean b) {
        if (b) {
            mWebView.loadUrl("javascript:testOnAndroid(1)");
        } else
            mWebView.loadUrl("javascript:testOnAndroid(0)");
        LogUtils.d("            mWebView.loadUrl(\"javascript:testOnAndroid(0)\");\n");
    }


    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_webview);
//        mTitle = getIntent().getStringExtra(Constants.TITLE);
        url = getIntent().getStringExtra(Constants.URL);
        type = getIntent().getIntExtra(Constants.TYPE, 0);
        faceUrl = getIntent().getStringExtra("faceUrl");

        LogUtils.d("url---" + url + "mtitle----" + mTitle + ",faceUrl--" + faceUrl);
        if (!url.equals(Constants.URL_AGREEMENT)) {
            userId = UserService.getInatance().getUserBean(this).getId();
        }
        msgApi.registerApp(ThreeAppParams.WX_APP_ID);
//        sb = new StringBuffer();
        req = new PayReq();
        registerBoradcastReceiver();
        initDrager();
        getShareContent(url);
    }

    /*
     *
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void findViewById() {
        findViewById(R.id.img_back).setOnClickListener(this);
        View shareView = findViewById(R.id.img_share);
        if (type == 13) {
            shareView.setVisibility(View.GONE);
        }
        top = findViewById(R.id.top);
        shareView.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_video_name);
        tv_title.setText(mTitle);
        mWebView = new WebView(this.getApplicationContext());
        LinearLayout mll = (LinearLayout) findViewById(R.id.ll_webWiew);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setLayoutParams(layoutParams);
        mll.addView(mWebView);
        initWebView();
        if (url.equals(Constants.URL_AGREEMENT)) {
            shareView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mWebView != null)
            mWebView.loadUrl(url);
    }

    class MyWebViewClient extends WebViewClient {

        // 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            getShareContent(url);
            LogUtils.d("url" + url);
            if (url.contains("backapp=1"))
                isBackApp = true;
            else isBackApp = false;
            if (url.contains("alipay.com"))
                top.setVisibility(View.GONE);
            else
                top.setVisibility(View.VISIBLE);
            if (url.contains("zhengzai.tv")) {
                shareNowUrl = true;
            } else
                shareNowUrl = false;
            view.loadUrl(url);
            // 如果不需要其他对点击链接事件的处理返回true，否则返回false
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.contains("backapp=1"))
                isBackApp = true;
            else isBackApp = false;
//            LogUtils.d("currentUrl----"+mWebView.getUrl());
//            LogUtils.d("url"+url);
//            String js = "var newscript = document.createElement(\"script\");";
//            js += "newscript.src=\"http://www.123.456/789.js\";";
//            js += "newscript.onload=function(){xxx();};";  //xxx()代表js中某方法
//            js += "document.body.appendChild(newscript);";
            super.onPageFinished(view, url);
//            mWebView.loadUrl("javascript:getShareInfoForAndroid()");

        }
    }

    private void getShareContent(String url) {
        sharetit = "";
        sharedes = "";
        shareimg = "";
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    protected void initWebView() {
        // 提高渲染速度
        mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
        // 设置JS支持
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 触摸焦点有效
        mWebView.requestFocus();
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        // 支持插件
        // webView.getSettings().setPluginsEnabled(true);
        // 将图片调整到适合webview的大小 
        mWebView.getSettings().setUseWideViewPort(true);
        //
        mWebView.getSettings().setLoadWithOverviewMode(true);
        // 支持缩放 
        mWebView.getSettings().setSupportZoom(false);
        // 支持内容从新布局
        mWebView.getSettings()
                .setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // 多窗口 
        mWebView.getSettings().supportMultipleWindows();
        // 关闭webview中缓存
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置可以访问文件 
        mWebView.getSettings().setAllowFileAccess(true);
        // 当webview调用requestFocus时为webview设置节点
        mWebView.getSettings().setNeedInitialFocus(true);
        // 设置支持缩放
        mWebView.getSettings().setBuiltInZoomControls(true);
        // 支持通过JS打开新窗口
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持自动加载图片
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        // 设置背景颜色 透明
        mWebView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        // 不在浏览器打开，在自己的页面打开
        mWebView.setWebViewClient(new MyWebViewClient());
        // mWebView.loadUrl("file:///android_asset/index.html");
        // wView.loadUrl("content://com.android.htmlfileprovider/sdcard/index.html");

        //开启支持js打开本地存储
        mWebView.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.loadUrl(url);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitle = title;
                LogUtils.d("receivetitle-----" + title);
                tv_title.setText(title);
            }
        });
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid(final String videoId,
                                       final String vedioType) {
                LogUtils.t("clickOnAndroid---videoId", videoId + ", vedioType="
                        + vedioType);
                if (isFinished)
                    return;
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (vedioType.equals("4")) {
                            Utils.playLive(WebActivity.this, videoId);
                        } else if (vedioType.equals("5")) {
                            Utils.playShow(WebActivity.this, videoId, "");
                        } else {
                            Utils.playVideo(WebActivity.this, videoId, "");
                        }
                        finish();
                    }
                });
            }
        }, "playVideo");
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid(final String videoId,
                                       final String vedioType, final String singerId) {
                LogUtils.t("clickOnAndroid---videoId", videoId + ", vedioType="
                        + vedioType + ",singerId=" + singerId);
                if (isFinished)
                    return;
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (vedioType.equals("4")) {
                            Utils.playLive(WebActivity.this, videoId);
                        } else if (vedioType.equals("5")) {
                            Utils.playShow(WebActivity.this, videoId, singerId);
                        } else {
                            Utils.playVideo(WebActivity.this, videoId, "");
                        }
                        finish();
                    }
                });
            }
        }, "playShow");
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid(final String title, final String content, final String shareUrl) {
                if (isFinished)
                    return;
                sharetit = title;
                sharedes = content;
                shareimg = shareUrl;
                LogUtils.d("sharetit----" + sharetit);
                LogUtils.d("sharedes----" + sharedes);
                LogUtils.d("shareimg----" + shareimg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shareMethod();
                    }
                });

            }
        }, "share");
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid(final String userId) {
                if (isFinished)
                    return;
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.OpenUserInfo(WebActivity.this, userId, "1");
                    }
                });
            }
        }, "userDetail");
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid() {
                if (isFinished)
                    return;
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.sendBroadcastToService(7, WebActivity.this);
                        WebActivity.this.startActivity(new Intent(WebActivity.this, ApplyAnchorActivity.class));
                        WebActivity.this.finish();
                    }
                });
            }
        }, "applyAnchor");
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid(final String videoIds,
                                       final int payType, final String totalMoney, final String body,
                                       final String voucherCode) {
                LogUtils.d("totalMoney==" + totalMoney);
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (payType == 0) {
                            getAliOrder(body, userId, videoIds,
                                    totalMoney, voucherCode);
                        } else if (payType == 1) {
                            getWXOrder(body, userId, videoIds, totalMoney, voucherCode);
                        }
                    }
                });
            }

        }, "buy");
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);

        if (popThreeShare != null && arg2 != null) {
            popThreeShare.setSinaWeibo(arg0, arg1, arg2);
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_PAY_RESULT)) {
                dismissDialog();
                int intExtra = intent.getIntExtra(Constants.ACTION_PAY_RESULT,
                        -2);
                toSentPayResult(payReq.getOut_trade_no(), intExtra);

                if (intExtra == 0)
                    Utils.toast(context, R.string.pay_success);
                else
                    Utils.toast(context, R.string.pay_fail);
            }
        }
    };

    // 发送支付结果
    private void toSentPayResult(String out_trade_no, int intExtra) {
        toReslut(intExtra == 0);
        RequestParams params = UrlTool.getParams(Constants.OUT_TRADE_NO,
                out_trade_no, Constants.TRADE_STATE, String.valueOf(intExtra));
        SendActtionTool.postNoCheck(Constants.UserParams.URL_WEIXIN_BACK,
                ServiceAction.Action_Pay, PayAction.Action_sentWeixinBack,
                this, params);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        isFinished = true;
        super.onDestroy();
        mWebView.setVisibility(View.GONE);
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    // 获得微信订单
    private void getWXOrder(String body, String userId, String videoId,
                            String totalMoney, String voucherCode) {
        RequestParams params = UrlTool.getPostParams(Constants.BODY, body,
                Constants.TYPE, Constants.VIDEO, Constants.USER_ID, userId,
                Constants.VIDEO_ID, videoId, Constants.TOTAL_FEE, totalMoney,
                Constants.CLIENT_IP, getIp(), Constants.TRADE_TYPE,
                Constants.APP, Constants.VOUCHER_CODE, voucherCode,
                Constants.UserParams.SOURCE, Constants.UserParams.ANDROID, Constants.IsQrcode, "0");

        SendActtionTool.postNoCheck(Constants.UserParams.URL_GET_WEIXIN_ORDER,
                ServiceAction.Action_Pay, PayAction.Action_getWinXin_prepay_id,
                this, params);
        showLoadingDialog();
    }

    // 获得支付宝订单
    private void getAliOrder(String body, String userId, String videoId,
                             String totalMoney, String voucherCode) {
        RequestParams params = UrlTool.getPostParams(Constants.SUBJECT, body,
                Constants.TYPE, Constants.VIDEO, Constants.USER_ID, userId,
                Constants.VIDEO_ID, videoId, Constants.TOTAL_FEE, totalMoney,
                Constants.CLIENT_IP, getIp(), Constants.TRADE_TYPE,
                Constants.APP, Constants.VOUCHER_CODE, voucherCode,
                Constants.UserParams.SOURCE, Constants.UserParams.ANDROID, Constants.IsQrcode, "0");

        SendActtionTool.post(Constants.UserParams.URL_GET_ALI_ORDER,
                ServiceAction.Action_Pay, PayAction.Action_getAliSign, this,
                params);
        showLoadingDialog();
    }

    private void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_PAY_RESULT);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((PayAction) action) {
            case Action_getWinXin_prepay_id:
                payReq = JSON.parseObject(value.toString(),
                        new TypeReference<WeixinPayReq>() {
                        });
                toPayByWX(payReq);
                break;
            case Action_getAliSign:
                ResultBean<AliPayInfo> resultBean = JSON.parseObject(
                        value.toString(),
                        new TypeReference<ResultBean<AliPayInfo>>() {
                        });
                aliPayInfo = resultBean.data;
                if (aliPayInfo != null) {
                    AliPay.aliPay(this, mHandler,
                            aliPayInfo.getOrderInfo(), aliPayInfo.getSign());
                }
                break;
            case Action_sentWeixinBack:
                break;
        }
    }

    private void toPayByWX(WeixinPayReq payReq) {
        req.appId = ThreeAppParams.WX_APP_ID;
        req.partnerId = payReq.getMch_id();
        req.prepayId = payReq.getPrepay_id();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = payReq.getNonce_str();
        req.timeStamp = String.valueOf(getTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        LogUtils.d("appid==" + req.appId);
        LogUtils.d("noncestr==" + req.nonceStr);
        LogUtils.d("package==" + req.packageValue);
        LogUtils.d("partnerid==" + req.partnerId);
        LogUtils.d("prepayid==" + req.prepayId);
        LogUtils.d("timestamp==" + req.timeStamp);

        req.sign = genAppSign(signParams);
        msgApi.registerApp(ThreeAppParams.WX_APP_ID);

        msgApi.sendReq(req);
    }

    /**
     * 生成签名
     */
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(ThreeAppParams.WX_PAY_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes())
                .toUpperCase();
        LogUtils.t("orion", appSign);
        return appSign;
    }

    /**
     * @return ip地址
     */
    private String getIp() {
        return NetworkUtils.getWifiIpAddress(getApplicationContext());
    }

    private long getTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        dismissDialog();
    }

}
