package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.Utils;

/**
 * @author rendy
 *         <p/>
 *         编辑个人信息
 */
public class EditUserInfoActivity extends BaseActivity {
    private TextView titleTet;
    private EditText edit;
    private int tipe;
    private UserEntity userBean;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_back:
                finish();
                break;
            case R.id.edit_save:
                sendAction();
                break;
            default:
                break;
        }
    }

    private void sendAction() {
        String value = edit.getText().toString();
        switch (tipe) {
            // 更改邮箱
            case R.string._emial:
                if (CheckCode.isEmail(value)) {
                    SendActtionTool.get(UserParams.URL_USER_UPDATE, null,
                            UserAction.Action_Update_Email, this, UrlTool
                                    .getParams(UserParams.KEY, UserParams.EMAIL,
                                            UserParams.VALUE, value,
                                            Constants.USER_ID, userBean.getId()));
                } else {
                    Utils.toast(getApplicationContext(), "请输入正确的邮箱");
                }
                break;
            // 更改名字
            case R.string._name:
                if (!GeneralTool.isEmpty(value)) {
                    SendActtionTool.get(UserParams.URL_USER_UPDATE, null,
                            UserAction.Action_Update_UserName, this, UrlTool
                                    .getParams(UserParams.KEY, UserParams.USERNAME,
                                            UserParams.VALUE, value,
                                            Constants.USER_ID, userBean.getId()));
                } else {
                    Utils.toast(getApplicationContext(), "请输入姓名");
                }
                break;
            // 更该 电话
            case R.string._phone:
                if (CheckCode.isMobileNO(value)) {
                    SendActtionTool.get(UserParams.URL_USER_UPDATE, null,
                            UserAction.Action_Update_Mobiles, this, UrlTool
                                    .getParams(UserParams.KEY, Constants.MOBILE,
                                            UserParams.VALUE, value,
                                            Constants.USER_ID, userBean.getId()));
                } else {
                    Utils.toast(getApplicationContext(), "请输入正确的手机号");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edituserinfo);
        userBean = UserService.getInatance().getUserBean(this);
    }

    @Override
    public void findViewById() {
        tipe = getIntent().getIntExtra(EditUserInfoActivity.class.getName(),
                R.string._name);
        edit = (EditText) findViewById(R.id.edit_editEdt);
        titleTet = (TextView) findViewById(R.id.edit_title);
        titleTet.setText(tipe);
        initHint();
        findViewById(R.id.edit_save).setOnClickListener(this);
        findViewById(R.id.edit_back).setOnClickListener(this);
    }

    private void initHint() {

        switch (tipe) {
            // 更改邮箱
            case R.string._emial:
                edit.setText(userBean.getEmail());
                edit.setHint("请输入邮箱");
                break;
            // 更改名字
            case R.string._name:
                if (!TextUtils.isEmpty(userBean.getUserName()))
                    edit.setText(userBean.getUserName());
                edit.setHint("请输入姓名");
                break;
            // 更该 电话
            case R.string._phone:
                edit.setText(userBean.getMobile());
                edit.setHint("请输入电话");
                break;

            default:
                break;
        }

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        String modify = edit.getText().toString();
        switch ((UserAction) action) {
            case Action_Update_Email:
                userBean.setEmail(modify);
                break;
            case Action_Update_UserName:
                userBean.setUserName(modify);
                break;
            case Action_Update_Mobiles:
                userBean.setMobile(modify);
                break;
            default:
                break;
        }
        UserService.getInatance().setUserBean(userBean, this);
        finish();
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        Utils.toast(getApplicationContext(), value.toString());
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
    }

}
