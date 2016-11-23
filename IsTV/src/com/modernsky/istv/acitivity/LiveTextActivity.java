package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Utils;

/**
 * Created by zqg on 2016/6/1.
 */
public class LiveTextActivity extends BaseActivity {
    EditText et;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_text_live_id);
    }

    @Override
    public void findViewById() {
        et = (EditText) findViewById(R.id.et);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                String liveId = et.getText().toString();
                Utils.sendBroadcastToService(1, this);
                Intent intent = new Intent(this, LiveActivity.class);
                intent.putExtra(Constants.VIDEO_ID, "2144");
                intent.putExtra("liveId", liveId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                break;
        }
    }
}
