package com.modernsky.istv.acitivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.modernsky.istv.BaseActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * zhouyang 打榜说明页
 */
public class DabangAboutActivity extends BaseActivity {
    private ListView listView;
    private CommonAdapter adapter;
    private List<String> list;
    private List<String> list2;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dabang_about);
    }

    @Override
    public void findViewById() {
        list =new ArrayList<>();
        list2 = new ArrayList<>();
        list.add("上榜规则：");
        list.add("榜单周期：");
        list.add("奖励：");


        list2.add("人气值最高得分为10000分，通过以下各项数值综合运算得出：\n" +
                "\n" +
                "观看量：40%\n" +
                "\n" +
                "分享次数：20%（单个用户对单个视频仅计算一次）\n" +
                "\n" +
                "点赞数量：20%\n" +
                "\n" +
                "收获礼物数量：20%");
        list2.add("周榜单：每周日24：00结束统计，周一12：00公布榜单\n" +
                "\n" +
                "月榜单：每月24：00结束统计，下月第一天20：00公布\n" +
                "\n" +
                "季榜单：三月榜单结束后一天20：00公布");
        list2.add("周达人：\n" +
                "\n" +
                "第一名： 草莓音乐节单日门票兑换券，草莓音乐节直播礼包\n" +
                "\n" +
                "第二名： Livehouse演出门票1张\n" +
                "\n" +
                "第三名： 草莓音乐节直播礼包1份\n" +
                "\n" +
                "月达人：\n" +
                "\n" +
                "第一名： 专场演出一场（含直播）\n" +
                "                 摩登天空专访推送一条\n" +
                "                 草莓音乐节套票兑换券\n" +
                "\n" +
                "第二名： 摩登天空专访推送一条\n" +
                "                 草莓音乐节套票兑换券\n" +
                "                 草莓音乐节直播礼包1份\n" +
                "\n" +
                "第三名：草莓音乐节套票兑换券\n" +
                "                草莓音乐节直播礼包1份\n" +
                "\n" +
                "\n" +
                "季达人：\n" +
                "\n" +
                "第一名：有机会签约摩登天空\n" +
                "                有机会登上草莓音乐节舞台\n" +
                "                专场演出一场（含直播）\n" +
                "                摩登天空专访推送1条\n" +
                "                摩登天空周边礼包1份\n" +
                "\n" +
                "第二名：有机会签约摩登天空\n" +
                "                有机会登上草莓音乐节舞台\n" +
                "                专场演出1场（含直播）\n" +
                "                摩登天空专访推送1条");


        listView = (ListView) findViewById(R.id.dabang_listview);
        findViewById(R.id.dabang_back).setOnClickListener(this);
        adapter=new CommonAdapter<String>(this,list, R.layout.item_dabang) {
            @Override
            public void convert(ViewHolder h, String i) {
                h.setText(R.id.item_dabang_name,i);
                h.setText(R.id.item_dabang_dsc,list2.get(h.getPosition()));

            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dabang_back:
                finish();
                break;
        }
    }
}