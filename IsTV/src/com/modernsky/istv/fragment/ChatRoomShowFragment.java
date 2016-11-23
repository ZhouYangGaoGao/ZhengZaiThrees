package com.modernsky.istv.fragment;

/**
 *
 */

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.LiveActivity;
import com.modernsky.istv.acitivity.ShowActivity;
import com.modernsky.istv.adapter.AdapterPagerFragment;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.XiuchanMessage;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.ThreeAppParams;
import com.modernsky.istv.utils.Utils;
import com.umeng.socialize.utils.Log;

import java.util.Date;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.ChatRoomMemberInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-6-20 下午1:45:51
 * @类说明：
 */
public class ChatRoomShowFragment extends BaseFragment {

    private ShowActivity mShowActivity;
    //
    private String mChatroomId = "";
    private String toUserName;
    private UserEntity userBean;
    private String toUserId;
    //
    // 秀场消息列表
    private ForegroundColorSpan blueSpan;
    private ForegroundColorSpan writeSpan;
    private ForegroundColorSpan blueSpan_zhengzai;
    private UserEntity singerEntity;
    private String time;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescreption() {
        return descreption;
    }

    public void setDescreption(String descreption) {
        this.descreption = descreption;
    }

    private String descreption;

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    //
    private ViewPager viewPager = null;
    public ChatFragment cf = null;
    //    public GiftFragment gf = null;
    private RankFragment rf = null;
    //    private MoreFragment mf = null;
    private AnchorDetailFragment anchorFragment = null;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ShowActivity aty = (ShowActivity) getActivity();
            XiuchanMessage message = (XiuchanMessage) msg.obj;
            Log.w("xqp", msg.what + "");
            switch (msg.what) {
                // 发送聊天信息成功
                case 0:
                    GeneralTool.KeyBoardCancle(getActivity());
                    cf.addMessage(message);
                    aty.sendTanmu(message, true);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    //                    if (msg.what - 1 == 0) {
                    //                    }
                    viewPager.setCurrentItem(msg.what - 1);
                    break;
                // 接受到新的消息
                case 5:
                    if (message == null) {
                        return;
                    }
                    boolean isMyGift = false;
                    if (message != null) {
                        isMyGift = userBean.getId().equals(message.getFromUserId());
                    }


                    switch (message.getType()) {
                        // 顶部和底部部通知
                        case 3:
                        case 4:
                            LogUtils.d("isMyGift====" + isMyGift);
                            if (!isMyGift) {
                                cf.addMessage(message);
                                mShowActivity.sendTanmu(message, false);
                                mShowActivity.queueDisplayGif(message, !TextUtils.isEmpty(message.getGifImgUrl()), false);
                            }
                            break;
                        // 顶端通知
                        case 2:
                            cf.addMessage(message);
                            mShowActivity.sendTanmu(message, isMyGift);
                            break;
                        case 1:
                        default:
                            mShowActivity.sendTanmu(message, false);
                            cf.addMessage(message);
                            addPeopleNum(message);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public int peopleNum = 1;

    private void addPeopleNum(XiuchanMessage message) {
        if (message.getType() == 8) {
            peopleNum++;
            //            mShowActivity.setPeopleNum(peopleNum);
        }
        if (message.getType() == 7) {
            long addTime = 0;
            addTime = message.getAddTime();
            mShowActivity.updateLeftTime(addTime);
        }
        if (message.getType() == 6) {
            mShowActivity.setLeftSecends(0);
            mShowActivity.getTimer().cancel();
            mShowActivity.getMyMediaController().getmTimeLeft().setText("直播结束");
            mShowActivity.getMyMediaController().getVerticalTimeText().setText("直播结束");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mShowActivity = (ShowActivity) getActivity();
        //        userBean = UserService.getInatance().getUserBean(mShowActivity);
        userBean = mShowActivity.getUserBean();
        String mChatroomId = ChatRoomShowFragment.this.getArguments()
                .getString("id");
        setChatroomId(mChatroomId);
        return inflater.inflate(R.layout.fragment_chatroom_, container, false);
    }

    @Override
    public void initView(View rootView) {

        Bundle bundle = new Bundle();
        bundle.putString("toUserName", toUserName);
        bundle.putString("toUserId", toUserId);
        bundle.putString("mChatroomId", mChatroomId);
        bundle.putSerializable("userBean", userBean);
        cf = new ChatFragment();
        //        cf = new ChatFragment(toUserName, toUserId, mChatroomId, userBean);
        cf.setArguments(bundle);

        rf = new RankFragment();
        bundle = new Bundle();
        bundle.putBoolean("isLive", true);
        anchorFragment = new AnchorDetailFragment();
        anchorFragment.setArguments(bundle);
        LogUtils.d("toUserName==" + toUserName, "toUserId==" + toUserId + "mChatroomId==" + mChatroomId + "userBean==" + userBean.toString());
        blueSpan = new ForegroundColorSpan(getResources().getColor(
                R.color.textgray));
        writeSpan = new ForegroundColorSpan(getResources().getColor(
                R.color.white));
        blueSpan_zhengzai = new ForegroundColorSpan(Color.parseColor("#9fd9f6"));
        cf.setSpan(blueSpan, writeSpan, blueSpan_zhengzai);
        initViewPager(rootView);
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
        // edtChat.setHint(null);
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

    int reContect = 3;

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
            LogUtils.t(ShowActivity.class.getName(),
                    "开始连接容云服务" + UserService.getInatance().getUserBean(mShowActivity).getRongcloudToken());
            RongIMClient.connect(UserService.getInatance().getUserBean(mShowActivity).getRongcloudToken(),
                    new RongIMClient.ConnectCallback() {
                        @Override
                        public void onSuccess(String arg0) {
                            ThreeAppParams.IS_CONNECT_SERVER = true;
                            LogUtils.t(ShowActivity.class.getName(),
                                    "链接容云服务器成功");
                            chatListener();
                            RongIMClient.getInstance().getConversationList();
                        }

                        @Override
                        public void onTokenIncorrect() {
                            LogUtils.t(ShowActivity.class.getName(), "连接失败");
                        }

                        @Override
                        public void onError(
                                io.rong.imlib.RongIMClient.ErrorCode arg0) {
                            LogUtils.t(ShowActivity.class.getName(), "连接失败" + arg0);
                        }
                    });
        } else {
            chatListener();
        }
    }

    /**
     * //获取聊天室信息
     */
    private void getChatRoomInfo() {
        RongIMClient.getInstance().getChatRoomInfo(mChatroomId, 20, ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC, new RongIMClient.ResultCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo chatRoomInfo) {
                int count = chatRoomInfo.getTotalMemberCount();
                List<ChatRoomMemberInfo> memberInfo = chatRoomInfo.getMemberInfo();
                String ids = "";
                for (int i = 0; i < memberInfo.size(); i++) {
                    if (i == 0) {
                        ids += memberInfo.get(i).getUserId();
                    } else {
                        ids += "," + memberInfo.get(i).getUserId();
                    }
                    LogUtils.d("peopleid" + memberInfo.get(i).getUserId());
                }
                LogUtils.d("count=====" + count);
                //                String userId = memberInfo.get(0).getUserId();
                peopleNum = count;
                mShowActivity.setPeopleNum(count);
                mShowActivity.getPeopleInfo(ids);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    public void updateUserEnty(UserEntity userEntity, String time, String location, String descreption) {
        this.singerEntity = userEntity;
        this.time = time;
        this.location = location;
        this.descreption = descreption;
        LogUtils.d("updateUserEnty----outer");
        if (anchorFragment != null && anchorFragment.isAdded()) {
            anchorFragment.updateVideoInfo(time, location, descreption);
            anchorFragment.updateUserEnty(userEntity);
            LogUtils.d("updateUserEnty----inner");
        }

    }


    /**
     * 加入聊天时 获取新的消息
     */
    private void chatListener() {
        if (TextUtils.isEmpty(mChatroomId)) {
            return;
        }
        RongIMClient.getInstance().joinChatRoom(mChatroomId, 20,
                new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.t(LiveActivity.class.getName(), "加入聊天室成功_:"
                                + mChatroomId);
                        getChatRoomInfo();//获取聊天室信息

                        // zwz 去除某某进入了房间
                        //                        cf.setEditText("进入了房间", true);
                        cf.setEditText("", true);
                    }

                    @Override
                    public void onError(
                            io.rong.imlib.RongIMClient.ErrorCode arg0) {
                        LogUtils.t(LiveActivity.class.getName(), "加入聊天室失败" + arg0.toString());
                        chatConnectServer();
                    }
                });
        RongIMClient
                .setOnReceiveMessageListener(new OnReceiveMessageListener() {
                    @Override
                    public boolean onReceived(Message mes, int arg1) {
                        // 输出消息类型。
                        MessageContent content = mes.getContent();
                        LogUtils.d("message-----" + mes.toString());
                        // 此处输出判断是否是文字+消息，并输出，其他消息同理。
                        if (!(content instanceof TextMessage)) {
                            return false;
                        }
                        String string = ((TextMessage) content).getContent();
                        LogUtils.d("string======" + string);
                        if (string == null || !string.contains("type")) {
                            return false;
                        }
                        XiuchanMessage obj = JSON.parseObject(string,
                                XiuchanMessage.class);
                        obj.setMsgId(mes.getMessageId());
                        obj.setTime(TimeTool.getTimeStr(
                                new Date(mes.getSentTime()), getActivity()));
                        android.os.Message message = new android.os.Message();
                        message.obj = obj;
                        message.what = 5;
                        handler.sendMessage(message);
                        return false;
                    }

                });

    }

    /**
     * 退出聊天室
     */
    private void chatQuit() {
        if (mChatroomId == null) {
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

    @Override
    public void onDestroy() {
        chatQuit();
        super.onDestroy();
    }

    private void initViewPager(View rootView) {
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        AdapterPagerFragment pagerAdapter = new AdapterPagerFragment(getChildFragmentManager());
        //        pagerAdapter.addFragment(gf);
        pagerAdapter.addFragment(cf);
        pagerAdapter.addFragment(rf);
        //        pagerAdapter.addFragment(mf);
        pagerAdapter.addFragment(anchorFragment);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        if (rf != null && rf.isAdded()) {
                            rf.setmChatroomId();
                        }
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // Log.i("onPageScrolled", arg0 + "  " + arg1 + "   " + arg2);
                //                if (arg1 == 0) {
                //                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // Log.w("onPageScrollStateChanged", arg0 + "");
                switch (arg0) {
                    case 2:
                        break;
                }
            }
        });
    }

    // 刷新界面列表
    public Handler getHandler() {
        return handler;
    }

    public String getToUserId() {
        return cf.getToUserId();
    }

    public void backImpress() {
        cf.backImpress();
    }

    public String getChatRoomId() {
        return mChatroomId;
    }

    public UserEntity getSingerEntity() {
        return singerEntity;
    }

    public void setSingerEntity(UserEntity singerEntity) {
        this.singerEntity = singerEntity;
    }
}
