package com.modernsky.istv.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.R;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.CountBean;
import com.modernsky.istv.listener.CommonListener;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.utils.Constants.XiuchangParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方 登录信息
 */
public class PopGiftCount extends PopupWindow implements CommonListener {

    private Activity mActivity;
    private ListView lsitView;
    static List<CountBean> datas = new ArrayList<CountBean>();

    public PopGiftCount(Activity activity) {
        super(activity);
        this.mActivity = activity;
        initView(activity);
    }

    @SuppressWarnings("deprecation")
    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.pop_count_number, null);
        setContentView(rootView);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);

        lsitView = (ListView) rootView.findViewById(R.id.countListView);
        lsitView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dismiss();
                if (callBack != null) {
                    callBack.setCountBean(datas.get(position));
                }
            }
        });
    }

    /**
     *
     */
    private void updateListView() {
        lsitView.setAdapter(new CommonAdapter<CountBean>(mActivity, datas,
                R.layout.item_count) {
            @Override
            public void convert(ViewHolder helper, CountBean item) {
                TextView tet = helper.getView(R.id.item_count);
                tet.setText(item.getCount());
            }

        });
    }

    /**
     * 展示 位置
     */
    public void showPop() {
        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0,
                0);
    }

    public void showAtTop() {
        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0,
                170);
        if (datas.size() == 0) {
            SendActtionTool.post(XiuchangParams.GIFT_COUNT_LIST,
                    ServiceAction.Action_Comment,
                    CommentAction.Action_addHuifu, this);
        } else {
            updateListView();
        }
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        JSONObject obj = (JSONObject) value;

        try {
            List<CountBean> countData = JSON.parseArray(obj
                    .getJSONArray("data").toString(), CountBean.class);
            datas.clear();
            datas.addAll(countData);
            updateListView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {

    }

    @Override
    public void onStart(ServiceAction service, Object action) {

    }

    @Override
    public void onFinish(ServiceAction service, Object action) {

    }

    private CallBack callBack;

    public interface CallBack {
         void setCountBean(CountBean item);
    }

    public void setCallBack(CallBack back) {
        this.callBack = back;
    }
}
