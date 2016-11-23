package com.modernsky.istv.utils;

/**
 * Created by zxm on 2015/3/23.
 */
public class Constants {
    public static final String PROTOCOL = "http" + "://";
    public static final String URL_BASE_ZHENGSHI_READ = "http://data.zhengzai.tv";//正式服务器地址
    public static final String URL_BASE_ZHENGSHI_WRITE = "http://media.zhengzai.tv";//正式服务器地址
    public static final String URL_BASE_ZHENGSHI_LOG = "http://stat.zhengzai.tv";//正式服务器地址日志系统的

    // zwz 20160919(接口的重建)
    // public static final String URL_TEMP_ZHENGSHI_LOG = "http://t.zhengzai.tv:8686";//测试服务器地址 日志系统的
    public static final String URL_TEMP_ZHENGSHI_LOG = "http://stat.zhengzai.tv";//测试服务器地址 日志系统的


    public static final String URL_BASE_TEMP_READ = "http://t.zhengzai.tv:8585";// 测试服务器地址

    // zwz 20160919(接口的重建)
    // public static final String URL_BASE_TEMP_WRITE = "http://t.zhengzai.tv:8484";// 测试服务器地址
    public static final String URL_BASE_TEMP_WRITE = "http://121.42.148.100:8383";// 测试服务器地址

    public static final String tagUrl_temp = "http://t.zhengzai.tv/pages/videoshare.html?vbreId="; //正式点播分享地址
    public static final String tagLiveUrl_temp = "http://t.zhengzai.tv/pages/live.html?vbreId=";//正式直播分享地址

    public static final String tagUrl_base = "http://wap.zhengzai.tv/pages/videoshare.html?vbreId="; //测试点播分享地址
    public static final String tagLiveUrl_base = "http://wap.zhengzai.tv/pages/live.html?vbreId=";//测试直播分享地址

    public static final String tagUrl = (LogUtils.debug) ? tagUrl_temp
            : tagUrl_base;//
    public static final String tagLiveUrl = (LogUtils.debug) ? tagLiveUrl_temp
            : tagLiveUrl_base;//
    public static final String URL_BASE_LOG = (LogUtils.debug) ? URL_TEMP_ZHENGSHI_LOG
            : URL_BASE_ZHENGSHI_LOG;//
    public static final String URL_BASE_READ = (LogUtils.debug) ? URL_BASE_TEMP_READ
            : URL_BASE_ZHENGSHI_READ;//
    public static final String URL_BASE_WRITE = (LogUtils.debug) ? URL_BASE_TEMP_WRITE
            : URL_BASE_ZHENGSHI_WRITE;//

    // 测试服务器地址
    public static final String ALBUM_NAME = "album_name";
    public static final String URL_MP4 = "http://f01.v1.cn/group1/M00/1E/71/ChQBFVUb4AaAbeK-AiTSgVNkyeQ619.mp4";
    public static final String U_I = "U-I";//网络请求头信息
    public static final String CITYIDS = "cityIds";
    public static final String SORT = "sort";
    public static final String SINGER_ID = "singerId";
    public static final String ACTION_USERBEAN_CHANGE = "ACTION_USERBEAN_CHANGE";
    public static String URL_AGREEMENT = "http://wap.zhengzai.tv/showpages/agreement.html";
    public static String URL_MIANZE = "http://wap.zhengzai.tv/showpages/protocol.html";
    //    public static String URL_ABOUT = "http://182.92.80.2/showpages/aboutus.html";
    // public static String URL_LIVE = "rtmp://live.v1.cn/v1live/v1live";
    public static String URL_LIVE = "http://r.gslb.lecloud.com/live/hls/20150422300000916/desc.m3u8";
    public static String URL_LIVE_TEMP = "http://img1.peiyinxiu.com/2015020312092f84a6085b34dc7c.mp4";


