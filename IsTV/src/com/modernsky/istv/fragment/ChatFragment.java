package com.modernsky.istv.fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.ShowActivity;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.RoundAngleImageView;
import com.modernsky.istv.widget.WidgetRadioSwitch;
import com.modernsky.istv.widget.WidgetRadioSwitch.SwitchListener;
import com.modernsky.istv.window.PeopleInfoDialog;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * 解决问题：秀场视频播放页面的聊天fragment
 *
 * @author 谢秋鹏
 */
@SuppressLint("ValidFragment")
public class ChatFragment extends BaseFragment {
    //
    private String mChatroomId;
    private UserEntity userBean;
    private String toUserName;
    private String toUserId;

    public EditText getEdtChat() {
        return edtChat;
    }

    public void setEdtChat(EditText edtChat) {
        this.edtChat = edtChat;
    }

    // 秀场输入框
    private EditText edtChat;
    private Button btn_send;
    private WidgetRadioSwitch wrs = null;
    private ListView listViewChat;
    public CommonAdapter<XiuchanMessage> adapterChat;
    private Button btnGift;
    // SPAN
    private ForegroundColorSpan blueSpan;
    private ForegroundColorSpan writeSpan;
    private ForegroundColorSpan blueSpan_zhengzai;
    //    private Dialog createGuideDialog;
    // 秀场消息列表
    private List<XiuchanMessage> chatDatas = new ArrayList<XiuchanMessage>();
    private boolean ifhasSendOut = true;

