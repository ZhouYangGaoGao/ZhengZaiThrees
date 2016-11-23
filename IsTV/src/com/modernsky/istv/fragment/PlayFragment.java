package com.modernsky.istv.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.LookForwardActivity;
import com.modernsky.istv.action.CommentAction;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.adapter.PlayPinglunListAdapter;
import com.modernsky.istv.bean.Content;
import com.modernsky.istv.bean.Huifu;
import com.modernsky.istv.bean.Pinglun;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.ResultPinglun;
import com.modernsky.istv.bean.ResultPinglunList;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.VideoPinglun;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.UserParams;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.widget.WidgetRadioSwitch;
import com.modernsky.istv.window.PinglunDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放界面fragment
 *
 * @author zxm
 */
@SuppressLint("ValidFragment")
public class PlayFragment extends BaseFragment {
    //
    private ListView mPinglunListView;
    private PlayPinglunListAdapter playPinglunAdapter;
    private List<VideoPinglun> pingluns = null;
    private int commentCount = 0;
    private OnDianZanListener dianZanListener;
    private String lastBuildTime;
    private InputMethodManager imm;
    private PinglunDialog pinglunDialog;
    //
    private int userStatus;
    private EditText pintlunTv;
    private List<Content> mContents = new ArrayList<Content>();
    private String userName;
    private String faceUrl;
    public PullToRefreshListView pullToRefreshListView;
    private String toUserId = "";
    private String videoId = "";
    private String commentId = "";
    private String userId = "0";
    private boolean isVisibleToUser;
    private WidgetRadioSwitch wrs;


    public boolean isShowWrs() {
        return showWrs;
    }

    public void setShowWrs(boolean showWrs) {
        this.showWrs = showWrs;
    }