    public static String URL_LIVE_ENGINE = URL_BASE_LOG + "/liveEngine";
    public static String URL_LOG_ENGINE = URL_BASE_LOG + "/logEngine";
    public static String URL_EXIT_SERVLET = URL_BASE_LOG + "/exitServlet";
    public static String URL_RECORD_SERVLET = URL_BASE_LOG + "/recordServlet";
    public static String URL_GET_NUMINLIVE = URL_BASE_LOG + "/totals";

    //获取用户创建视频来自乐视的资源信息
    //    http://60.190.249.55:8090/vrs/api/getUploadVideoUrl.jsn?videoName=ee
    public static String URL_INFO_UPDATEVIDEO = URL_BASE_WRITE + "/vrs/api/getUploadVideoUrl.jsn";
    //直播预览页
    //    http://60.190.249.55:8090/vrs/api/getUploadVideoUrl.jsn?videoName=ee
    //    public static String URL_BEFORE_LIVEPUBLISH = "http://60.190.249.55:89/pages/order.html";
    //上传视频信息
    public static String URL_RETURNVIDEOINFO_SERVER = URL_BASE_WRITE + "/vrs/api/create_by_user_play.jsn";
    //    //获取学校列表
    //    public static String URL_GETSCHOOL = URL_BASE_READ + "/search/schoolIndexSearch";
    //获取城市 和 学校 列表
    public static String URL_GETCITY = URL_BASE_READ + "/search/schoolIndexSearch";

    /**
     * ==============================朱洧志 start================================
     */
    // http://210.14.158.158:8686(测试) 20160919
    // URL_BASE_LOG(正式)
    public static String URL_ZHIBO_SERVICE = "http://210.14.158.158:8686";

    // 分享的接口
    public static String URL_ZHIBO_SHARE = URL_BASE_LOG + "/shareEngine";
    // 进入直播
    // public static String URL_ZHIBO_START = URL_ZHIBO_SERVICE + "/logEngine";
    // 退出直播
    public static String URL_ZHIBO_END = URL_BASE_LOG + "/endEngine";


    //获取时间轴起点
    public static String URL_TIMESHAFT = "http://stat.zhengzai.tv/timeEngine";
    //获取时间轴排行榜是否可点击
    public static String URL_TIMESHAFT_ISSHOW = "http://stat.zhengzai.tv/isShowEngine";

    /**
     * ==============================朱洧志   end================================
     */


    // public static String URL_LIVE =
    // "rtmp://r.gslb.lecloud.com/live/20150422300000916";
    // public static String URL_LIVE =
    // "rtmp://broadcast.zhengzai.tv/live/channeltest1";
    public static String URL_CALENDAR_VAL = URL_BASE_READ
            + "/info/schedule/scheduleList";// 日历接口
    public static String URL_SEARCH_KEYWORDS = URL_BASE_READ + "/search/bykeywords";
    public static String URL_SEARCH_HOT = URL_BASE_READ + "/info/datadict/search";
    public static String URL_SEARCH_TYPE = URL_BASE_READ
            + "/info/channel/channelList";// 搜索类型接口
    public static String URL_RANK = URL_BASE_READ + "/info/rank/getList";
    //http://stat.zhengzai.tv/totals?videoId=123456
    public static String URL_TOPIC = URL_BASE_READ + "/info/collect/getList";
    public static final String URL_HOMEPAGE = URL_BASE_READ + "/info/foucs/homePage";
    public static String URL_FOCUSLIST = URL_BASE_READ + "/info/foucs/focusList";
    public static String URL_ALL_VIDEO_LIST = URL_BASE_READ
            + "/info/video/queryAllVideoList";// 获取视频列表
    public static String URL_GET_ARTIST_ALL_VIDEO = URL_BASE_READ
            + "/info/collect/getCollectDetail";// 获取艺人全部视频列表
    public static String URL_ALBUM_DETAIL = URL_BASE_READ
            + "/info/album/albumDetail";
    public static String URL_VIDEO_DETAIL = URL_BASE_READ
            + "/info/video/videoDetail";
    public static String URL_VIDEO_NEXT_PLAY_INFO = URL_BASE_READ
            + "/info/video/nextPlayInfo";
    public static String URL_GET_VIDEO_AD = URL_BASE_READ + "/info/ad/getAd?type=2";// 视频暂停广告
    public static String URL_GET_START_AD = URL_BASE_READ + "/info/ad/getAd?type=1";// app启动广告
    public static String URL_GET_TICKET_LIST = URL_BASE_READ
            + "/info/ticket/ticketList";// 获取票务信息
    public static final String URL_GET_SUBJECT = URL_BASE_READ
            + "/info/foucs/getSubject";// 获取事件活动页
    public static String URL_GET_STARDETAIL = URL_BASE_READ
            + "/info/collect/getStarDetailByObjId";// 艺人介绍页
    public static final String URL_GET_MUSICLIST = URL_BASE_READ
            + "/info/music/queryMusicPage";// 获取电台列表
    public static final String URL_GET_USER_VIDEO = URL_BASE_READ
            + "/info/video/getUserVideoList";// 获取用户(主播) 视频列表数据
    /**
     * 获取推流的信息  主要是 推流的地址  和 mchatroomId
     */
    public static final String URL_GET_LIVEPUBLISHINFO = URL_BASE_READ
            + "/info/video/getPushUrlAndChatroomId";
    /**
     * 获取申请主播的详情页的信息
     */
    public static final String URL_GET_DETAILINFO_APPLY = URL_BASE_READ
            + "/info/detail/getApplyPageDetail";
    /**
     * 获取城市列表
     */
    public static final String URL_GET_CITYS = URL_BASE_READ
            + "/info/collect/getcitys";

