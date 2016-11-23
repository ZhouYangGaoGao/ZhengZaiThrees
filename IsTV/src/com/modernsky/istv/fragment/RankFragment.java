package com.modernsky.istv.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.XiuchangAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.PaihangBean;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.XiuchangParams;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.view.RoundAngleImageView;
import com.modernsky.istv.widget.WidgetRadioSwitch;
import com.modernsky.istv.widget.WidgetRadioSwitch.SwitchListener;
import com.modernsky.istv.window.PeopleInfoDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankFragment extends BaseFragment {
    //
    private PullToRefreshListView listViewPaihang;
    private String mChatroomId;
    private CommonAdapter<PaihangBean> adapterPaihang = null;
    private Activity mAct;
    private ChatRoomShowFragment chatRoomShowFragment;
    private WidgetRadioSwitch wrs = null;
//    private boolean isLive;
    private List<PaihangBean> datas = new ArrayList<PaihangBean>();

    @Override
    public void onClick(View v) {

    }

//    public RankFragment(boolean isLive) {
//        super();
//        this.isLive = isLive;
//    }


    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chatRoomShowFragment = (ChatRoomShowFragment) getParentFragment();
        mAct = getActivity();
//        this.isLive=getArguments().getBoolean("isLive",true);
        return inflater.inflate(R.layout.fragment_rank, container, false);
    }

    @Override
    public void initView(View rootView) {
        listViewPaihang = (PullToRefreshListView) rootView.findViewById(R.id.xiuchang_paihangListView);
        listViewPaihang.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listViewPaihang.setOnRefreshListener(new com.handmark.pulltorefresh.library.PullToRefreshBase
                .OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                setmChatroomId();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        LogUtils.d("initView  listViewPaihang ");
        wrs = (WidgetRadioSwitch) rootView.findViewById(R.id.wrs);
        wrs.bindListener(new SwitchListener() {
            @Override
            public void invoke(int str) {
                android.os.Message message = new android.os.Message();
                message.what = str;
                ((ChatRoomShowFragment) getParentFragment()).getHandler().sendMessage(message);
            }
        });
//        if (!isLive) {
//            wrs.setVisibility(View.GONE);
//        }
        setmChatroomId();
    }

    public void setmChatroomId() {
        this.mChatroomId = chatRoomShowFragment.getChatRoomId();
        RequestParams req = UrlTool.getPostParams(Constants.CHATROOM_ID, mChatroomId);
        SendActtionTool.post(XiuchangParams.SHOW_CHATROOM_RANK, ServiceAction.Action_xiuchang, XiuchangAction
                .ACTION_ORDER, this, req);
    }


    /**
     * 初始化 排行
     */
    private void updatePaihang() {
        if (adapterPaihang == null) {

            adapterPaihang = new CommonAdapter<PaihangBean>(mAct, datas, R.layout.item_paihang) {
                @Override
                public void convert(ViewHolder helper, final PaihangBean item) {
                    TextView name = helper.getView(R.id.paihang_userNameTet);
                    TextView mb = helper.getView(R.id.paihang_userGongxianTet);
                    RoundAngleImageView userFace = helper.getView(R.id.paihang_userImg);
                    name.setText(item.getUser().getUserName());
                    mb.setText(item.getMbCount() + " M豆");
                    BitmapTool.getInstance().getAdapterUitl().display(userFace, item.getUser().getFaceUrl());
                    if (item.getUser().getStatus() == 6) {
                        helper.setBackgroundResource(R.id.tv_lv_anchor, R.drawable.dzz_0home_icon_levelbg);
                    } else
                        helper.setBackgroundResource(R.id.tv_lv_anchor, R.drawable.icon_huizhang);

                    if (item.getUser().getRank() != null) {
                        helper.setText(R.id.tv_lv_anchor, item.getUser().getRank().getRank());
                    }

                    userFace.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.getUser() != null && !item.getUser().getId().endsWith(UserService.getInatance()
                                    .getUserBean(mContext).getId()))
                                new PeopleInfoDialog(mContext, item.getUser().getId()).show();
                        }
                    });
                }
            };
            listViewPaihang.getRefreshableView().setAdapter(adapterPaihang);
        } else
            adapterPaihang.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess(service, action, value);
        JSONObject obj = (JSONObject) value;
        switch ((XiuchangAction) action) {
            case ACTION_ORDER:
                try {
                    List<PaihangBean> tempdatas = JSON.parseArray(obj.getString("data"), PaihangBean.class);
                    if (tempdatas != null && tempdatas.size() > 0) {
                        datas.clear();
                        datas.addAll(tempdatas);
                        listViewPaihang.onRefreshComplete();
                        updatePaihang();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        if (listViewPaihang != null) {
            listViewPaihang.onRefreshComplete();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (wrs != null) {
                wrs.setImage(1);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
