package com.modernsky.istv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.modernsky.istv.utils.ThreeAppParams;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class AppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);

        api.registerApp(ThreeAppParams.WX_APP_ID);
    }
}