    public static final String TYPE_ARTIST = "TYPE_ARTIST";

    public static final String ALBUM_ID = "albumId";
    public static final String OBJECT_ID = "objectId";
    public static final String COLLECT_ID = "collectId";
    public static String VIDEO_ID = "videoId";
    public static String VIDEO_NAME = "videoName";
    public static String VIDEO_PIC = "videoPic";
    public static final String SOURCE = "source";
    public static String MOBILE = "MOBILE";
    public static String PLAY_TIME = "playTime";
    public static String LIVE = "LIVE";
    public static String FILTER = "filter";
    // public static String FILTER_ALBUM =
    // "latestVideo,name,videoPlayInfo,videoId,videoUrl,standardPic,vedioType,liveInfo,isPay,isNeedPay";
    public static String FILTER_ALBUM = "isVRsource,latestVideo,name,videoPlayInfo,videoId,videoUrl,standardPic";
    public static String FILTER_ALBUM_detail = "data,albumId,name,latestVideo,videoId";
    public static String FILTER_OBJECTID = "name,targetId,data,videoPlayInfo,videoId,videoUrl,standardPic,videoType,isVRsource";

    public static String FILTER_VIDEO_PLAYER = "isVRsource,location,description,name,albumId,data,showTime,time,endTime,videoPlayInfo,videoId,foreignVid,foreignUnique,videoUrl,standardPic,videoType,liveInfo,liveInfoId,activityId,streamId,type,msg,rtmp,hls,isPay,isNeedPay,chatroomId";
    public static String FILTER_VIDEO_SHOW = "userinfo,userId,location,description,name,albumId,data,catalog,stageName,detail,starrName,showTime,time,endTime,videoPlayInfo,videoId,foreignVid,foreignUnique,videoUrl,standardPic,videoType,liveInfo,liveInfoId,activityId,streamId,type,msg,rtmp,hls,isPay,isNeedPay,chatroomId";
    public static String FILTER_VIDEO = "name,albumId,data,catalog,stageName,detail,starrName,showTime,time,endTime,videoPlayInfo,videoId,foreignVid,foreignUnique,videoUrl,standardPic,videoType,liveInfo,liveInfoId,activityId,streamId,type,msg,rtmp,hls,isPay,isNeedPay,chatroomId";