    @Override
    public void onClick(View v) {
//        android.os.Message message = new android.os.Message();
//        Handler handler = ((ChatRoomShowFragment) getParentFragment()).getHandler();
        switch (v.getId()) {
            // 点击回复
            case R.id.rl_pinglun_info1:
                XiuchanMessage item = (XiuchanMessage) v.getTag();
                // hideBottomNavi(null);
                // 获得焦点
                edtChat.requestFocus();
                edtChat.setFocusable(true);
                edtChat.setFocusableInTouchMode(true);
                setToUserName(item.getFromUserName());
                setToUserId(item.getFromUserId());
                if (userBean.getUserName().equals(toUserName)) {
                    edtChat.setHint("请输入");
                } else {
                    edtChat.setHint("回复：" + toUserName);
                }
                GeneralTool.KeyBoardShow(getActivity(), edtChat);
                break;
            case R.id.xiuchang_btn_Gift:
                ((ShowActivity) ChatFragment.this.getActivity()).initGiftDialog();
//                message.what = 1;
//                handler.sendMessage(message);
                break;
            case R.id.btn_send:
                if (!ifhasSendOut) {
                    return;
                }
                setEditText(edtChat.getText().toString(), false);
                wrs.setVisibility(View.VISIBLE);
                break;
            //
            default:
            case R.id.wrs:
                wrs.onClick(v);
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && wrs != null) {
            wrs.setImage(0);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public ChatFragment(String toUserName, String toUserId, String mChatroomId, UserEntity userBean) {
        super();
        this.toUserName = toUserName;
        this.toUserId = toUserId;
        this.mChatroomId = mChatroomId;
        this.userBean = userBean;
    }
    public ChatFragment() {
        super();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
////            if (createGuideDialog != null) {
////                createGuideDialog.dismiss();
////            }
//        }
    }
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        this.toUserName = bundle.getString("toUserName");
        this.toUserId = bundle.getString("toUserId");
        this.mChatroomId = bundle.getString("mChatroomId");
        this.userBean = (UserEntity) bundle.getSerializable("userBean");

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void initView(View rootView) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        btnGift = (Button) rootView.findViewById(R.id.xiuchang_btn_Gift);
        btnGift.setOnClickListener(this);
        edtChat = (EditText) rootView.findViewById(R.id.xiuchang_Edtinput);
        //
        wrs = (WidgetRadioSwitch) rootView.findViewById(R.id.wrs);
        wrs.bindListener(new SwitchListener() {
            @Override
            public void invoke(int str) {
                Log.i("xqp", str + "");
                android.os.Message message = new android.os.Message();
                message.what = str;
                ((ChatRoomShowFragment) getParentFragment()).getHandler().sendMessage(message);
            }
        });
        btn_send = (Button) rootView.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        rootView.findViewById(R.id.gc1).setOnClickListener(this);
        rootView.findViewById(R.id.gc2).setOnClickListener(this);
        rootView.findViewById(R.id.gc3).setOnClickListener(this);
        listViewChat = (ListView) rootView.findViewById(R.id.listViewChat);
        //
        initChatEdt();
        updateChatListView();
        rootView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 获取是否引导过笨页面
                Boolean hasNewGuide = PreferencesUtils.getBooleanPreferences(
                        activity, PreferencesUtils.TYPE_GUIDE[1]);
                if (!hasNewGuide
                        && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    createGuideDialog = DialogTool.createGuideDialog(
//                            getActivity(), 1, new DialogGuideListener() {
//                                @Override
//                                public void onGuide(int index) {
//                                    btnGift.performClick();
//                                }
//
//                            });
                    PreferencesUtils.saveBooleanPreferences(getActivity(),
                            PreferencesUtils.TYPE_GUIDE[1], true);
                }
            }
        }, 1000);
        wrs.setImage(0);
    }

    private void initChatEdt() {
        edtChat.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 得到焦点
                } else {
                    // 失去焦点
                    edtChat.setText("");
                }
            }
        });
        edtChat.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    setEditText(edtChat.getText().toString(), false);
                }
                return false;
            }
        });
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wrs.setVisibility(View.GONE);
                btn_send.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    wrs.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * @param value
     * @param isComming 进入聊天室  我来了
     */
    public void setEditText(String value, boolean isComming) {
        if (TextUtils.isEmpty(mChatroomId)) {
            Utils.toast(getActivity(), "账号过期请重新登录");
            return;
        }
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(value.trim())) {
            // zwz 去除某某进入了房间
            // zUtils.toast(getActivity(), "发送的内容不能为空");
            return;
        }
        int lengh = (int) Utils.calculateWeiboLength(value) - 140;
        if (lengh > 0) {
            Utils.toast(getActivity(), "评论长度最大为140个汉字,已经超出" + lengh
                    + "个字,请删减后再试。");
            return;
        }
        ifhasSendOut = false;
        initMessage(value, isComming);
        edtChat.setText("");
    }

    /**
     * @param value
     * @param isMycomming 进入聊天室  我来了
     */
    private void initMessage(String value, boolean isMycomming) {
        XiuchanMessage sendXiuchangMsg = new XiuchanMessage();
        if (userBean.getUserName().equals(toUserName)) {
            sendXiuchangMsg.setToUserName("");
            sendXiuchangMsg.setToUserId("");
        } else {
            if (!TextUtils.isEmpty(toUserName)) {
                sendXiuchangMsg.setToUserName(toUserName);
                sendXiuchangMsg.setToUserId(toUserId);
            } else {
                sendXiuchangMsg.setToUserId("");
                sendXiuchangMsg.setToUserName(null);
            }
        }
        sendXiuchangMsg.setMsg(CheckCode.checkSensitive(getActivity(), value));
        sendXiuchangMsg.setFromUserName(userBean.getUserName());
        sendXiuchangMsg.setFromUserId(userBean.getId());
        if (isMycomming) {
            sendXiuchangMsg.setType(8);
        } else {
            if (TextUtils.isEmpty(toUserName)) {
                sendXiuchangMsg.setType(1);
            } else {
                sendXiuchangMsg.setType(5);
            }
        }
        sendXiuchangMsg.setFromUserPic(userBean.getFaceUrl());
        sendMessage(sendXiuchangMsg);
    }


    public void sendMessage(final XiuchanMessage sendXiuchangMsg) {
        final String jsonString = JSON.toJSONString(sendXiuchangMsg);
        TextMessage messageContent = TextMessage.obtain(jsonString);
        LogUtils.d("sendMessage" + jsonString);
        // 发送消息的封装
        RongIMClient.getInstance().sendMessage(ConversationType.CHATROOM,
                mChatroomId, messageContent, userBean.getId(), "",
                new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer arg0, ErrorCode arg1) {
                        LogUtils.t(ShowActivity.class.getName(), "Integer:"
                                + arg0 + "---发送消息失败:" + arg1.toString());
                        ifhasSendOut = true;
                        // btn_send.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(Integer arg0) {
                        sendMsgSuccess(sendXiuchangMsg, jsonString, arg0);
                        // btn_send.setEnabled(true);
                        ifhasSendOut = true;
                        LogUtils.d("onSuccess111111111");
                    }
                }, new ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message arg0) {
                        LogUtils.t(ShowActivity.class.getName(), "发送消成功:"
                                + arg0.toString());
                        // sendMsgSuccess(sendXiuchangMsg, jsonString,
                        // arg0.getMessageId());
                        // btn_send.setEnabled(true);
                        ifhasSendOut = true;
                        LogUtils.d("onSuccess222222222");
                    }

                    @Override
                    public void onError(ErrorCode arg0) {
                        LogUtils.t(ShowActivity.class.getName(), "发送消息失败:"
                                + arg0.toString());
                        // btn_send.setEnabled(true);
                        ifhasSendOut = true;
                    }
                });
    }

    private void sendMsgSuccess(final XiuchanMessage sendXiuchangMsg,
                                final String jsonString, Integer arg0) {
        LogUtils.t(ShowActivity.class.getName(), "发送消息成功");
        if (sendXiuchangMsg != null) {
            sendXiuchangMsg.setTime("刚刚");
            sendXiuchangMsg.setMsgId(arg0);
            android.os.Message message = new android.os.Message();
            message.obj = sendXiuchangMsg;
            message.what = 0;
            ((ChatRoomShowFragment) getParentFragment()).getHandler()
                    .sendMessage(message);
        }
        setToUserName(null);
        edtChat.setHint(null);
        // 发送回复功能
//        if (sendXiuchangMsg.getToUserName() != null) {
//            sendHuiFu(jsonString, String.valueOf(arg0), mChatroomId);
//        }
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
        if (TextUtils.isEmpty(toUserName)) {
            edtChat.setHint(null);
        }
    }

    /**
     * 更新聊天信息
     */
    private void updateChatListView() {

        adapterChat = new CommonAdapter<XiuchanMessage>(getParentFragment()
                .getActivity(), chatDatas, R.layout.dianbo_pinglun_lv_child) {
            //        adapterChat = new CommonAdapter<XiuchanMessage>(getParentFragment()
//                .getActivity(), chatDatas, R.layout.item_gc) {
            @Override
            public void convert(ViewHolder helper, final XiuchanMessage item) {
                helper.getView(R.id.giftImg).setVisibility(View.GONE);
                helper.getView(R.id.giftNum).setVisibility(View.GONE);
                ImageView img = helper.getView(R.id.img_grid);
                if (!TextUtils.isEmpty(item.getFromUserId())) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!item.getFromUserId().equals(UserService.getInatance().getUserBean(ChatFragment.this.getActivity()).getId())) {
                                new PeopleInfoDialog(ChatFragment.this.getActivity(), item.getFromUserId()).show();
                            }
                        }
                    });
                    if (!TextUtils.isEmpty(item.getFromUserName())) {
                        View normalView = helper.getView(R.id.rl_pinglun_info1);
                        normalView.setVisibility(View.VISIBLE);
                        normalView.setOnClickListener(ChatFragment.this);
                        normalView.setTag(item);
                    }
                }


                RoundAngleImageView pic = helper
                        .getView(R.id.img_grid);
                pic.setVisibility(View.VISIBLE);
                BitmapTool.getInstance().getAdapterUitl()
                        .display(pic, item.getFromUserPic());
                helper.getView(R.id.rl_pinglun_info1).setVisibility(View.VISIBLE);
                TextView name = helper.getView(R.id.tv_video_name1);
                TextView time = helper.getView(R.id.time1);
                switch (item.getType()) {

                    // 普通消息
                    case 1:
                    case 5:
//                        View normalView = helper.getView(R.id.rl_pinglun_info1);
//                        normalView.setVisibility(View.VISIBLE);
//                        normalView.setOnClickListener(ChatFragment.this);
//                        normalView.setTag(item);

                        time.setText(item.getTime());
                        // 回复的内容
                        String content;
                        if (!TextUtils.isEmpty(item.getToUserName())) {
                            content = item.getFromUserName() + " 回复 "
                                    + item.getToUserName() + " " + item.getMsg();
                        } else {
                            content = item.getFromUserName() + " " +
                                    item.getMsg();
//                            msg.setText(item.getMsg());
                        }
                        name.setText(content);
                        break;
                    // 聊天列表通知
                    // 底部和聊天列表通知
                    case 2:
                        name.setText(item.getMsg());
                        time.setText(item.getTime());
                        break;
                    case 3:
                    case 4:

                        helper.getView(R.id.giftImg).setVisibility(View.VISIBLE);
                        helper.getView(R.id.giftNum).setVisibility(View.VISIBLE);
                        LogUtils.d("item.getCount()===" + item.getCount() + "item.getPic()===" + item.getPic());
                        BitmapTool.getInstance().getAdapterUitl().display(helper.getView(R.id.giftImg), item.getPic());
                        ((TextView) helper.getView(R.id.giftNum)).setText(" X" + item.getCount() + "!");

                        name.setText(item.getFromUserName() + "送给选手");

                        time.setText(item.getTime());
                        break;
                    case 7:
                        name.setText("本场直播延长了 " + item.getOnceTime() / 60000 + "分钟");
                        time.setText(item.getTime());
                        break;
                    case 6:
                        name.setText("选手结束了本场直播");
                        time.setText(item.getTime());
                        break;
                    case 8:
                        name.setText(item.getFromUserName() + " " + item.getMsg());
                        time.setText(item.getTime());
                        break;
                    default:
                        break;
                }
            }
        };
        listViewChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listViewChat.setAdapter(adapterChat);
        int size = adapterChat.getCount();
        listViewChat.setSelection(size == 0 ? size : size - 1);
        // getHistory();
    }

