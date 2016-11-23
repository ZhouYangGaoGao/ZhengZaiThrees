package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.unity.GoogleUnityActivity;
import com.modernsky.istv.utils.LogUtils;
import com.unity3d.player.UnityPlayer;

public class VrPlayerActivity extends GoogleUnityActivity {
    public static GoogleUnityActivity Instance;
    private static GoogleUnityActivity sActivity;
    private UnityPlayer mUnityPlayer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sActivity = this;
        Instance = this;

        this.mUnityPlayer = new UnityPlayer(this);
        setContentView(this.mUnityPlayer);
        this.mUnityPlayer.requestFocus();


        // 播放参数
        Bundle bundle = this.getIntent().getExtras();

        String Name = bundle.getString("Name", "");
        String path = bundle.getString("Path", "");
        String GlassesType = bundle.getString("GlassesType", "");
        String ScreenType = bundle.getString("ScreenType", "");
        LogUtils.i("_______" + path + "_______" + Name + "_______" + GlassesType + "_______" + ScreenType);

        UnityPlayer.UnitySendMessage("PluginListener", "UPANO_PLAYER_TITLE", Name);
        UnityPlayer.UnitySendMessage("PluginListener", "UPANO_PLAYER_LOCATION", path);
        UnityPlayer.UnitySendMessage("PluginListener", "UPANO_PLAYER_GLASSES", GlassesType);
        UnityPlayer.UnitySendMessage("PluginListener", "UPANO_PLAYER_DISPLAY", ScreenType);
    }

    public void PlayEnd(String _msg) {
        LogUtils.i("______" + _msg);
    }

    public void BackMenu(String _msg) {
//       Utils.sendBroadcastToService(8, this);
        Intent intent = new Intent(VrPlayerActivity.this, MainActivity.class);
        startActivity(intent);
        VrPlayerActivity.this.finish();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BackMenu("");
    }
}