    public static String FILTER_ALBUM_VIDEO = "album,videoId,name,videoPlayInfo,videoUrl,standardPic,videoType,showTime,time,isVRsource";//
    public static String FILTER_ARTIST_ALL_VIDEO = "videoId,standardPic,videoType,album,name,videoPlayInfo,videoUrl";// 艺人所有视频过滤字段
    public static final String PINGLUN_TEXT = "pingun_text";

    public static final String PINGLUN_IMG = "pingun_img";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String USER_ENTITY = "userEntity";
    public static final String DATA = "data";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String STARTTIME = "startTime";
    public static final String ENDTIME = "endTime";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String USER_ID = "userId";
    public static final String TO_USER_ID = "toUserId";
    public static final String AUDIO_ID = "audioId";
    public static final String IS_LIKE = "isLike";
    public static final String AlbumName = "albumName";
    public static final String BUILD_TIME = "buildTime";
    public static final String QR_CODE_TOKEN = "qrcodeToken";
    public static final String RESOURCE_ID = "resourceId";
    public static final String COMMENT_ID = "commentId";
    public static final String CONTENT = "content";
    public static final String ID = "id";

    /**
     * 首页
     */
    public static final String TYPE_FIRSTPAGE = "1";
    /**
     * 秀场列表
     */
    public static final String TYPE_SHOW_LIST = "3";
    /**
     * 演唱会
     */
    public static final String TYPE_YanChangHui = "3";
    /**
     * live House
     */
    public static final String TYPE_LIVE_HOUSE = "4";
    /**
     * mtv
     */
    public static final String TYPE_MTV = "5";
    /**
     * 直播列表
     */
    public static final String TYPE_ZhiBo = "100";

    public static final String BODY = "body";// 商品描述
    public static final String SUBJECT = "subject";// 商品描述
    public static final String TOTAL_FEE = "totalFee";// 总金额
    public static final String CLIENT_IP = "clientIp";// ip
    public static final String TRADE_TYPE = "tradeType";// 客户端类型
    public static final String APP = "APP";
    public static final String VOUCHER_CODE = "voucherCode";// 优惠码
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String ACTION_PAY_RESULT = "ACTION_PAY_RESULT";// 支付结果
    public static final String ACTION_LOGIN_CHANGE = "ACTION_LOGIN_CHANGE";// 登录状态改变
    public static final String ACTION_GETUSERINFO = "ACTION_GETUSERINFO";// 登录状态改变
    public static final String OUT_TRADE_NO = "outTradeNo";
    public static final String TRADE_STATE = "tradeState";
    public static final int LUNBO_TIME = 4000;// 轮播时间间隔 毫秒
    public static final String SHOW_TIME = "演出时间表";
    public static final String POSOTION = "POSOTION";
    public static final String VIDEO = "VIDEO";
    public static final String MB = "MB";
    public static final String MONEY = "MONEY";
    public static final String CHATROOM_ID = "chatroomId";
    public static final String ARTIST_ALL_VIDEO = "ARTIST_ALL_VIDEO";
    // public static final String URL_SHARE =
    // "http://wap.zhengzai.tv/share/download_wx.html";
    public static final String URL_SHARE = "http://a.app.qq.com/o/simple.jsp?pkgname=com.modernsky.istv";
    public static final String INDEX = "index";
    public static final String Id_ZhengXiaoZai = "5532154e0cf293b15141f25d";// 正小在
    public static final String ANDROID_MOBILE = "ANDROID_MOBILE";
    public static final String VERSION = "version";
    public static String ACTIVITY_ID = "activityId";
    public static String IsQrcode = "isQrcode";
    public static String Sharetit = "sharetit";
    public static String Sharedes = "sharedes";
    public static String Shareimg = "shareimg";

    /**
     * @author rendy 用户中心 信息参数
     */
    public static class UserParams extends Constants {
        public static final String Base_zhengshi_write = "http://uin.zhengzai.tv";// 正式操作类接口
        public static final String Base_zhengshi_read = "http://ure.zhengzai.tv";// 正式显示类接口
        public static final String Base_temp_write = "http://t.zhengzai.tv:8181";// 测试操作类接口

