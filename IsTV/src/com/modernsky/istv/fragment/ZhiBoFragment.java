package com.modernsky.istv.fragment;

/**
 *
 */

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.http.RequestParams;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.LiveActivity;
import com.modernsky.istv.action.ServiceAction;
import com.modernsky.istv.action.XiuchangAction;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.SendActtionTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.tool.UrlTool;
import com.modernsky.istv.utils.CheckCode;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.Constants.XiuchangParams;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.RoundAngleImageView;
import com.modernsky.istv.widget.WidgetSendBar;
import com.modernsky.istv.widget.WidgetSendBar.SendBarListener;
import com.modernsky.istv.window.PeopleInfoDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-20 下午1:45:51
 * @类说明：
 */
@SuppressLint("ResourceAsColor")
public class ZhiBoFragment extends BaseFragment {
    //
    private String mChatroomId = "";
    private String toUserName;
    private UserEntity userBean;
    //
    private EditText edtChat = null;
    private ListView listViewChat;
    int reContect = 3;
    private CommonAdapter<XiuchanMessage> adapterChat;
    // 秀场消息列表
    private List<XiuchanMessage> chatDatas = new ArrayList<XiuchanMessage>();
    //    private ForegroundColorSpan blueSpan;
//    private ForegroundColorSpan writeSpan;
//    private ForegroundColorSpan blueSpan_zhengzai;
    private WidgetSendBar wsb = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            LiveActivity aty = (LiveActivity) getActivity();
            XiuchanMessage message = (XiuchanMessage) msg.obj;
            switch (msg.what) {
                // 发送聊天信息成功
                case 1:
                    addMessage(message);
                    aty.sendTanmu(message, true);
                    break;
                // 接受到新的消息
                case 2:
                    switch (message.getType()) {
                        // 顶部和底部部通知
                        case 4:
                            addMessage(message);
                            break;
                        // 顶端通知
                        case 2:
                            break;
                        default:
                            addMessage(message);
                            aty.sendTanmu(message, false);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击回复
            case R.id.rl_pinglun_info1:
                toUserName = ((XiuchanMessage) v.getTag()).getFromUserName();
                if (UserService.getInatance().getUserBean(this.getActivity()).getUserName().equals(toUserName)) {
                    toUserName = "";
                    wsb.replyComment("请输入");
                } else {
                    wsb.replyComment(toUserName);
                }
                GeneralTool.KeyBoardShow(getActivity(), wsb.et_input);
                break;
            default:
                break;
        }
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userBean = UserService.getInatance().getUserBean(getActivity());
        return inflater.inflate(R.layout.fragment_zhipo, container, false);
    }

    @Override
    public void initView(View rootView) {
        wsb = (WidgetSendBar) rootView.findViewById(R.id.wsb);
        wsb.bindListener(new SendBarListener() {
            @Override
            public void invoke(String str) {
                wsb.btn_send.setEnabled(false);
                sendMessage(str);

            }
        });
        //
        listViewChat = (ListView) rootView.findViewById(R.id.listViewChat);
        initListView();
//        blueSpan = new ForegroundColorSpan(getResources().getColor(
//                R.color.textgray));
//        writeSpan = new ForegroundColorSpan(getResources().getColor(
//                R.color.white));
//        blueSpan_zhengzai = new ForegroundColorSpan(Color.parseColor("#9fd9f6"));
    }

