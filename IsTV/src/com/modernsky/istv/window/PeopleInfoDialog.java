package com.modernsky.istv.window;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.listener.CommonListener;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.RoundAngleImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zqg on 2016/3/11.
 */
public class PeopleInfoDialog extends Dialog implements View.OnClickListener, CommonListener {
    boolean isAnchoredEnter;
    TextView levelText;
    TextView nameText;
    ImageView sexImg;
    TextView jobText;
    TextView placeText;
    TextView powerNum;
    TextView fansNum;
    TextView focuseNum;
    TextView focuseBtn;
    TextView hasFocusBtn;
    RoundAngleImageView picImg;
    private boolean isAttention;
    private Context context;
    private String userId;

    private UserEntity currentUserEntity;

    public PeopleInfoDialog(Context context) {
        super(context);
    }

    public PeopleInfoDialog(Context context, String userID) {
        super(context, R.style.peopleInfo_dialog);
        this.userId = userID;
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_audiences);
        setCanceledOnTouchOutside(true);
        getContent(userId);
        initClose();
    }

    private void initClose() {
        findViewById(R.id.img_delete).setOnClickListener(this);
    }

    private void initView() {
        isAttention = (currentUserEntity.getIsAttention() == 1);
        picImg = (RoundAngleImageView) findViewById(R.id.img_pic_dialog);
//        priseImg = (ImageView) findViewById(R.id.img_heart);
        levelText = (TextView) findViewById(R.id.tv_level_dialog);
        nameText = (TextView) findViewById(R.id.tv_name_people_dialog);
        sexImg = (ImageView) findViewById(R.id.img_sex_people);
        jobText = (TextView) findViewById(R.id.tv_job_people_dialog);
        placeText = (TextView) findViewById(R.id.tv_location_people_dialog);
        powerNum = (TextView) findViewById(R.id.tv_num_stareberry_dialog);
        fansNum = (TextView) findViewById(R.id.tv_num_fans_dialog);
        focuseNum = (TextView) findViewById(R.id.tv_num_focus_dialog);
        focuseBtn = (TextView) findViewById(R.id.tv_focuspeople_dialog);
        focuseBtn.setOnClickListener(this);
        hasFocusBtn = (TextView) findViewById(R.id.tv_hasfocus_dialog);
//        priseImg.setOnClickListener(this);
        hasFocusBtn.setOnClickListener(this);

        if (currentUserEntity.getStatus() != 6) {
            levelText.setBackgroundResource(R.drawable.icon_huizhang);
        } else {
            levelText.setBackgroundResource(R.drawable.dzz_0home_icon_levelbg);
        }
//        jobText.setVisibility(View.VISIBLE);
        BitmapTool.getInstance().getAdapterUitl().display(picImg, currentUserEntity.getFaceUrl());
        levelText.setText(currentUserEntity.getRank().getRank());
        nameText.setText(currentUserEntity.getUserName());
        LogUtils.d("getUserName-----" + currentUserEntity.getUserName());
        if (currentUserEntity.getSex() == 1) {
            //1 男  0 女  -1 保密
            sexImg.setImageResource(R.drawable.icon_man);
        } else if (currentUserEntity.getSex() == 0) {
            sexImg.setImageResource(R.drawable.icon_woman);
        } else {
            sexImg.setImageResource(R.drawable.icon_secret_sex);
        }
        jobText.setText(currentUserEntity.getBadge().getName());
        LogUtils.d("getBadge-----" + currentUserEntity.getBadge().getName());
        if (TextUtils.isEmpty(currentUserEntity.getAddress())) {
            placeText.setVisibility(View.INVISIBLE);
        } else {
            placeText.setVisibility(View.VISIBLE);
        }
        placeText.setText(currentUserEntity.getAddress());
        powerNum.setText(String.valueOf(currentUserEntity.getStrawCount()));
        fansNum.setText(String.valueOf(currentUserEntity.getFansCount()));
        focuseNum.setText(String.valueOf(currentUserEntity.getAttentionCount() ));
        initBtn();
    }

    private void initBtn() {
        if (isAttention) {
            hasFocusBtn.setVisibility(View.VISIBLE);
            focuseBtn.setVisibility(View.GONE);
        } else {//没有关注
            focuseBtn.setVisibility(View.VISIBLE);
            hasFocusBtn.setVisibility(View.GONE);
//            if (isAnchoredEnter) {
////                focuseBtn.setText("关注TA");
//            } else {
////                focuseBtn.setText("关注TA");
//            }

        }
    }

    private void goAttention() {
        SendActtionTool.post(Constants.UserParams.URL_ADD_ATTENTION, null, UserAction.ACTION_USER_ATTENTION, this,
                UrlTool.getPostParams(Constants.USER_ID, UserService.getInatance().getUserBean(context).getId(), Constants.TO_USER_ID,
                        currentUserEntity.getId()));
    }

    //发起网络请求r
    private void getContent(String userId) {
        LogUtils.d("livepublish-----getContent--------");
        SendActtionTool.post(Constants.UserParams.URL_GET_ONE, null, UserAction.ACTION_GET_USERENTITY, this, UrlTool
                .getPostParams(Constants.USER_ID, userId, Constants
                        .UserParams.TERMINAL, "MOBILE"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_delete:
                dismiss();
                break;
            // true 关注   false进入主页
            case R.id.tv_focuspeople_dialog:  //纯文字
            case R.id.tv_hasfocus_dialog:  //有心的图片
                if (UserService.getInatance().isNeedLogin(context)) {
                    DialogTool.createToLoginDialog(context);
                } else {
                    goAttention();
                }
                break;

        }
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        LogUtils.d("peopleunfo2 value====" + value.toString());
        switch ((UserAction) action) {


            case ACTION_GET_USERENTITY://获取某人的信息
                LogUtils.d("个人主页加载数据成功 ");
                JSONObject jObject = (JSONObject) value;
                try {
                    currentUserEntity = JSON.parseObject(jObject.getString(Constants.USER_ENTITY), UserEntity.class);
                    initView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_USER_ATTENTION:
                //关注成功
                try {
                    int attention = ((JSONObject) value).getJSONObject("data").getInt("isAttention");
                    isAttention = (attention == 1);
                    // 1 关注 2 取消关注
                    if (isAttention) {
                        Utils.toast(context, "关注成功");
                    } else {
                        Utils.toast(context, "取消关注成功");
                    }
                    initBtn();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {

    }

    @Override
    public void onStart(ServiceAction service, Object action) {
        ((BaseActivity) context).showLoadingDialog();
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        ((BaseActivity) context).dismissDialog();
    }
}
