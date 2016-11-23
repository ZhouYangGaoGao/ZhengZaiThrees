package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;

/**
 * Created by zqg on 2016/3/1.
 */
public class DzzLiveInfoActivity extends BaseActivity {
    TextView titleText;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_detailinfo);
    }

    @Override
    public void findViewById() {
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText("直播标题");
        findViewById(R.id.img_me).setOnClickListener(this);
        findViewById(R.id.img_search).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_me:
                finish();
                break;
        }

    }
}
