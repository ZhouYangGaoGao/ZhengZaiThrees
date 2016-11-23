package com.modernsky.istv.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.action.RankAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.RechargeBean;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author rendy 购买礼物的界面
 */
public class BuyGiftActivity extends BaseActivity {
    private GridView gridView;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_buygift);
    }

    @Override
    public void findViewById() {
        ImageButton btn_Back = (ImageButton) findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(this);
        gridView = getView(R.id.gridView);
        SendActtionTool.post(UserParams.URL_RECHARGE_LIST, ServiceAction.Acion_Rank, RankAction.Action_month, this);
        showLoadingDialog();
    }

    private void update(final List<RechargeBean> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        gridView.setAdapter(new CommonAdapter<RechargeBean>(BuyGiftActivity.this, datas, R.layout.item_buygift) {
            @Override
            public void convert(ViewHolder helper, RechargeBean item) {
                TextView tetMb = helper.getView(R.id.num_MB);
                TextView tetgivi = helper.getView(R.id.num_send);
                TextView tetRmb = helper.getView(R.id.tv_money);
                tetMb.setText(item.getMb());
                tetgivi.setText(String.valueOf(item.getGiveMB()));
                tetRmb.setText("¥ " + item.getMoney());
            }
        });
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.startBuyMB(BuyGiftActivity.this, datas.get(position));
            }
        });
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        JSONObject obj = (JSONObject) value;
        List<RechargeBean> datas;
        try {
            datas = JSON.parseArray(obj.getJSONArray("data").toString(), RechargeBean.class);
            update(datas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        super.onFaile(service, action, value);
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        dismissDialog();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent intent) {
        super.onActivityResult(arg0, arg1, intent);
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equals(Constants.ACTION_PAY_RESULT)) {
            dismissDialog();
            Utils.sendBroadcastToMainactivityToUpdateUserinfo(this);
            int intExtra = intent.getIntExtra(Constants.ACTION_PAY_RESULT, -2);
            if (intExtra == 0) {
                Utils.toast(BuyGiftActivity.this, R.string.pay_success);
            } else
                Utils.toast(BuyGiftActivity.this, R.string.pay_fail);
        }
    }
}