        // zwz 20160919(接口的重建)
        // public static final String Base_temp_read = "http://t.zhengzai.tv:8282";// 测试显示类接口
        public static final String Base_temp_read = "http://121.42.148.100:8585";// 测试显示类接口

        public static final String Base_write = (LogUtils.debug) ? Base_temp_write
                : Base_zhengshi_write;
        public static final String Base_read = (LogUtils.debug) ? Base_temp_read
                : Base_zhengshi_read;
        /**
         * 注册接口
         **/
        public static final String URL_REGISTER = Base_write + "/m/user/register";
        /**
         * 用户的头像地址
         **/
        public static final String USER_URL = "http://pic.zhengzai.tv/";
        // public static final String USER_URL =
        // "http://pic-zhengzai-tv.oss-cn-qingdao.aliyuncs.com/";
        public static final String URL_REGISTER_CODE = Base_write + "/m/user/code";
        public static final String URL_REGISTER_CODE_MODIFY = Base_write
                + "/m/user/codeForModifyPassword";
        public static final String URL_REGISTER_LOGIN = Base_read + "/m/user/login";
        /**
         * 获取直播间的观众
         */
        public static final String URL_GETPEOPLE_LISTS = Base_read + "/m/user/list";

        //用户信息编辑接口
        public static final String URL_USER_UPDATE = Base_write + "/m/user/update";
        /**
         * 直播结束
         */
        public static final String URL_END_PUBLISH = Base_write + "/m/video/live/stop";

        //直播间点赞
        public static final String URL_PRICEANCHOR = Base_write + "/m/show/chatroom/praise";
        /**
         * 检测版本更新
         **/
        public static final String URL_Check_VERSION = Base_read
                + "/api/check_version";
        /**
         * 查询单个聊天室 的主播 人气  mb个数  power个数等
         **/
        public static final String URL_GETANCHORINFO = Base_read
                + "/m/show/chatroom/one";
        /**
         * 检测版本更新
         **/
        public static final String URL_GET_MUSIC = Base_read + "/m/audio/play/";
        /**
         * 查询个人是否是主播及审核的状态；
         **/
        public static final String URL_GET_ISANCHOR = Base_read + "/m/dorm/one";
        /**
         * 使用Power 来增加 时常
         **/
        public static final String URL_ADDTIME_USERPOWER = Base_write + "/m/show/gift/useStraw";
        // 通过手机获取验证码
        public static final String URL_USER_UPDATE_Phone_Psd = Base_write
                + "/m/user/update_password_mobile";
        // 通过邮箱获取验证码
        public static final String URL_USER_UPDATE_EMAIL_Psd = Base_write
                + "/m/user/update_password_email";
        public static final String URL_REGISTER_LOGOUT = Base_write
                + "/m/user/logout";
        public static final String URL_ADD_PUSH_TOKEN = Base_write
                + "/m/user/add/token";// 添加token
        public static final String URL_COMMENT_LIST = Base_read + "/m/comment/list";// 评论列表
        public static final String URL_COMMENT_ONE = Base_read + "/m/comment/one";// 评论回复列表
        public static final String URL_COMMENT_LIST_REPLY = Base_read
                + "/m/comment/list/reply";// 评论回复
        public static final String URL_ADD_COMMENT = Base_write
                + "/m/comment/add_comment";// 添加评论
        public static final String URL_ADD_PRAISE_COMMENT = Base_write
                + "/m/comment/add_praise/comment";// 对评论点赞
        public static final String URL_ADD_PRAISE_RESOUCE = Base_write
                + "/m/comment/add_praise/resource";// 对资源点赞
        public static final String URL_ADD_REPLY = Base_write
                + "/m/comment/add_reply";// 添加回复
        public static final String URL_ADD_PLAY_RECORD = Base_write
                + "/m/play_record/add";// 添加播放记录

