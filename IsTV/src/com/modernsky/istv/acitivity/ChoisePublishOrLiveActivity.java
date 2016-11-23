package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.view.RoundAngleImageView;

/**
 * Created by zqg on 2016/3/2.
 */
public class ChoisePublishOrLiveActivity extends BaseActivity {
    private TextView peopleLevel;
    private TextView peopleName;
    private RoundAngleImageView imageview;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choisepublishorlive);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.btn_public).setOnClickListener(this);
        findViewById(R.id.btn_beginlive).setOnClickListener(this);
        findViewById(R.id.img_close).setOnClickListener(this);
        peopleLevel = (TextView) findViewById(R.id.tv_level);
        peopleName = (TextView) findViewById(R.id.tv_name_anchor);
        imageview = (RoundAngleImageView) findViewById(R.id.peoplePic);
        UserEntity.RankEntity rank = UserService.getInatance().getUserBean(this).getRank();
        if (rank != null) {
            peopleLevel.setText(UserService.getInatance().getUserBean(this).getRank().getRank());
        } else {
            peopleLevel.setText("1");
        }
        peopleName.setText(UserService.getInatance().getUserBean(this).getUserName());
        BitmapTool.getInstance().getAdapterUitl().display(imageview, UserService.getInatance().getUserBean(this).getFaceUrl());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_public:
                startActivity(new Intent(this, PublishActivity.class));
                finish();
//                Utils.toast(this,"开始发布");
                break;
            case R.id.btn_beginlive:
//                Utils.toast(this, "开始直播");
                startActivity(new Intent(this, LivePublishActivity.class));
                finish();
                break;
            case R.id.img_close:
                finish();
                break;

        }
    }
}
