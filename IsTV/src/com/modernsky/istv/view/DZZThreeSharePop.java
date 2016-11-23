package com.modernsky.istv.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.modernsky.istv.R;
import com.modernsky.istv.utils.Constants;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.PreferencesUtils;
import com.modernsky.istv.utils.Utils;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;

/**
 * 第三方 登录信息
 */
public class DZZThreeSharePop extends PopupWindow implements OnClickListener {
    //	private UMSocialService mController = UMServiceFactory
//			.getUMSocialService("com.umeng.share");
    private Context context;
    private String shareUrl = "http://wap.zhengzai.tv";
//    private String tagBaseUrl = "http://wap.zhengzai.tv/pages/videoshare.html?vbreId=";
//    private String tagLiveUrl = "http://wap.zhengzai.tv/pages/live.html?vbreId=";
    private String tagTitle = "草莓音乐节";
    private String tagContent = "#正在现场# ";
    private String tagUrl="";
    private String picUrl;
    private String copyUrl;
    //    private SinaShareTool shareTool;
    UMShareAPI mShareAPI;
    private String vu;

    public DZZThreeSharePop(Context context) {
        super(context);
        mShareAPI = UMShareAPI.get(context);
        this.context = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_threesharepop, null);
        setContentView(rootView);
        showWindow(rootView);
        initView(rootView);
    }

    /**
     * @param tille   分享的标题
     * @param content 分享的内容
     * @param vbreId  分享的url
     */
    public void setShareInfo(String tille, String content, String picUrl, String vbreId,String vu) {
        this.tagTitle = tille;
        this.tagContent = content;
        this.picUrl = picUrl;
//        this.tagUrl = tagBaseUrl + vbreId;
        this.tagUrl = Constants.tagUrl + vbreId;
        copyUrl=tagUrl;
        this.vu=vu;
    }

    public void setShareInfo(String tille, String content, String picUrl, String vbreId,
                             boolean isLive) {
        this.tagTitle = tille;
        this.tagContent = content;
        this.picUrl = picUrl;
//        this.tagUrl = tagLiveUrl + vbreId;
        this.tagUrl = Constants.tagLiveUrl + vbreId;
        copyUrl=tagUrl;
    }

    public void setShareUrl(String tille, String content, String picUrl, String url) {
        this.tagTitle = tille;
        this.tagContent = content;
        this.tagUrl = url;
        this.picUrl = picUrl;
    }

    @SuppressWarnings("deprecation")
    private void initView(View rootView) {
        rootView.findViewById(R.id.img_copyurl_pop).setOnClickListener(this);
        rootView.findViewById(R.id.img_qq_pop).setOnClickListener(this);
        rootView.findViewById(R.id.img_weibo_pop).setOnClickListener(this);
        rootView.findViewById(R.id.img_pyq_pop).setOnClickListener(this);
        rootView.findViewById(R.id.img_wechat_pop).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_weibo_pop:
                if (TextUtils.isEmpty(vu)) {
                    performShare(SHARE_MEDIA.SINA);
                } else {
                    performAnchorSinaShare(SHARE_MEDIA.SINA);
                }
//                setShareImgUrl(tagTitle,tagContent,picUrl,SHARE_MEDIA.SINA);
                break;
            case R.id.img_wechat_pop:
                performShare(SHARE_MEDIA.WEIXIN);
//                setShareImgUrl(tagTitle, tagContent, picUrl, SHARE_MEDIA.WEIXIN);
                break;
            case R.id.img_pyq_pop:
                performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
//                setShareImgUrl(tagTitle, tagContent, picUrl, SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.img_qq_pop:
                performShare(SHARE_MEDIA.QQ);
//                setShareImgUrl(tagTitle, tagContent, picUrl, SHARE_MEDIA.QQ);
                break;
//            case R.id.qzone:
////			addQQPlatform();
//                performShare(SHARE_MEDIA.QZONE);
//                break;
            case R.id.img_copyurl_pop:

                copy(getContentView().getContext(), copyUrl);
//                performShare(SHARE_MEDIA.QZONE);
                break;
            default:
                break;
        }
        dismiss();
    }

    private void copy(Context context, String content) {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        Utils.toast(getContentView().getContext(), "复制链接成功");
    }

    private void performShare(SHARE_MEDIA platform) {
        UMImage umimg;
//        int imgId = R.drawable.logo_now;
        String content = tagContent;
        if (platform == SHARE_MEDIA.SINA) {
            content = tagContent + " "+ tagUrl + " 下载正在现场App立即观看现场 @正在现场ModernSkyNow ";
            LogUtils.d("dzztagUrl---"+tagUrl);
//            umimg = new UMImage(context, picUrl);
            umimg = new UMImage(context, "http://img.zhengzai.tv/common/zzshare.jpg");
            tagUrl = "";
        } else
            umimg = new UMImage(context, picUrl);
        LogUtils.d("tagUrl-----" + tagUrl);
        String  friendtitle;
        if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
            friendtitle = content;
        } else {
            friendtitle=tagTitle;
        }
        ProgressDialog dialog =  new ProgressDialog(context);
        dialog.setTitle(tagTitle);
        dialog.setMessage(tagContent);
        Config.dialog = dialog;
        new ShareAction((Activity) context).setPlatform(platform)
                .withText(content)
                .withTitle(friendtitle)
                .withTargetUrl(tagUrl)
                .withMedia(umimg)
                .setCallback(new UMShareListener() {

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Utils.toast((Activity) context, " 分享成功啦");
                        PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_SHARE_TIME, System.currentTimeMillis());
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Utils.toast((Activity) context, " 分享失败啦");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Utils.toast((Activity) context, " 分享取消啦");
                    }
                }).share();
    }

    /**
     * 主播个人主页分享& 预告详情页分享
     *
     * @param
     * @return
     */
    public void performAnchorSinaShare(SHARE_MEDIA platform) {
        UMVideo umvideo;
//        int imgId = R.drawable.logo_now;
//        UMImage umimg = new UMImage(context, imgUrl);
//        UMImage umimg = new UMImage(context, imgId);
        String content ="#正在现场# "+ tagContent+" @正在现场ModernSkyNow ";
//        umvideo= new UMVideo("http://data.zhengzai.tv/info/video/sina/videoapi?url=http://wap.zhengzai.tv/pages/videoshare.html?vbreId=100_2e29fab84b");
//        umvideo= new UMVideo("http://yuntv.letv.com/bcloud.html?uu=53f80d6851&vu=2e29fab84b");
//        toUrl="http://data.zhengzai.tv/info/video/sina/videoapi?url=http://wap.zhengzai.tv/pages/videoshare.html?vbreId=100_2e29fab84b";
//        toUrl="";
        if (!TextUtils.isEmpty(vu)) {
            tagUrl="http://yuntv.letv.com/bcloud.html?uu=53f80d6851&vu="+vu+"&width=760&pu=51373b1a30&height=380";
//            toUrl="http://yuntv.letv.com/bcloud.html?uu=53f80d6851&vu="++"f455aba007&width=760&pu=51373b1a30&height=380";
        }
        umvideo= new UMVideo(tagUrl);
//        content = tagContent + " " + toUrl + " 下载正在现场App立即观看现场 @正在现场ModernSkyNow ";
        LogUtils.d("content---"+content);
        LogUtils.d("tagTitle---"+tagTitle);
        LogUtils.d("tagContent---"+tagContent);
        LogUtils.d("tagUrl---"+tagUrl);
//        umvideo.setThumb(umimg);
        umvideo.setTitle(tagTitle);
        ProgressDialog dialog =  new ProgressDialog(context);
        dialog.setTitle(tagTitle);
        dialog.setMessage(tagContent);
        Config.dialog = dialog;
        new ShareAction((Activity) context).setPlatform(platform)
                .withText(content)
                .withTitle(tagTitle)
                .withTargetUrl("")
                .withMedia(umvideo)
                .setCallback(new UMShareListener() {

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Utils.toast((Activity) context, " 分享成功啦");
                        PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_SHARE_TIME, System
                                .currentTimeMillis());
                        LogUtils.d("ceshi", "分享成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Utils.toast((Activity) context, " 分享失败啦");
                        LogUtils.d("ceshi", "分享失败啦");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Utils.toast((Activity) context, " 分享取消啦");
                        LogUtils.d("ceshi", "分享取消啦");
                    }
                }).share();
    }
    /**
     * 展示 位置
     */
    public void showBototomPop() {
        showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public void showBelowView(View view) {
        this.showAsDropDown(view, 0, 5);
//        showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }


    // /**
    // * 展示 位置
    // */
    // public void showPop() {
    // showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0,
    // 0);
    //
    // }
    public void setSinaWeibo(int requestCode, int resultCode, Intent data) {
//        shareTool.getHandler().authorizeCallBack(requestCode, resultCode, data);
        if (mShareAPI != null)
            mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    //
    private void showWindow(final View view) {
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimRight);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.setOutsideTouchable(true);
        // 键盘监听
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            }
        });
    }
}