        public static final String URL_GET_WEIXIN_ORDER = Base_write
                + "/api/wxpay/prepay_id";// 获取微信支付参数
        public static final String URL_GET_ALI_ORDER = Base_write
                + "/api/alipay/sign";// 获取支付宝支付参数
        public static final String URL_WEIXIN_BACK = Base_write + "/api/wxpay/back";// 获取微信支付结果回调
        /**
         * 检测 第三方的绑定信息
         **/
        public static final String URL_GET_ONE = Base_read + "/m/user/one";
        //关注/取消关注
        public static final String URL_ADD_ATTENTION = Base_write + "/m/user/attention";

        //粉丝/关注人列表
        public static final String URL_GET_ATTENTION_FANS = Base_read + "/m/user/friends";
        //主播作品列表
        public static final String URl_VIDEO_LIST = Base_read + "/m/video/list/user";

        //删除作品
        public static final String URL_WORKS_DEL = Base_write + "/m/video/delAndAdd";


        //提交草莓添加
        public static final String URL_CAOMEI_ADD = Base_write + "";
        //任务列表
        public static final String URL_TASK_LIST = Base_read + "/m/user/tasks";
        //做任务
        public static final String URl__DO_TASK = Base_write + "/m/user/doTask";


        // 添加预约
        public static final String URL_YUYUE_ADD = Base_write + "/m/subscribe/add";
        // public static final String URL_YUYUE_DEL = Base +
        // "/m/reservation/del";
        // 预约列表
        public static final String URL_YUYUE_LIST = Base_read + "/m/subscribe/list";
        // 观看记录
        public static final String URL_PLAY_RECORD = Base_read
                + "/m/play_record/list";
        // 删除播放记录
        public static final String URL_PLAY_RECORD_DEL = Base_write
                + "/m/play_record/del";
        // 收藏的专辑
        public static final String URL_COLLECT_LIST_ALBUM = Base_write
                + "/m/collection/album_list";
        // 收藏的视屏列表
        public static final String URL_COLLECT_LIST = Base_read
                + "/m/collection/list";
        // 添加收藏
        public static final String URL_COLLECT_ADD = Base_write + "/m/collection/add";
        // 删除收藏的视屏或者专辑
        public static final String URL_Dell_Collect_VIDEO = Base_write
                + "/m/collection/del";
        // 第三方登录
        public static final String URL_OPEN_LOGIN = Base_write + "/m/user/login/open";
        // 获取优惠券信息
        public static final String URL_YOUHUIQUAN = Base_read + "/m/voucher/list";
        // public static final String URL_ORDERLIST = Base + "/m/pay/list";
        //获取订单列表
        public static final String URL_ORDERLIST = Base_read + "/m/pay/listNew";
        // 使用优惠券
        public static final String URL_USE_YOUHUIQUAN = Base_write + "/m/voucher/use";
        public static final String URL_OPEN_Check = Base_read + "/m/user/check_open";
        public static final String URL_USER_BINDING = Base_write + "/m/user/binding";
        // 用户反馈
        public static final String URL_USER_REPORT = Base_write + "/m/feedback/add";
        public static final String URL_USER_GETTOKEN = Base_read + "/m/audio/token";
        //        public static final String URL_USER_ADDSONG = Base + "/m/audio/add";
        public static final String URL_USER_ADDSONG = Base_write + "/m/audio/like";
        //用户创建直播
        public static final String URL_LIVE_CREATE = Base_write + "/m/video/live/create";
        //是否赞和是否收藏
        public static final String URL_USER_IfHasZanAndShoucang = Base_read + "/m/collection/check";
        //站内信列表
        public static final String URL_NOTICE_LIST = Base_read + "/m/notice/list";
        //读消息
        public static final String URL_NOTICE_READ = Base_write + "/m/notice/read";
        //删除信息
        public static final String URL_NOTICE_DEL = Base_write + "/m/notice/del";
        //申请成为主播
        public static final String URL_APPLYFOR_ANCHOR = Base_write + "/m/apply/dorm/add";
        //发布预告
        public static final String URL_PUBLISH_PREVIUE = Base_write + "/m/apply/herald/add";
        //订单列表
        public static final String URL_PAY_LIST = Base_read + "/m/pay/listNew";
        //关闭直播，直播成绩单
        public static final String URL_LIVE_STOP = Base_write + "/m/video/live/stop";


