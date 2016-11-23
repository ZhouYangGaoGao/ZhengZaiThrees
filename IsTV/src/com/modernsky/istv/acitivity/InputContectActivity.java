package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.Utils;

/**
 * @author rendy 输入用户信息
 */
public class InputContectActivity extends BaseActivity {
    private EditText count, psd;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerOne_backBtn:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.registerOne_nextBtn:
                String value = count.getText().toString();
                String password = psd.getText().toString();
                Intent intent = getIntent();
                if (!CheckCode.isEmail(value) && !CheckCode.isMobileNO(value)) {
                    Utils.toast(getApplicationContext(), "请正确输入手机或者邮箱");
                    break;
                }
                if (GeneralTool.isEmpty(password) || password.length() < 6
                        || password.length() > 20) {
                    Utils.toast(getApplicationContext(), R.string.input_psd);
                    break;
                }
                intent.putExtra(InputContectActivity.class.getName(), value + "_" + password);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_input_user_contect);
    }

    @Override
    public void findViewById() {
        findViewById(R.id.registerOne_backBtn).setOnClickListener(this);
        findViewById(R.id.registerOne_nextBtn).setOnClickListener(this);
        count = (EditText) findViewById(R.id.registerOne_countEdt);
        psd = (EditText) findViewById(R.id.registerOne_psdEdt);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
