package com.modernsky.istv.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.R;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史记录
 *
 * @author 谢秋鹏
 */
public class ListSearchHistory extends ListView {
    private List<String> historys;
    private CommonAdapter<String> commonAdapter;
    private View footView;
    private OnItemClickListener listener;

    public ListSearchHistory(Context context, OnItemClickListener listener) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.list_search_history, this);
        this.listener = listener;
        init();
    }

    private void init() {
        String str = PreferencesUtils.getPreferences(getContext().getApplicationContext(), PreferencesUtils.TYPE_HOSTORY_LIST);
        if (historys == null) {
            historys = new ArrayList<String>();
        }
        if (!TextUtils.isEmpty(str)) {
            historys = JSON.parseArray(str, String.class);
        }
        commonAdapter = new CommonAdapter<String>(getContext(), historys, R.layout.item_history) {
            @Override
            public void convert(ViewHolder helper, String item) {
                helper.setText(R.id.tv_name, item);
            }
        };
        footView = LayoutInflater.from(getContext()).inflate(R.layout.tv_single, null);
        this.addFooterView(footView);
        this.setAdapter(commonAdapter);
        this.setOnItemClickListener(listener);
    }

    public void show() {
        if (historys.size() == 0) {
            this.addFooterView(footView);
        }
        this.setVisibility(View.VISIBLE);
    }

    public void add(String keywords) {
        if (!historys.contains(keywords)) {
            historys.add(keywords);
            if (historys.size() > 5) {
                historys.remove(0);
            }
            saveHistorys(historys);
            commonAdapter.notifyDataSetChanged();
        }
    }

    public void clean() {
        historys.clear();
        this.removeFooterView(footView);
        saveHistorys(historys);
        commonAdapter.notifyDataSetChanged();
        Utils.toast(getContext(), "已经清空历史记录");
    }

    /**
     * 保存历史记录到sharePreferences
     *
     * @param list
     */
    private void saveHistorys(List<String> list) {
        String str = JSON.toJSONString(list);
        PreferencesUtils.savePreferences(getContext().getApplicationContext(), PreferencesUtils.TYPE_HOSTORY_LIST, str);
    }
}
