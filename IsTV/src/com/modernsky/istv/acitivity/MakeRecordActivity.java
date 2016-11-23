package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.View;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;

public class MakeRecordActivity extends BaseActivity {

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;

        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_makerecord_layout);
    }

    @Override
    public void findViewById() {
        // TODO Auto-generated method stub
        findViewById(R.id.img_back).setOnClickListener(this);
    }

}
