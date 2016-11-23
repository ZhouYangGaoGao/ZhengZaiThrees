package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.Utils;

/**
 * @author rendy 注册第一步
 */
public class RegisterOneActivity extends BaseActivity {
    private EditText count;
    private TextView text;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerOne_backBtn:
                finish();
                break;
            case R.id.registerOne_nextBtn:
                String value = count.getText().toString();
                check(value);
                Intent intent = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                intent.putExtra(RegisterOneActivity.class.getName(), value);
                if (CheckCode.isMobileNO(value)) {
                    intent.putExtra(RegisterActivity.class.getName(), 1);
                    startActivity(intent);
                } else {
                    Utils.toast(getApplicationContext(), "请正确输入手机号");
                }
//                if (CheckCode.isEmail(value)) {
//                    intent.putExtra(RegisterActivity.class.getName(), 2);
//                    startActivity(intent);
//                } else if (CheckCode.isMobileNO(value)) {
//                    intent.putExtra(RegisterActivity.class.getName(), 1);
//                    startActivity(intent);
//                } else {
//                    Utils.toast(getApplicationContext(), "请正确输入手机或者邮箱");
//                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_one);
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
        text = getView(R.id.register_codeEroTet);
        findViewById(R.id.registerOne_backBtn).setOnClickListener(this);
        findViewById(R.id.registerOne_nextBtn).setOnClickListener(this);
        count = (EditText) findViewById(R.id.registerOne_countEdt);
    }

    private void check(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (CheckCode.isMobileNO(s)) {
//            if (CheckCode.isEmail(s) || CheckCode.isMobileNO(s)) {
//                setError(count, R.drawable.edit_count_select, -1,
//                        R.drawable.edit_line_select);
                setError(count, R.drawable.edit_phone_select, -1,
                        R.drawable.edit_line_select);
                text.setVisibility(View.INVISIBLE);

            } else {
//                setError(count, R.drawable.edit_phone_select,
//                        R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
                setError(count, R.drawable.edit_phone_select,
                        -1, R.drawable.ling_0_1_wrong);
                text.setVisibility(View.VISIBLE);
            }
        } else {
            setError(count, R.drawable.edit_phone_select,
                    -1, R.drawable.ling_0_1_wrong);
//            setError(count, R.drawable.edit_count_select,
//                    R.drawable.icon_0_1_wrong, R.drawable.ling_0_1_wrong);
            text.setVisibility(View.VISIBLE);
        }
    }
}
