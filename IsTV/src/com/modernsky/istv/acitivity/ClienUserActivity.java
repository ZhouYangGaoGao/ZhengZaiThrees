package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.manager.DavikActivityManager;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.Constants.UserParams;

/**
 * 当前手机用户个人中心
 */
public class ClienUserActivity extends BaseActivity {
    private UserEntity userBean;
    private EditText edtPhone, edtEmail, edtPassword, edtName, etIntro;
    private TextView btnEmali, btnPsd, btnPhone;
    private TextView nan, nv, secret;
    private Button btn_intro;


    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_client);
    }

    private void setError(EditText targetView, int lt, int rt, int bm) {
        Drawable right = rt == -1 ? null : GeneralTool
                .setNearDrawable(rt, this);
        Drawable bottom = bm == -1 ? null : GeneralTool.setNearDrawable(bm,
                this);
        Drawable left = lt == -1 ? null : GeneralTool.setNearDrawable(lt, this);
        targetView.setCompoundDrawables(left, null, right, bottom);
    }

    @Override
    public void findViewById() {
        getView(R.id.register_userBtn).setOnClickListener(this);
        userBean = UserService.getInatance().getUserBean(this);
        edtPhone = (EditText) findViewById(R.id.register_phoneEdt);
        edtEmail = (EditText) findViewById(R.id.register_emailEdt);
        edtName = (EditText) findViewById(R.id.userName);
        etIntro = (EditText) findViewById(R.id.et_intro);
        btn_intro = (Button) findViewById(R.id.btn_intro);

        btnPhone = getView(R.id.register_Btnphone);
        btnEmali = getView(R.id.register_emailBtn);
        btnPsd = getView(R.id.register_codeBtn);


        //
        btnPhone.setOnClickListener(this);
        btnEmali.setOnClickListener(this);
        btnPsd.setOnClickListener(this);
        btn_intro.setOnClickListener(this);
        // 修改性别
        nv = getView(R.id.btnGirl);
        nv.setOnClickListener(this);
        nan = getView(R.id.btnNan);
        nan.setOnClickListener(this);
        secret = getView(R.id.btnSecret);
        secret.setOnClickListener(this);

        if (!TextUtils.isEmpty(userBean.getEmail())) {
            edtEmail.setEnabled(false);
            edtEmail.setHint(userBean.getEmail());
            btnEmali.setVisibility(View.GONE);

        }

        if (!"".equals(userBean.getMobile())) {
            edtPhone.setHint(userBean.getMobile());
            btnPhone.setVisibility(View.GONE);

        } else {
            btnPhone.setVisibility(View.VISIBLE);
            edtPhone.setEnabled(true);
        }

        edtName.setText(userBean.getUserName());

        switch (userBean.getSex()) {
            case 1:
                setnormal(nan, R.drawable.icon_manchoosen);

                break;
            case 0:

                setnormal(nv, R.drawable.icon_woman);

                break;
            case -1:
                setnormal(secret, R.drawable.icon_secret_sex);
                break;

            default:
                break;
        }

        etIntro.setText(userBean.getSign());


    }

    private void setnormal(TextView view, int icon) {
        Drawable drawable2 = getResources().getDrawable(icon);
        // / 这一步必须要做,否则不会显示.
        if (drawable2!=null) {
            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),
                    drawable2.getMinimumHeight());
            view.setCompoundDrawables(drawable2, null, null, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_userBtn:
                String value1 = edtName.getText().toString();
                if (value1.trim().equals(userBean.getUserName().trim())) {
                    break;
                }
                if (TextUtils.isEmpty(value1)
                        || Utils.calculateWeiboLength(value1) > 8
                        || Utils.calculateWeiboLength(value1) < 1) {
                    Toast.makeText(getApplicationContext(), "用户名字长度1-8位", Toast.LENGTH_LONG)
                            .show();
                    break;
                }
                setUpdateDate("username", value1, UserAction.Action_CHECK_VERSION);
                break;
            // 手机绑定
            case R.id.register_Btnphone:
                startActivity(new Intent(getApplicationContext(), BingPhoneActivity.class));
                break;
            // 邮箱绑定
            case R.id.register_emailBtn:
                String value = edtEmail.getText().toString();
                if (!CheckCode.isEmail(value)) {
                    Toast.makeText(getApplicationContext(), "请输入正确的邮箱", Toast.LENGTH_LONG).show();
                    break;
                }
                setUpdateDate("email", value, UserAction.Action_CHECK_ONE);
                break;
            // 密码修改
            case R.id.register_codeBtn:
                // Intent intent = new Intent(getApplication(),
                // ResetPsdActivity.class);
                // intent.putExtra(ResetPsdActivity.class.getName(), 1);
                // startActivity(intent);
                Intent intent = new Intent(getApplication(), ChangeActivity.class);
                startActivity(intent);
                break;
            //简介修改
            case R.id.btn_intro:
                String intro = etIntro.getText().toString();
                if (intro.trim().equals(userBean.getSign().trim())) {
                    break;
                }
                setUpdateDate("sign", intro, UserAction.ACTION_CHECK_SIGN);
                break;
            // 性别男
            case R.id.btnNan:
                setUpdateDate("sex", "1", UserAction.Action_BINDING);
                setnormal(nan, R.drawable.icon_manchoosen);
                setnormal(nv, R.drawable.icon_womanunchoosen);
                setnormal(secret, R.drawable.icon_secretsex_gray);
                userBean.setSex(1);
                break;
            // 性别美女
            case R.id.btnGirl:
                setUpdateDate("sex", "0", UserAction.Action_BINDING);
                setnormal(nan, R.drawable.icon_manunchoosen);
                setnormal(nv, R.drawable.icon_woman);
                setnormal(secret, R.drawable.icon_secretsex_gray);
                userBean.setSex(0);
                break;
            // 性别保密
            case R.id.btnSecret:
                setUpdateDate("sex", "-1", UserAction.Action_BINDING);
                setnormal(nan, R.drawable.icon_manunchoosen);
                setnormal(nv, R.drawable.icon_womanunchoosen);
                setnormal(secret, R.drawable.icon_secret_sex);
                userBean.setSex(-1);

                break;
            default:
                break;
        }
    }

    public void backBtn(View view) {
        finish();
    }

    /**
     * 更新信息
     */
    private void setUpdateDate(String params, String newVlaue, UserAction action) {
        // http://182.92.167.30/m/user/update?userId=5552e8810cf25aa30080683c&key=userName&value=111111&code=0&oldPassword=""
        RequestParams req = UrlTool.getPostParams("userId", userBean.getId(),
                "key", params, "value", newVlaue, "code", "0", "oldPassword",
                "");
        SendActtionTool.post(UserParams.URL_USER_UPDATE,
                ServiceAction.Action_User, action, this, req);
        showLoadingDialog();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        switch ((UserAction) action) {
            // 性别
            case Action_BINDING:
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                UserService.getInatance().setUserBean(userBean, this);
                break;
            // 修改邮箱
            case Action_CHECK_ONE:
                Toast.makeText(getApplicationContext(), "已向您的邮箱发送验证链接\n请查收邮件完成绑定",
                        Toast.LENGTH_SHORT).show();
                userBean.setEmail(edtEmail.getText().toString());
                btnEmali.setVisibility(View.GONE);
                edtEmail.setEnabled(false);
                UserService.getInatance().setUserBean(userBean, this);
                break;
            // 修改名字
            case Action_CHECK_VERSION:
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                userBean.setUserName(edtName.getText().toString());
                UserService.getInatance().setUserBean(userBean, this);
                break;
            case ACTION_CHECK_SIGN:
                Utils.toast(this, "修改成功");
                userBean.setSign(etIntro.getText().toString());
                UserService.getInatance().setUserBean(userBean, this);
                break;
            default:
                break;
        }
        Intent intent2 = new Intent();
        intent2.setAction(Constants.ACTION_USERBEAN_CHANGE);
        DavikActivityManager.getScreenManager().currentActivity()
                .sendBroadcast(intent2);
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        Toast.makeText(getApplicationContext(), value.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        dismissDialog();
    }
}
