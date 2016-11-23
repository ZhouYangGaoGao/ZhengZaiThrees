package com.modernsky.istv.acitivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRCodeActivity extends BaseActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    //
    private String userId = "";
    private int userStatus;

    @Override
    protected void onResume() {
        super.onResume();
        isUesrLogined();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setTheme(R.style.MaterialDesign);
        mScannerView = new ZBarScannerView(this);
        mScannerView.setAutoFocus(true);
        setContentView(mScannerView);
    }

    @Override
    public void findViewById() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    /**
     * 判断用户状态，如果未登录，跳转登录界面
     */
    public boolean isUesrLogined() {
        if (UserService.getInatance().isNeedLogin(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return false;
        } else {
            UserEntity bean = UserService.getInatance().getUserBean(this);
            userId = bean.getId();
            userStatus = bean.getStatus();
            return true;
        }
    }

    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();
        //
        String str = result.getContents();
        URL url = null;
        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.w("xqp", url.toString());
        if (!LogUtils.debug) {//正式环境要判断
            if (!(Constants.PROTOCOL + url.getHost()).contains("zhengzai.tv")) {
                Toast.makeText(this, "二维码格式错误", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        final URL url2 = url;
        new AlertDialog.Builder(this)
                .setPositiveButton("确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                RequestParams params = UrlTool.getPostParams(Constants.QR_CODE_TOKEN, url2.getQuery().split("=")[1], Constants.USER_ID, userId);
                                SendActtionTool.post(Constants.UserParams.URL_LOGIN_OTT, ServiceAction.Action_User, UserAction.ACTION_LOGIN_OTT, QRCodeActivity.this, params);
                                finish();
                            }
                        })
                .setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                .setMessage("是否登录OTT？").create().show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        switch ((UserAction) action) {
            case ACTION_LOGIN_OTT:
                Toast.makeText(getApplicationContext(), "OTT登录成功", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        Utils.toast(this, value.toString());
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        Utils.toast(this, "onFaile" + value.toString());
    }
}
