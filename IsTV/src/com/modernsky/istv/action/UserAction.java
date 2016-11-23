package com.modernsky.istv.action;

/**
 * @author rendy 用户事件归纳
 */
public enum UserAction {
    Action_login(null), Action_Login_OUT(null), Action_Regiser_Phone(null), Action_Regiser_Phone_Code(
            null), Action_Regiser_Email(null), Action_Update_UserName(null), Action_Update_Face_Url(
            null), Action_Update_Email(null), Action_Update_Mobiles(null), Action_OPEN_CHECK(
            null), Action_OPEN_LOGIN(null), Action_FindPsd_Phone(null), Action_FindPsd_EMAIL(
            null), Action_Push_Acton(null), Action_Push_Acton_Out(null), Action_CHECK_VERSION(
            null), Action_CHECK_ONE(null), Action_BINDING(null), Action_GETTOKEN(
            null), ACTION_CHECK_SIGN(null),
    // 喜欢和删除
    Action_AddOrDeleteSong(null),
    // 获取个人信息
    Action_GetPeopleInfo(null),
    // 取消喜欢和删除
    Action_DisLikeSong(null),
    // 观看纪录
    Action_See_Leave(null),
    // 删除记录
    Action_See_Leave_DEL(null),
    // 收藏
    Action_Shoucang(null), Action_Shoucang_Del(null),
    // 添加预约
    Action_Yuyue_Add(null),
    // 取消预约
    Action_Yuyue_Cancle(null),
    // 预约list
    Action_Yuyue_LIST(null),
    // 订单列表
    Action_GET_ORDERLIST(null),
    // 更新敏感词库
    Action_CHECK_CODE_VERSION(null),
    // 获取音乐列表
    Action_GET_MUSIC_LIST(null),
    // 用户反馈
    Action_USER_REPORT(null),
    // 二维码登录OTT
    ACTION_LOGIN_OTT(null),
    //申请成为主播
    ACTION_APPLYFOR_ANCHOR(null),
    //使用power来获取时长
    ACTION_ADDTIME_USERPOWER(null),
    //结束直播
    ACTION_END_PUBLISH(null),
//     获取主播的得到的赞 power 等

    ACTION_GETANCHOR_INFO(null),

    // zwz
    ACTION_ZHIBO_END(null),
    // zwz 周.月.季度
    Action_GET_ANY_ZHUBO_RANK(null),


    //获取上传视频信息
    ACTION_GETINFO_UPDATEVIDEO(null),
    //获取聊天室的人物信息
    ACTION_GETPEOPLEINFOS(null),
    //返还服务器 上传到乐事的视频信息
    ACTION_RETURNVIDEOINFO_SERVER(null),
    //发布预告
    ACTION_PUBLISHPREVIUE(null),
    //获取单个用户息息
    ACTION_GET_USERENTITY(null),
    //获取单个用户息息
    ACTION_GET_LETTERINFO(null),

    //获取关注人列表信息
    ACTION_GET_FOCUS_LIST(null),
    //获取粉丝列表信息
    ACTION_GET_FANS_LIST(null),
    //主播作品列表
    ACTION_VIDEO_LIST(null),
    //删除作品
    ACTION_WORKS_DEL(null),
    //添加草莓
    ACTION_ADD_CAOMEI(null),
    //任务列表
    ACTION_TASK_LIST(null),
    //做任务
    ACTION_DO_TASK(null),
    //主播端 获取 videoDetail
    ACTION_GETVIDEO_DETAIL(null),

    //添加关注
    ACTION_USER_ATTENTION(null),
    //推流界面 获取 推流地址  和
    ACTION_LIVEPUBLISH(null),


    //主播排行榜
    Action_GET_ZHUBO_RANK(null),
    //直播间的人数
    Action_GET_TOTLE_NUM(null),
    //学校列表
    Action_GET_SCHOOLLIST(null),
    //城市列表
    Action_GET_CITYLIST(null),
    //发布预告前的准备工作
    Action_GET_BEFOREPUBLISH(null),
    //站内信
    ACTION_NOTICE_LIST(null), ACTION_NOTICE_READ(null), ACTION_NOTICE_DEL(null), ACTION_LIVE_STOP(null), ACTION_GET_USER_VIDEO(null), ACTION_SEND_OPENINFO(null), ACTION_SEND_CLOSEINFO(null), ACTION_GET_UNREAND_COUNT(null);


    public Object value;

    private UserAction(Object value) {
        this.value = value;
    }

}