    private boolean showWrs;

//    public PlayFragment(String videoId) {
//        this.videoId = videoId + "";
//    }
//
//    public PlayFragment() {
//        super();
//    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public interface OnDianZanListener {
        void onCompletion(String commentId, int count);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                finishInput();
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!UserService.getInatance().isNeedLogin(getActivity())) {
            UserEntity bean = UserService.getInatance().getUserBean(getActivity());
            userId = bean.getId();
            userStatus = bean.getStatus();
            userName = bean.getUserName();
            faceUrl = bean.getFaceUrl();
        }
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!UserService.getInatance().isNeedLogin(getActivity())) {
            UserEntity bean = UserService.getInatance().getUserBean(getActivity());
            userId = bean.getId();
            userStatus = bean.getStatus();
            userName = bean.getUserName();
            faceUrl = bean.getFaceUrl();
        }
        this.videoId = getArguments().getString("videoId");
        this.showWrs=getArguments().getBoolean("iswrs");
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void initView(View rootView) {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 初始化控件
        pintlunTv = (EditText) rootView.findViewById(R.id.footbar_play);
        pintlunTv.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 得到焦点
                } else {
                    // 失去焦点
                    pintlunTv.setText("");
                }
            }
        });
        pintlunTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEND) {
                    finishInput();
                    return true;
                }
                return false;
            }
        });
        TextView btn_send = (TextView) rootView.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        //
        initList(rootView);

        wrs = (WidgetRadioSwitch) rootView.findViewById(R.id.wrs);
        wrs.setIslookforward(true);
        wrs.bindListener(new WidgetRadioSwitch.SwitchListener() {
            @Override
            public void invoke(int str) {
                LogUtils.d("wrs.bindLi===="+str);
                if (showWrs) {
                    wrs.gc3.setVisibility(View.GONE);
                    wrs.gc1.setVisibility(View.VISIBLE);
                    wrs.gc2.setVisibility(View.VISIBLE);
                    wrs.gc1.setBackgroundResource(R.drawable.tabbar_user_selector);
                    wrs.gc2.setBackgroundResource(R.drawable.tabbar_find_selector);
                    ( (LookForwardActivity) getActivity()).setViewpager(str-1);
                }
            }
        });
        if (showWrs) {
            wrs.setVisibility(View.VISIBLE);
            wrs.gc3.setVisibility(View.GONE);
            wrs.gc1.setVisibility(View.VISIBLE);
            wrs.gc2.setVisibility(View.VISIBLE);
            wrs.gc1.setBackgroundResource(R.drawable.tabbar_user_selector);
            wrs.gc2.setBackgroundResource(R.drawable.tabbar_find_selector);
            wrs.setImage(1);
        } else {
            wrs.setVisibility(View.GONE);
        }

    }

    protected void finishInput() {
        if (UserService.getInatance().isNeedLogin(getActivity())) {
            DialogTool.createToLoginDialog(getActivity());
            return;
        }
        String string = pintlunTv.getText().toString().trim();
        imm.hideSoftInputFromWindow(pintlunTv.getWindowToken(), 0);
        if (TextUtils.isEmpty(string)) {
            Utils.toast(getActivity(), "评论为空");
            return;
        }
        int lengh = (int) Utils.calculateWeiboLength(string) - 140;
        if (lengh > 0) {
            Utils.toast(getActivity(), "评论长度最大为140个汉字,已经超出" + lengh + "个字,请删减后再试。");
            return;
        }
        pintlunTv.setText("");
        // 上传文字
        List<Content> contents = new ArrayList<Content>();
        Content content = new Content();
        content.setType("1");
        string = Utils.filterString(string);
        string = CheckCode.checkSensitive(getActivity(), string);
        content.setContent(string);
        contents.add(0, content);
        finishInput(contents);
    }

    private void finishInput(List<Content> contents) {
        mContents = contents;
        Log.i("xqp", userId + " " + toUserId + " " + videoId + " " + JSON.toJSONString(mContents) + " " + userStatus);
        if (TextUtils.isEmpty(toUserId)) {
            sendPingLun();
        } else {
            sendHuiFu();
        }
    }

    private void initList(View rootView) {
        pingluns = new ArrayList<VideoPinglun>();
        pullToRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.listview_pinglun);
        pullToRefreshListView.setMode(Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pingluns.clear();
                sendPinglunList(videoId, "0");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                sendPinglunList(videoId, lastBuildTime);
            }
        });
        mPinglunListView = pullToRefreshListView.getRefreshableView();
        Drawable drawable = getResources().getDrawable(R.drawable.line);
        mPinglunListView.setDivider(drawable);
        //
        mPinglunListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
        playPinglunAdapter = new PlayPinglunListAdapter(pingluns, getActivity(), this);
        mPinglunListView.setAdapter(playPinglunAdapter);
    }

    public void refresh() {
        pullToRefreshListView.setRefreshing();
    }

    private void downRefresh() {
        pintlunTv.setHint(commentCount + "条评论");
        //
        playPinglunAdapter.notifyDataSetChanged();
        mPinglunListView.smoothScrollToPosition(1);
        mPinglunListView.setSelection(1);
    }

    private void upRefresh(int postion) {
        pintlunTv.setHint(commentCount + "条评论");
        if (pingluns != null && pingluns.size() > 0) {
            VideoPinglun laseVideoPinglun = pingluns.get(pingluns.size() - 1);
            if (laseVideoPinglun != null) {
                lastBuildTime = String.valueOf(laseVideoPinglun.getBuildTime());
            }
        }
        playPinglunAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        super.onSuccess( service, action, value);
        String jsonString = value.toString();
        if (getActivity() == null) {
            return;
        }
        switch ((CommentAction) action) {
            case Action_addPinglun:
                ResultPinglun<Pinglun> resultPinglun = JSON.parseObject(jsonString, new TypeReference<ResultPinglun<Pinglun>>() {
                });
                Pinglun pinglun2 = resultPinglun.getData();
                if (resultPinglun.getStatus() == 1) {
                    Utils.toast(getContext(), R.string.pinglun_success);
                    VideoPinglun pinglun = new VideoPinglun();
                    pinglun.setId(pinglun2.getId());
                    UserEntity entity = new UserEntity();
                    entity.setUserName(userName);
                    entity.setFaceUrl(faceUrl);
                    entity.setId(pinglun2.getUserId());
                    pinglun.setUserEntity(entity);
                    pinglun.setBuildTime(System.currentTimeMillis());
                    pinglun.setContent(mContents);
                    pingluns.add(0, pinglun);
                    commentCount++;
                    downRefresh();
                } else {
                    Utils.toast(getContext(), resultPinglun.getMessage());
                }
                break;
            case Action_getPinglunList:
                ResultPinglunList<VideoPinglun> resultPingluns = JSON.parseObject(jsonString, new TypeReference<ResultPinglunList<VideoPinglun>>() {
                });
                commentCount = resultPingluns.getCommentCount();
                int position = pingluns.size() - 1;
                pingluns.addAll(resultPingluns.getData());
                upRefresh(position);
                break;
            case Action_addPraiseComment:
                ResultPinglun<Pinglun> resultPinglunPraise = JSON.parseObject(jsonString, new TypeReference<ResultPinglun<Pinglun>>() {
                });
                Pinglun data = resultPinglunPraise.getData();
                if (resultPinglunPraise.getStatus() == 1) {
                    Utils.toast(getContext(), R.string.dianzan_success);
                    if (dianZanListener != null) {
                        dianZanListener.onCompletion(data.getCommentId(), resultPinglunPraise.getCount());
                    }
                }
                break;
            case Action_getHuifuList:
                ResultBean<List<Huifu>> pinglunHuifus = JSON.parseObject(jsonString, new TypeReference<ResultBean<List<Huifu>>>() {
                });
                if (callback != null) {
                    playPinglunAdapter.notifyDataSetChanged();
                    callback.onHuifuComplet(pinglunHuifus);
                }
                break;
            case Action_addHuifu:
                ResultPinglun<Pinglun> resultHuifu = JSON.parseObject(jsonString, new TypeReference<ResultPinglun<Pinglun>>() {
                });
                LogUtils.d(jsonString);
                if (resultHuifu != null && resultHuifu.getStatus() == 1) {
                    Utils.toast(getActivity(), "回复成功");
                } else {
                    if (resultHuifu!=null) {
                        Utils.toast(getActivity(), resultHuifu.getMessage());
                    }
                }
                sendHuifuList(commentId, "0", callback);
                backClean();
                break;
            default:
                break;
        }

    }

    @Override
    public void onFaile(ServiceAction service, Object action, Object value) {
        Utils.toast(getActivity(),value.toString());
        switch ((CommentAction) action) {
            case Action_addPraiseComment:
                if (dianZanListener != null) {
                    if (value.toString().contains("已经赞过")) {
                        dianZanListener.onCompletion(commentId,-1);
                    }
                }
                break;
        }

    }

    @Override
    public void onException(ServiceAction service, Object action, Object value) {
        Utils.toast(getActivity(), "onException " + value.toString());
    }

    @Override
    public void onFinish(ServiceAction service, Object action) {
        super.onFinish(service, action);
        if (CommentAction.Action_getPinglunList == action) {
            pullToRefreshListView.onRefreshComplete();
        }
    }

    public interface HuifuCallback {
        void onHuifuComplet(ResultBean<List<Huifu>> huifuResult);
    }

    private HuifuCallback callback;

    /**
     * 点赞请求
     */
    public void dianZan(String commentId, String toUserId, OnDianZanListener dianZanListener) {
        if (UserService.getInatance().isNeedLogin(getActivity())) {
//			new AlertDialogLogin(getActivity()).setMessage("您未登录\n请先登录");
            DialogTool.createToLoginDialog(getActivity());
            return;
        }
        setDianZanListener(dianZanListener);
        this.commentId=commentId;
        sendDianZan(commentId);
        // sendPinglunList(playActivity.getVideoId(), "0");
    }

    public OnDianZanListener getDianZanListener() {
        return dianZanListener;
    }

    public void setDianZanListener(OnDianZanListener dianZanListener) {
        this.dianZanListener = dianZanListener;
    }

    public void setPicture(String value, String string) {
        pinglunDialog.setPicture(value, string);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setCallback(HuifuCallback callback) {
        this.callback = callback;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void backClean() {
        this.toUserId = "";
        pintlunTv.setText("");
        pintlunTv.setHint(commentCount + "条评论");
    }

    public void reply(String toUserId, String toUserName, String commentId, HuifuCallback callback) {
        //
        pintlunTv.setHint(toUserName);
        pintlunTv.requestFocus();
        pintlunTv.setFocusable(true);
        pintlunTv.setFocusableInTouchMode(true);
        GeneralTool.KeyBoardShow(getActivity(), pintlunTv);
        setCallback(callback);
        //
        this.toUserId = toUserId;
        this.commentId = commentId;
    }

    /**
     * 获取视频评论列表
     */
    private void sendPinglunList(String videoId, String buildTime) {
        RequestParams params = UrlTool.getParams(Constants.RESOURCE_ID, videoId,//
                Constants.BUILD_TIME, buildTime,//
                Constants.USER_ID, userId,//
                Constants.STATUS, String.valueOf(1));
        LogUtils.d("videoId==="+videoId+"buildTime=="+buildTime+"userId=="+userId);
        SendActtionTool.get(UserParams.URL_COMMENT_LIST, ServiceAction.Action_Comment, CommentAction.Action_getPinglunList, this, params);
    }

    private void sendPingLun() {
        String string = JSON.toJSONString(mContents);
        RequestParams params = UrlTool.getParams(Constants.RESOURCE_ID, videoId,//
                Constants.USER_ID, userId,//
                Constants.CONTENT, string, //
                Constants.STATUS, String.valueOf(userStatus));
        SendActtionTool.get(UserParams.URL_ADD_COMMENT, ServiceAction.Action_Comment, CommentAction.Action_addPinglun, PlayFragment.this, params);
    }

    private void sendHuiFu() {
        String string = JSON.toJSONString(mContents);
        RequestParams params = UrlTool.getParams(Constants.COMMENT_ID, commentId,//
                Constants.USER_ID, userId,//
                Constants.TO_USER_ID, toUserId,//
                Constants.CONTENT, string,//
                Constants.STATUS, userStatus + "");
        SendActtionTool.get(UserParams.URL_ADD_REPLY, ServiceAction.Action_Comment, CommentAction.Action_addHuifu, PlayFragment.this, params);
    }

    private void sendDianZan(String commentId) {
        RequestParams params = UrlTool.getParams(Constants.COMMENT_ID, commentId, //
                Constants.USER_ID, userId,//
                Constants.TO_USER_ID, toUserId,//
                Constants.STATUS,//
                String.valueOf(userStatus));
        SendActtionTool.get(UserParams.URL_ADD_PRAISE_COMMENT, ServiceAction.Action_Comment, CommentAction.Action_addPraiseComment, this, params);
    }

    /**
     * 获取评论回复列表
     */
    public void sendHuifuList(String commentId, String buildTime, HuifuCallback callback) {
        setCallback(callback);
        RequestParams params = UrlTool.getParams(Constants.COMMENT_ID, commentId,//
                Constants.USER_ID, userId,//
                Constants.BUILD_TIME, buildTime);
        SendActtionTool.get(UserParams.URL_COMMENT_LIST_REPLY, ServiceAction.Action_Comment, CommentAction.Action_getHuifuList, this, params);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && wrs != null) {
            wrs.setImage(1);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public boolean isVisibleToUser() {
        return isVisibleToUser;
    }
}