//    public void sendHuiFu(String jsonString, String arg0, String mChatroomId) {
//        RequestParams params = UrlTool.getParams("content", jsonString,
//                "msgId", arg0, Constants.CHATROOM_ID, mChatroomId);
//        SendActtionTool.post(XiuchangParams.XiuChang_HUIFU,
//                ServiceAction.Action_xiuchang, XiuchangAction.ACTION_HUIFU,
//                ChatFragment.this, params);
//    }

    //
//    private void getHistory() {
//        RequestParams params = UrlTool.getParams(Constants.USER_ID,
//                userBean.getId(), Constants.CHATROOM_ID, mChatroomId);
//        SendActtionTool.post(XiuchangParams.XiuChang_HistoryReply,
//                ServiceAction.Action_xiuchang,
//                XiuchangAction.ACTION_HistoryReply, this, params);
//    }

    public void setSpan(ForegroundColorSpan blueSpan,
                        ForegroundColorSpan writeSpan, ForegroundColorSpan blueSpan_zhengzai) {
        this.blueSpan = blueSpan;
        this.writeSpan = writeSpan;
        this.blueSpan_zhengzai = blueSpan_zhengzai;
    }

//    @Override
//    public void onSuccess(ServiceAction service, Object action, Object value) {
//        JSONObject obj = (JSONObject) value;
//        switch ((XiuchangAction) action) {
//            case ACTION_HUIFU:
//                adapterChat.notifyDataSetChanged();
//                break;
//            default:
//                break;
//        }
//    }

    public void addMessage(XiuchanMessage message) {
        if (message.getGiftId() != null && !TextUtils.isEmpty(message.getPic())) {
            if (message.getCount() == 0) {
                return;
            }
        }
        if (adapterChat == null) {
            return;
        }
        if (message.getType() == 7 && message.getOnceTime() == 0) {
            return;
        }

        if (message.getType() == 9) {
            return;
        }

        if (chatDatas.size() > 0 && message.getType() == 8) {
            if (TextUtils.isEmpty(message.getFromUserId())) {
                return;
            }
            if ((message.getFromUserId()).equals(chatDatas.get(chatDatas.size() - 1).getFromUserId()) && chatDatas.get(chatDatas.size() - 1).getType() == 8) {
                return;
            }
        }
        chatDatas.add(message);
        adapterChat.notifyDataSetChanged();
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;

    }

    public void backImpress() {
        this.toUserId = "";
        edtChat.setText("");
        edtChat.setHint("请输入");
        setToUserId("");
        setToUserName("");
    }

}
