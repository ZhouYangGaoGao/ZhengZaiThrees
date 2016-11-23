/**
 *
 */
package com.modernsky.istv.acitivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
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
import com.modernsky.istv.bean.RechargeBean;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.WeixinPayReq;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
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
 * @创建时间：2015-6-1 下午2:40:44
 * @类说明：
 */
public class RechargeMbActivity extends BaseActivity {
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(RechargeMbActivity.this, null);
    private String userId;
    private RechargeBean rechargeBean;
    StringBuffer sb;
    private WeixinPayReq payReq;
    PayReq req;
    private TextView mTextMoney;
    private TextView mTextMB;

    private WeakHandler mHandler = new WeakHandler(this) {
        @Override
        public void conventHandleMessage( Message msg) {
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
                        Toast.makeText(RechargeMbActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        toReslutOK();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(RechargeMbActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.equals(resultStatus, "6001")) {// 支付取消
                            Toast.makeText(RechargeMbActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                            toSentPayResult(aliPayInfo.getOut_trade_no(), -2);

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            toSentPayResult(aliPayInfo.getOut_trade_no(), -1);
                            toReslutFail();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(RechargeMbActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private int intExtra = -2;
    public Intent mIntent;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_PAY_RESULT)) {
                dismissDialog();
                intExtra = intent.getIntExtra(Constants.ACTION_PAY_RESULT, -2);
                mIntent = intent;
                toSentPayResult(payReq.getOut_trade_no(), intExtra);
            }
        }


    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aliPayBtn:
                getAliOrder("购买" + rechargeBean.getMb() + "M豆", userId, rechargeBean.getId(), rechargeBean.getMoney(), "");
                break;
            case R.id.weChatPayBtn:
                getWXOrder("购买" + rechargeBean.getMb() + "M豆", userId, rechargeBean.getId(), rechargeBean.getMoney() + "00", "");
                break;
//		case R.id.edit_back:
//			onBackPressed();
//			break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_choise_pay);
        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.USER_ID);
        rechargeBean = (RechargeBean) intent.getSerializableExtra(Constants.OUT_TRADE_NO);
        msgApi.registerApp(ThreeAppParams.WX_APP_ID);
        sb = new StringBuffer();
        req = new PayReq();
        Utils.setFullScreen(this);
    }

    @Override
    public void findViewById() {
        registerBoradcastReceiver();
        findViewById(R.id.aliPayBtn).setOnClickListener(this);
        findViewById(R.id.weChatPayBtn).setOnClickListener(this);
//		findViewById(R.id.edit_back).setOnClickListener(this);
        mTextMoney = (TextView) findViewById(R.id.tv_num_mongey);
        mTextMoney.setText("￥" + rechargeBean.getMoney());
        mTextMB = (TextView) findViewById(R.id.tv_num_mb);
        mTextMB.setText(rechargeBean.getMb());
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

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        LogUtils.t("orion", appSign);
        return appSign;
    }

    // 获得微信订单
    private void getWXOrder(String body, String userId, String videoId, String totalMoney, String voucherCode) {
        RequestParams params = UrlTool.getParams(Constants.BODY, body, Constants.TYPE, Constants.MB, Constants.USER_ID, userId, Constants.VIDEO_ID,
                videoId, Constants.TOTAL_FEE, totalMoney, Constants.CLIENT_IP, getIp(), Constants.TRADE_TYPE, Constants.APP, Constants.VOUCHER_CODE,
                voucherCode, UserParams.SOURCE, UserParams.ANDROID, Constants.IsQrcode, "0");

        SendActtionTool.postNoCheck(UserParams.URL_GET_WEIXIN_ORDER, ServiceAction.Action_Pay, PayAction.Action_getWinXin_prepay_id, this, params);
        showLoadingDialog();
    }

    // 获得支付宝订单
    private void getAliOrder(String body, String userId, String videoId, String totalMoney, String voucherCode) {
        RequestParams params = UrlTool.getPostParams(Constants.SUBJECT, body, Constants.TYPE, Constants.MB, Constants.USER_ID, userId,
                Constants.VIDEO_ID, videoId, Constants.TOTAL_FEE, totalMoney, Constants.CLIENT_IP, getIp(), Constants.TRADE_TYPE, Constants.APP,
                Constants.VOUCHER_CODE, voucherCode, UserParams.SOURCE, UserParams.ANDROID, Constants.IsQrcode, "0");

        SendActtionTool.post(UserParams.URL_GET_ALI_ORDER, ServiceAction.Action_Pay, PayAction.Action_getAliSign, this, params);
        showLoadingDialog();
    }

    // public void getAliPay(String name, String detil, String money) {
    // PayDemoActivity.aliPay(this, mHandler, name, detil, money);
    // // PayDemoActivity.aliPay(this, mHandler, "商品名称", "商品详情", "0.01");
    // }
    private AliPayInfo aliPayInfo;

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

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

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        String string = value.toString();
        switch ((PayAction) action) {
            case Action_getWinXin_prepay_id:
                payReq = JSON.parseObject(string, new TypeReference<WeixinPayReq>() {
                });
                toPayByWX(payReq);
                break;
            case Action_getAliSign:
                ResultBean<AliPayInfo> resultBean = JSON.parseObject(string, new TypeReference<ResultBean<AliPayInfo>>() {
                });
                aliPayInfo = resultBean.data;
                if (aliPayInfo != null) {
                    AliPay.aliPay(this, mHandler, aliPayInfo.getOrderInfo(), aliPayInfo.getSign());
                }
                break;
            case Action_sentWeixinBack:
                if (intExtra == 0) {
                    Utils.toast(this, R.string.pay_success);
                } else {
                    Utils.toast(this, R.string.pay_fail);
                }
                RechargeMbActivity.this.setResult(RESULT_OK, mIntent);
                // 关闭Activity
                RechargeMbActivity.this.finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        Utils.toast(getApplicationContext(), value.toString());
        LogUtils.t("onFaile" + action.toString(), value.toString());
    }

    // 发送支付结果
    private void toSentPayResult(String out_trade_no, int intExtra) {
        RequestParams params = UrlTool.getParams(Constants.OUT_TRADE_NO, out_trade_no, Constants.TRADE_STATE, String.valueOf(intExtra));
        SendActtionTool.postNoCheck(UserParams.URL_WEIXIN_BACK, ServiceAction.Action_Pay, PayAction.Action_sentWeixinBack, this, params);
    }

    private void toReslutFail() {
        Toast.makeText(RechargeMbActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
        mIntent = new Intent(Constants.ACTION_PAY_RESULT);
        intExtra = -2;
        mIntent.putExtra(Constants.ACTION_PAY_RESULT, -2);
    }


    private void toReslutOK() {
        if (aliPayInfo != null) {
            toSentPayResult(aliPayInfo.getOut_trade_no(), 0);
        }
        intExtra = 0;
        mIntent = new Intent(Constants.ACTION_PAY_RESULT);
        mIntent.putExtra(Constants.ACTION_PAY_RESULT, 0);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_PAY_RESULT);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

}
