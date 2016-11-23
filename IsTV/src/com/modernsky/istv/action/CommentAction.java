package com.modernsky.istv.action;

/**
 * @author rendy 评论事件归纳
 */
public enum CommentAction {

    Action_Serach_Keywords,
    Action_Check_AnchorState,//获取审核状态
    Action_Report,//举报
    Action_Get_PeopleInfo,//获取某人的信息
    Action_AttenTion_People,//关注某人
    Action_send_Close,//关注某人
    Action_GetDetailInfo_Apply,//获取申请详情页 的信息
    Action_GetVideo_By_Id, Action_GetLastVideo_By_Id,
    //
    Action_getAlbumVideo, Action_addPinglun, Action_getPinglunList, Action_getHuifuList,
    //
    Action_addHuifu, Action_addPraiseComment, Action_addPraiseResouce, Action_getPraiseAndShoucang,
    //
    Action_shoucang_video, Action_shoucang_videoList, Action_shoucang_albumList,
    //
    Action_addPlayRecord, Action_getVideoAd, Action_delshoucang_video, Aciton_getSearchHot, Action_getSearchType,
    //
    Action_getArtistAllVideo, Action_getArtistDetail, Action_getPraiseCount, Action_GetVideo_Restart,
    Action_getCalendarVal, Action_Yuyue, Action_ArtistPersonList, ACTION_GETANCHORINFO, Action_addPlayToServices,
    ACTION_TimerShaft,ACTION_TimerShaft_IsShow
}
