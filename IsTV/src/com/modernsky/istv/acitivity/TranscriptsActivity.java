package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.UserAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.PaihangBean;
import com.modernsky.istv.bean.TranscriptsBean;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.choiseCity.BitmapUtil;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LocalCacheUtil;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.PopThreeShare;
import com.modernsky.istv.view.RoundAngleImageView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by FQY on 16/3/1.
 * 成绩单act
 */
public class TranscriptsActivity extends BaseActivity {
    RoundAngleImageView img_user;
    TextView tv_lv_left;
    TextView tv_live_name;
    TextView tv_user_name;
    TextView tv_meilizhi;
    TextView tv_count;
    TextView tv_like;
    TextView tv_caomei;
    ListView listview;
    ImageView img_ewm;
    View ll_share;
    private UserEntity userEntity;
    private PopThreeShare popThreeShare;
    private List<PaihangBean> userEntityList;
    private CommonAdapter<PaihangBean> commonAdapter;

    private void updateView(TranscriptsBean bean) {
        BitmapTool.getInstance().getAdapterUitl().display(img_user, userEntity.getFaceUrl());
        tv_lv_left.setText(userEntity.getRank().getRank());
        if (userEntity.getHerald() != null) {
            tv_live_name.setText(userEntity.getHerald().getName());
        }
        tv_user_name.setText(userEntity.getUserName());
        tv_meilizhi.setText(String.valueOf(bean.getExper() ));
        tv_count.setText(String.valueOf(bean.getViewCount() ));
        tv_like.setText(String.valueOf(bean.getPraiseCount() ));
//        tv_mb.setText(bean.getMbCount() + "");
        tv_caomei.setText(String.valueOf(bean.getStrawCount()));
        userEntityList.clear();
        userEntityList.addAll(bean.getRank());
        commonAdapter.notifyDataSetChanged();
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.a_transcripts);
        userEntity = UserService.getInatance().getUserBean(this);
        popThreeShare = new PopThreeShare(this);
    }

    @Override
    public void findViewById() {
        img_user= (RoundAngleImageView) findViewById(R.id.img_user);
        tv_lv_left= (TextView) findViewById(R.id.tv_lv_left);
        tv_live_name= (TextView) findViewById(R.id.textView7);
        tv_user_name= (TextView) findViewById(R.id.textView);
        tv_meilizhi= (TextView) findViewById(R.id.textView9);
        tv_count= (TextView) findViewById(R.id.tv_count);
        tv_like= (TextView) findViewById(R.id.tv_like);

        tv_caomei= (TextView) findViewById(R.id.tv_caomei);
        listview= (ListView) findViewById(R.id.listview);
        img_ewm= (ImageView) findViewById(R.id.img_ewm);
        ll_share=  findViewById(R.id.ll_share);
        findViewById(R.id.tv_wx).setOnClickListener(this);
        findViewById(R.id.tv_qq).setOnClickListener(this);
        findViewById(R.id.tv_pyq).setOnClickListener(this);
        findViewById(R.id.tv_wb).setOnClickListener(this);
        findViewById(R.id.imgbtn_complete).setOnClickListener(this);
        userEntityList = new ArrayList<PaihangBean>();
        commonAdapter = new CommonAdapter<PaihangBean>(this, userEntityList, R.layout.item_transcripts_rank) {
            @Override
            public void convert(ViewHolder helper, final PaihangBean item) {

                if (helper.getPosition() == 0) {
                    helper.setImageResource(R.id.item_img_index, R.drawable.dzz_report_toplist_1);
                } else if (helper.getPosition() == 1) {
                    helper.setImageResource(R.id.item_img_index, R.drawable.dzz_report_toplist_2);
                } else if (helper.getPosition() == 2) {
                    helper.setImageResource(R.id.item_img_index, R.drawable.dzz_report_toplist_3);
                }

                helper.setImageByUrl(R.id.item_img_user, item.getUser().getFaceUrl());
                helper.setText(R.id.item_tv_username, item.getUser().getUserName());
                helper.setText(R.id.item_tv_count, item.getMbCount() + "M豆");
//                TextView view = helper.getView(R.id.item_tv_username);
                if (item.getUser().getSex() == 1) {
                    helper.setImageResource(R.id.item_img_sex, R.drawable.icon_man);
                } else if (item.getUser().getSex() == 0) {
                    helper.setImageResource(R.id.item_img_sex, R.drawable.icon_woman);
                } else
                    helper.setImageResource(R.id.item_img_sex, R.drawable.icon_secret_sex);

                if (item.getUser().getStatus() == 6) {
                    helper.setBackgroundResource(R.id.item_tv_lable, R.drawable.dzz_0home_icon_levelbg);
                } else {
                    helper.setBackgroundResource(R.id.item_tv_lable, R.drawable.icon_huizhang);
                }
                if (item.getUser().getRank() != null) {
                    helper.setText(R.id.item_tv_lable, item.getUser().getRank().getRank());
                }
                final TextView attention = helper.getView(R.id.item_tv_index);
                if (item.getUser().getIsAttention() == 0) {
                    attention.setText("关注");
                    attention.setTextColor(Color.parseColor("#24e7a9"));
                    attention.setBackgroundResource(R.drawable.but_guanzhu_hl);
                } else {
                    attention.setText("已关注");
                    attention.setTextColor(Color.parseColor("#808282"));
                    attention.setBackgroundResource(R.drawable.but_guanzhu);
                }
                helper.setOnClickListener(R.id.item_tv_index, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserAction.ACTION_USER_ATTENTION.value = attention;
                        SendActtionTool.post(Constants.UserParams.URL_ADD_ATTENTION, null, UserAction.ACTION_USER_ATTENTION, TranscriptsActivity.this, UrlTool.getPostParams(Constants.USER_ID, userEntity.getId(), Constants.TO_USER_ID, item.getUser().getId()));
                    }
                });
                helper.getView(R.id.item_img_user).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.OpenUserInfo(TranscriptsActivity.this, item.getUser().getId(), "1");
                    }
                });