        /**
         * 发布预告前的准备工作
         */
        public static final String URL_BEFORE_PUBLISH = Base_read + "/m/herald/before";
        //主播排行榜
        public static final String URL_ANCHOR_TOP = Base_read + "/m/user/top";

        /**
         * 充值列表
         */
        public static final String URL_RECHARGE_LIST = UserParams.Base_read
                + "/m/show/recharge/list";
        /**
         * 二维码登录OTT
         */
        public static final String URL_LOGIN_OTT = UserParams.Base_write
                + "/m/user/login/mobileByQrcodeToken";
        /**
         * 未读消息数量
         */
        public static final String URL_GET_UNREAND_COUNT = UserParams.Base_read
                + "/m/notice/getUnReadCount";


        public static final String EMAIL_PHONE = "emailOrMobile";
        public static final String Mobile = "mobile";
        public static final String PASSWORD = "password";
        public static final String CODE = "code";
        public static final String EMAIL = "email";
        public static final String USERNAME = "username";
        public static final String FACE_URL = "face_url";
        public static final String KEY = "key";
        public static final String VALUE = "value";

        public static final String openId = "openId";
        public static final String openName = "openName";
        public static final String faceUrl = "faceUrl";
        public static final String sex = "sex";
        public static final String location = "location";
        public static final String SOURCE = "source";
        public static final String ANDROID = "android";
        public static final String TERMINAL = "terminal";// 终端类型

        public static final String TYPE = "type";// 1:粉丝列表 2:关注人列表
        // 1:TV/OTT,2:PC,3:Mobile,4:pad,5:all

    }

    public static class XiuchangParams {
        /**
         * 秀场赠送的礼物列表
         */
        public static final String GIFT_LIST = UserParams.Base_read
                + "/m/show/gift/list";
        /**
         * 徽章列表
         */
        public static final String Badge_LIST = UserParams.Base_read
                + "/m/show/badge/list";
        /**
         * 秀场赠送的礼物数量列表
         */
        public static final String GIFT_COUNT_LIST = UserParams.Base_read
                + "/m/show/count/list?type=1";
        /**
         * 在秀场 我要赠送礼物的接口
         */
        public static final String GIFT_MY_SEND = UserParams.Base_write
                + "/m/show/gift/use";
        //        /**
        //         * 排行
        //         */
        //        public static final String ORDER = UserParams.Base_read
        //                + "/m/show/mb/list/show";
        /**
         * 排行
         */
        public static final String ORDER = UserParams.Base_read
                + "/m/show/chatroom/user/rank";
        /**
         * 主播贡献排行
         */
        public static final String SHOW_CHATROOM_RANK = UserParams.Base_read
                + "/m/show/chatroom/user/rank";
        /**
         * 购买礼物
         */
        public static final String BUY_GIFT = UserParams.Base_write
                + "/m/show/gift/buy";
        /**
         * 回复数据
         */
        public static final String XiuChang_HUIFU = UserParams.Base_write
                + "／m/rongcloud/userIsOnLine";
        /**
         * 回复历史
         */
        public static final String XiuChang_HistoryReply = UserParams.Base_read
                + "m/rongcloud/userHistoryReply";

    }

    public static class ResultConst {
        // 拍照图片返回
        public static final int RESULT_TAKE_PHOTO = 0X2;
        // 本地文件浏览
        public static final int RESULT_SHARE_LOCAL_PHOTO = 0X4;
        // 剪裁之后回调
        public static final int RESULT_SCALE_PIC = 0x3;

        public static final String USER_PHOTO_ICON = "hi_logo.jpg";

    }


    public static class AppFile {
        public static final String USER_INFO = "zhengzaitv";
    }
}