    /**
     * 更新聊天信息
     */
    private void initListView() {
        adapterChat = new CommonAdapter<XiuchanMessage>(getActivity(),
                chatDatas, R.layout.dianbo_pinglun_lv_child) {
            @Override
            public void convert(ViewHolder helper, final XiuchanMessage item) {
                helper.getView(R.id.giftImg).setVisibility(View.GONE);
                helper.getView(R.id.giftNum).setVisibility(View.GONE);
                ImageView img = helper.getView(R.id.img_grid);
                if (!TextUtils.isEmpty(item.getFromUserId())) {
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!item.getFromUserId().equals(UserService.getInatance().getUserBean(ZhiBoFragment.this.getActivity()).getId())) {
                                new PeopleInfoDialog(ZhiBoFragment.this.getActivity(), item.getFromUserId()).show();
                            }
                        }
                    });
                    if (!TextUtils.isEmpty(item.getFromUserName())) {
                        View normalView = helper.getView(R.id.rl_pinglun_info1);
                        normalView.setVisibility(View.VISIBLE);
                        normalView.setOnClickListener(ZhiBoFragment.this);
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

//                        helper.getView(R.id.giftImg).setVisibility(View.VISIBLE);
//                        helper.getView(R.id.giftNum).setVisibility(View.VISIBLE);
//                        LogUtils.d("item.getCount()==="+item.getCount()+"item.getPic()==="+item.getPic());
//                        BitmapTool.getInstance().getAdapterUitl().display(helper.getView(R.id.giftImg), item.getPic());
//                        ((TextView)helper.getView(R.id.giftNum)).setText(" X" + item.getCount()+"!");
//
//                        name.setText(item.getFromUserName()+"送给选手");
//
//                        time.setText(item.getTime());
//                        break;
                    case 7:
//                        name.setText("本场直播延长了 " + item.getOnceTime() / 60000 + "分钟");
//                        time.setText(item.getTime());
//                        break;
                    case 8:
                        name.setText(item.getFromUserName() + " " + item.getMsg());
                        time.setText(item.getTime());
                        break;
                    default:
                        break;
                }
            }

        };
        listViewChat.setAdapter(adapterChat);
        listViewChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
        if (TextUtils.isEmpty(toUserName)) {
            if (edtChat != null) {
                edtChat.setHint("");
            }
        }
    }

    /**
     * @param chatroomId
     */
    public void setChatroomId(String chatroomId) {
        this.mChatroomId = chatroomId;
        if (chatroomId == null) {
            Utils.toast(getActivity(), "初始化失败，请退出后重新打开");
            return;
        }
        chatConnectServer();
    }

    /**
     * 连接到服务器
     */
    private void chatConnectServer() {
        if (reContect < 0) {
            Utils.toast(getActivity(), "加入聊天室失败！");
            return;
        }
        reContect--;
        if (!ThreeAppParams.IS_CONNECT_SERVER) {
            LogUtils.t(LiveActivity.class.getName(),
                    "开始连接容云服务" + userBean.getRongcloudToken());
            RongIMClient.connect(userBean.getRongcloudToken(),
                    new RongIMClient.ConnectCallback() {
                        @Override
                        public void onSuccess(String arg0) {
                            ThreeAppParams.IS_CONNECT_SERVER = true;
                            LogUtils.t(LiveActivity.class.getName(),
                                    "链接容云服务器成功");
                            chatListener();
                        }

                        @Override
                        public void onTokenIncorrect() {
                            LogUtils.t(LiveActivity.class.getName(), "连接失败");
                        }

                        @Override
                        public void onError(
                                io.rong.imlib.RongIMClient.ErrorCode arg0) {
                            LogUtils.t(LiveActivity.class.getName(), "连接失败");
                        }
                    });
        } else {
            chatListener();
        }
    }

    /**
     * 加入聊天时 获取新的消息
     */
    private void chatListener() {
        RongIMClient.getInstance().joinChatRoom(mChatroomId, 20,
                new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.t(LiveActivity.class.getName(), "加入聊天室成功_:"
                                + mChatroomId);
                        // getHistoryReply();
//                        sendMessage("进入了房间");
                    }

                    @Override
                    public void onError(
                            io.rong.imlib.RongIMClient.ErrorCode arg0) {
                        LogUtils.t(LiveActivity.class.getName(), "加入聊天室失败");
                        chatConnectServer();
                    }
                });
        RongIMClient
                .setOnReceiveMessageListener(new OnReceiveMessageListener() {

                    @Override
                    public boolean onReceived(Message arg0, int arg1) {

                        // 输出消息类型。
//                        MessageContent content = arg0.getContent();
                        // 此处输出判断是否是文字消息，并输出，其他消息同理。
                        if (arg0.getContent() instanceof TextMessage) {
                            final TextMessage textMessage = (TextMessage) arg0
                                    .getContent();

                            LogUtils.t("----", textMessage.getContent() + "_");
                            if (textMessage.getContent() != null
                                    && textMessage.getContent()
                                    .contains("type")) {
                                XiuchanMessage message = JSON.parseObject(
                                        textMessage.getContent(),
                                        XiuchanMessage.class);
                                message.setMsgId(arg0.getMessageId());
                                message.setTime(TimeTool.getTimeStr(new Date(
                                        arg0.getSentTime()), getActivity()));
                                android.os.Message object = new android.os.Message();
                                object.obj = message;
                                object.what = 2;
                                handler.sendMessage(object);
                            }
                        }
                        return false;
                    }

                });

    }

    /**
     * 退出聊天室
     */
    private void chatQuit() {
//		if (mChatroomId == null) {
        if (TextUtils.isEmpty(mChatroomId)) {
            return;
        }
        RongIMClient.getInstance().quitChatRoom(mChatroomId,
                new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.t(LiveActivity.class.getName(), "退出聊天室成功");
                    }

                    @Override
                    public void onError(
                            io.rong.imlib.RongIMClient.ErrorCode arg0) {
                        LogUtils.t(LiveActivity.class.getName(), "退出聊天室失败");
                    }
                });
    }

    // 发送消息的封装。
    public void sendMessage(String value) {
        final XiuchanMessage sendXiuchangMsg = getJavaBean(value);
        //
        TextMessage messageContent = TextMessage.obtain(JSON
                .toJSONString(sendXiuchangMsg));
        if (TextUtils.isEmpty(mChatroomId)) {
            wsb.btn_send.setEnabled(true);
            Utils.toast(getActivity(), "加入聊天室失败，请稍后重试");
            return;
        }
        RongIMClient.getInstance().sendMessage(ConversationType.CHATROOM,
                mChatroomId, messageContent, userBean.getId(), "",
                new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer arg0, ErrorCode arg1) {
                        wsb.btn_send.setEnabled(true);
                        LogUtils.t(LiveActivity.class.getName(), "发送消息失败");
                    }

                    @Override
                    public void onSuccess(Integer arg0) {
                        wsb.btn_send.setEnabled(true);
                        if (sendXiuchangMsg == null) {
                            return;
                        }
                        LogUtils.t(LiveActivity.class.getName(), "发送消息成功");
                        // 评论
                        if (sendXiuchangMsg.getToUserName() == null) {
                            sendXiuchangMsg.setMsgId(arg0);
                            sendXiuchangMsg.setTime("刚刚");
                        }
                        // 回复
                        if (sendXiuchangMsg.getToUserName() != null) {
                            RequestParams params = UrlTool.getPostParams("content",
                                    JSON.toJSONString(sendXiuchangMsg),
                                    "msgId", String.valueOf(arg0),
                                    Constants.CHATROOM_ID, mChatroomId);
                            SendActtionTool.post(XiuchangParams.XiuChang_HUIFU,
                                    ServiceAction.Action_xiuchang,
                                    XiuchangAction.ACTION_HUIFU,
                                    ZhiBoFragment.this, params);
                        }
                        android.os.Message message = new android.os.Message();
                        message.what = 1;
                        message.obj = sendXiuchangMsg;
                        handler.sendMessage(message);

                    }
                }, new ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message arg0) {
                        wsb.btn_send.setEnabled(true);
                        LogUtils.t(this.getClass().getName(),
                                "发送消成功:" + arg0.toString());

                    }

                    @Override
                    public void onError(ErrorCode arg0) {
                        wsb.btn_send.setEnabled(true);
                        LogUtils.t(this.getClass().getName(),
                                "发送消息失败:" + arg0.toString());
                    }
                });

    }

    @Override
    public void onSuccess(ServiceAction service, Object action, Object value) {
        switch ((XiuchangAction) action) {
            case ACTION_HUIFU:
                break;
            default:
                break;
        }
    }


    private XiuchanMessage getJavaBean(String value) {
        XiuchanMessage sendXiuchangMsg = new XiuchanMessage();
        sendXiuchangMsg.setMsg(CheckCode.checkSensitive(getActivity(), value));
        sendXiuchangMsg.setFromUserName(userBean.getUserName());
        sendXiuchangMsg.setFromUserId(userBean.getId());
        sendXiuchangMsg.setType(1);
        sendXiuchangMsg.setFromUserPic(userBean.getFaceUrl());
        sendXiuchangMsg.setToUserName(null);
        if (toUserName != null) {
            sendXiuchangMsg.setType(5);
            sendXiuchangMsg.setToUserName(toUserName);
            toUserName = null;
        }
        return sendXiuchangMsg;
    }

    @Override
    public void onDestroy() {
        chatQuit();
        super.onDestroy();
    }

    private void addMessage(XiuchanMessage message) {
        if (adapterChat == null) {
            return;
        }
        if (message.getType()==8) {
            return;//某某某进入了房间
        }
        chatDatas.add(message);
        addMessage();
    }

    private void addMessage() {

        adapterChat.notifyDataSetChanged();
        int size = chatDatas.size();
        listViewChat.setSelection(size == 0 ? size : size - 1);
    }
}