//                helper.getConvertView().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Utils.OpenUserInfo(TranscriptsActivity.this, item.getUser().getId(), "1");
//                    }
//                });

            }
        };
        listview.setAdapter(commonAdapter);
        getContent();
    }

    private void getContent() {
        String activityId = getIntent().getStringExtra(Constants.ACTIVITY_ID);
        String chatRoomId = getIntent().getStringExtra(Constants.CHATROOM_ID);
        RequestParams params = UrlTool.getPostParams(Constants.ACTIVITY_ID, activityId, Constants.CHATROOM_ID, chatRoomId);
        SendActtionTool.post(Constants.UserParams.URL_LIVE_STOP, null, UserAction.ACTION_LIVE_STOP, this, params);
        showLoadingDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_wx:
                cutScreenandShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.tv_pyq:
                cutScreenandShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.tv_qq:
                cutScreenandShare(SHARE_MEDIA.QQ);
                break;
            case R.id.tv_wb:
                cutScreenandShare(SHARE_MEDIA.SINA);
                break;
            case R.id.imgbtn_complete:
//                if (isExceed == 1 && !hasOpenDialog) {
//                    DialogTool.createToDeleteDialog(TranscriptsActivity.this, videoId);
//                    hasOpenDialog = true;
//                } else
                onBackPressed();

                break;
        }
    }


    @Override
    public void onBackPressed() {
        Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
        if (ll_share.getVisibility() != View.VISIBLE) {
            ll_share.setVisibility(View.VISIBLE);
            img_ewm.setVisibility(View.GONE);
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void cutScreenandShare(final SHARE_MEDIA platform) {
        ll_share.setVisibility(View.GONE);
        img_ewm.setVisibility(View.VISIBLE);
        final String path = LocalCacheUtil.pictureFilePath.getPath() + "/screen.png";
        img_ewm.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!BitmapUtil.getScreenHot(getWindow().getDecorView(), path)) {
                    return;
                }

                popThreeShare.setShareImg("正在现场", getRandomContent(), path, platform, new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ll_share.setVisibility(View.VISIBLE);
                        img_ewm.setVisibility(View.GONE);
                        Utils.toast(TranscriptsActivity.this, " 分享成功啦");
                        PreferencesUtils.saveLongPreferences(TranscriptsActivity.this, PreferencesUtils
                                .TYPE_SHARE_TIME, System.currentTimeMillis());
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ll_share.setVisibility(View.VISIBLE);
                        img_ewm.setVisibility(View.GONE);
                        Utils.toast(TranscriptsActivity.this, " 分享失败啦");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ll_share.setVisibility(View.VISIBLE);
                        img_ewm.setVisibility(View.GONE);
                        Utils.toast(TranscriptsActivity.this, " 分享取消啦");
                    }
                });
            }
        }, 500);


    }
    /**
     * 随机生成分享内容
     *
     * @param
     * @return
     */
    private String getRandomContent() {

        String[] strs = getResources().getStringArray(R.array.shareContent_transcripts);

        Random random = new Random();
        int num = random.nextInt(3);

        return strs[num];
    }
    @Override
    protected void onResume() {
        ll_share.setVisibility(View.VISIBLE);
        img_ewm.setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        switch ((UserAction) action) {
            case ACTION_LIVE_STOP:
                try {
                    JSONObject object = (JSONObject) value;
                    TranscriptsBean temp = JSON.parseObject(object.getString(Constants.DATA), TranscriptsBean.class);
                    if (temp != null) {
                        updateView(temp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_USER_ATTENTION:
                try {
                    JSONObject object = (JSONObject) value;
                    int isAttention = Integer.parseInt(object.getJSONObject(Constants.DATA).getString("isAttention"));
                    TextView attention = (TextView) UserAction.ACTION_USER_ATTENTION.value;
                    if (isAttention == 0) {
                        attention.setText(" 关注 ");
                        attention.setTextColor(Color.parseColor("#24e7a9"));
                        attention.setBackgroundResource(R.drawable.but_guanzhu_hl);
                        Utils.toast(TranscriptsActivity.this, " 取消关注成功");
                    } else {
                        attention.setText("已关注");
                        attention.setTextColor(Color.parseColor("#808282"));
                        attention.setBackgroundResource(R.drawable.but_guanzhu);
                        Utils.toast(TranscriptsActivity.this, " 关注成功");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        if (value != null) {
            Utils.toast(this, value.toString());
        }
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        dismissDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && popThreeShare != null) {
            popThreeShare.setSinaWeibo(requestCode, resultCode, data);
        }
    }

}
