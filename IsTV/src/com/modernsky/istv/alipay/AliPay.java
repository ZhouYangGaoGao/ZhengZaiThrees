package com.modernsky.istv.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.modernsky.istv.utils.LogUtils;

public class AliPay {

    // 商户PID
    // public static final String PARTNER = "2088911064667455";
    // 商户收款账号
    // public static final String SELLER = "3222642119@qq.com";
    // 商户私钥，pkcs8格式
    // public static final String RSA_PRIVATE =
    // "MIICXAIBAAKBgQCyoc6uTryzqpNdTIlUQZFsl9SLWthh9TBUW0o+09zAP8l5u5AjLxh5lQMojFWNIpvxKK9j+4IyeZgXiD7uxziPmaqEJqmvBV7OgoWGXEhkCevC7CJNw5X44UqxYMhJMzhiKcj/dcKs1/PEgr1Gv1Ima4ZrpDr7+9ODQrsE6rJTZQIDAQABAoGAVeFdEIhiSK61cqGdVJjBZcgJM4ZViaLrvWK2a0ZIV1VTc+ctAP1fcvSlNBVj/BqrhmHdEOCWU9YJvSaPuxSkp/ddnkcCenzd896vWyVjKlOHQB9xhXcfNvKSmkhNrLBS9F/kwTlj3CCWPjr4qhMKLIqxc0M/Njr3vZWlPkLoDAECQQDjt2VwcztH4ccC5CQ7HRv1LUU8ZQAsXsvsvjyGXdAsiKjbRahFDtfyi6m3lyG3qTrgvC0Jogm2GJOrMAJYQUPBAkEAyNGyau/yuMW5n8dkoEGtGcTMefBq7sOfVvHyLkxFwgR6M6BrMU+nwBw6Pk3qb3Y5Vt8w4iRCsP7jZLDGguuopQJAV0aNDGI90DkKa2NBN57afkeRh6o2PMtAYUYwMFd4V/kwromuCnm77mv06jZ0Z83mkQfOQyjli/MzdaL64xRwgQJAXIO+nmVMfLMagPFq+ilHhceUK9kGaljU30k3OP0KZgeKBJ6yw1TfAzH94xYiGpdscwj/jC8ISmIigBCwY0ANwQJBALNTqkYBA31HSVY9keiCM2KjQRbsNF+kGIUizLU19WhqGmmltwo1woa/hY7H1Eu6zDKFPvZj4wd5Fr/Gita9V9M=";
    // 支付宝公钥

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;


    public static void aliPay(final Activity context, final Handler handler,
                              String orderInfo, String sign) {
        LogUtils.t("orderInfo", orderInfo);
        LogUtils.t("sign", sign);

        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * get the sign type we use. 获取签名方式
     */
    public static String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
