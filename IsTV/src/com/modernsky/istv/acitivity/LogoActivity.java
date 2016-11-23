package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author rendy
 *         <p/>
 *         开机启动页
 */
public class LogoActivity extends BaseActivity {
    private ImageView imageView;
    private boolean isTrue;
    private boolean isBackground;
    private boolean canStartAct;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_start);
        SendActtionTool.get(Constants.URL_GET_START_AD, null, null, this);
    }

    @Override
    public void findViewById() {
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageResource(R.drawable.start_bcg);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                starAct();
                canStartAct = true;
            }
        }, 3000);
        if (!PreferencesUtils.getBooleanPreferences(this, PreferencesUtils.TYPE_FIRST_SETTING)) {
            createShortCut();
            PreferencesUtils.saveBooleanPreferences(this, PreferencesUtils.TYPE_FIRST_SETTING, true);
        }
    }

    private void starAct() {
        if (isFinishing())
            return;
        if (isBackground)
            return;
        if (isTrue) {
            startActivity(new Intent(getApplicationContext(),
                    WelcomeActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(),
                    MainActivity.class));
            LogUtils.d("startActivity(new Intent(MainActivity.class))");
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBackground) {
            isBackground = false;
            if (canStartAct)
                starAct();
        }
    }

    @Override
    protected void onPause() {
        isBackground = true;
        super.onPause();
    }

    public void createShortCut() {
        //创建快捷方式的Intent
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        //需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        //快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        //点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), SplashActivity.class));
        //发送广播。OK
        sendBroadcast(shortcutintent);
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        JSONObject object = (JSONObject) value;
        try {
            JSONArray array = object.getJSONArray(Constants.DATA);
            if (array == null || array.length() == 0) {
                isTrue = false;
                LogUtils.t("-LogoActivity--------", "没有广告" + array.toString()
                        + "_" + array.length());
            } else {
                LogUtils.t("----LogoActivity-----", "有广告" + array.toString()
                        + "_" + array.length());
                isTrue = true;
            }
        } catch (JSONException e) {
        }
    }


    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
        isTrue = false;
    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        super.onException(service, action, value);
        isTrue = false;
    }
}
