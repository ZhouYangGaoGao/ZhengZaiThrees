package com.modernsky.istv.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.modernsky.istv.R;
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

import java.io.File;

/**
 * 第三方 登录信息
 */
public class PopThreeShare extends PopupWindow implements OnClickListener {

    private Context context;
    private String tagUrl = "http://wap.zhengzai.tv/pages/videoshare.html?vbreId=";
    private String shareUrl = "http://wap.zhengzai.tv";
    private String tagLiveUrl = "http://wap.zhengzai.tv/pages/live.html?vbreId=";
    private String tagTitle = "正在现场";
    private String tagContent = "正在现场";
    private String toUrl;
    private String vu;
    private boolean isAnchorShare = false;//是否是主播分享
    //    private SinaShareTool shareTool;
    UMShareAPI mShareAPI;
    private String imgUrl;
    private int shareImgId = 0;

    private boolean isVideo;

    public PopThreeShare(Context context) {
        super(context);
        mShareAPI = UMShareAPI.get(context);
        this.context = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.pop_three_share, null);
        setContentView(rootView);
        showWindow(rootView);
        initView(rootView);
    }

    /**
     * @param tille   分享的标题
     * @param content 分享的内容
     * @param vbreId  分享的url
     */
    public void setShareInfo(String tille, String content, String vbreId) {
        this.tagTitle = tille;
        this.tagContent = content;
        this.tagUrl = tagUrl + vbreId;
    }

    public void setShareInfo(String tille, String content, String vbreId,
                             boolean isLive) {
        this.tagTitle = tille;
        this.tagContent = content;
        this.tagUrl = tagLiveUrl + vbreId;
    }

    public void setShareUrl(String tille, String content, String url) {
        this.tagTitle = tille;
        this.tagContent = content;
        this.tagUrl = url;
    }

    public void setShareUrl(String tille, String content, String imgUrl, String url) {
        this.tagTitle = tille;
        this.tagContent = content;
        this.imgUrl = imgUrl;
        this.tagUrl = url;
    }

    public void setShareUrlForAnchor(String tille, String content, String url, String imgUrl, String vu) {
        if (!TextUtils.isEmpty(tille)) {
            this.tagTitle = tille;
        }
        this.tagContent = content;
        this.toUrl = url;
        this.imgUrl = imgUrl;
        isAnchorShare = true;
        this.vu = vu;
    }

    public void setShareUrlForAnchor(String tille, String content, String url, int imgId) {
        if (!TextUtils.isEmpty(tille)) {
            this.tagTitle = tille;
        }
        this.tagContent = content;
        this.toUrl = url;
        this.shareImgId = imgId;
        isAnchorShare = true;
    }

    @SuppressWarnings("deprecation")
    private void initView(View rootView) {
        rootView.findViewById(R.id.weibo).setOnClickListener(this);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        rootView.findViewById(R.id.qq).setOnClickListener(this);
        rootView.findViewById(R.id.qzone).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.weibo:
                if (isAnchorShare) {
                    if (TextUtils.isEmpty(vu)) {
                        performAnchorShare(SHARE_MEDIA.SINA);
                    } else {
                        performAnchorSinaShare(SHARE_MEDIA.SINA);
                    }
                } else {
                    performShare(SHARE_MEDIA.SINA);
                }
                break;
            case R.id.wechat:
                if (isAnchorShare) {
                    performAnchorShare(SHARE_MEDIA.WEIXIN);
                } else {
                    performShare(SHARE_MEDIA.WEIXIN);
                }
                break;
            case R.id.wechat_circle:
                if (isAnchorShare) {
                    performAnchorShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                } else {
                    performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                }
                break;
            case R.id.qq:
                if (isAnchorShare) {
                    performAnchorShare(SHARE_MEDIA.QQ);
                } else {
                    performShare(SHARE_MEDIA.QQ);
                }
                break;
            case R.id.qzone:
                if (isAnchorShare) {
                    performAnchorShare(SHARE_MEDIA.QZONE);
                } else {
                    performShare(SHARE_MEDIA.QZONE);
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    /**
     * 主播个人主页分享& 预告详情页分享
     *
     * @param
     * @return
     */
    public void performAnchorShare(SHARE_MEDIA platform) {
        UMImage umimg;
        int imgId = R.drawable.logo_now;
        String content = tagContent;
        LogUtils.d("poptoUrl---" + toUrl);
        if (platform == SHARE_MEDIA.SINA) {
            content = "#正在现场# " + tagContent + " " + toUrl + " 下载正在现场App立即观看现场 @正在现场ModernSkyNow ";

            umimg = new UMImage(context, "http://img.zhengzai.tv/common/zzshare.jpg");
            toUrl = "";
        } else {
            if (!TextUtils.isEmpty(imgUrl)) {
                umimg = new UMImage(context, imgUrl);
            } else if (this.shareImgId != 0) {
                umimg = new UMImage(context, shareImgId);
            } else {
                umimg = new UMImage(context, imgId);
            }
        }
        String friendtitle;
        if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
            friendtitle = content;
        } else {
            friendtitle = tagTitle;
        }
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(tagTitle);
        dialog.setMessage(tagContent);
        Config.dialog = dialog;
        new ShareAction((Activity) context).setPlatform(platform)
                .withText(content)
                .withTitle(friendtitle)
                .withTargetUrl(toUrl)
                .withMedia(umimg)
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
     * 主播个人主页分享& 预告详情页分享
     *
     * @param
     * @return
     */
    public void performAnchorSinaShare(SHARE_MEDIA platform) {
        UMVideo umvideo;
//        int imgId = R.drawable.logo_now;
//        UMImage umimg = new UMImage(context, imgUrl);
        String content = "#正在现场# " + tagContent + " @正在现场ModernSkyNow ";
//        umvideo= new UMVideo("http://data.zhengzai.tv/info/video/sina/videoapi?url=http://wap.zhengzai.tv/pages/videoshare.html?vbreId=100_2e29fab84b");
//        umvideo= new UMVideo("http://yuntv.letv.com/bcloud.html?uu=53f80d6851&vu=2e29fab84b");
//        toUrl="http://data.zhengzai.tv/info/video/sina/videoapi?url=http://wap.zhengzai.tv/pages/videoshare.html?vbreId=100_2e29fab84b";
//        toUrl="";
        LogUtils.d("toUrl----" + toUrl);
        if (!TextUtils.isEmpty(vu)) {
            toUrl = "http://yuntv.letv.com/bcloud.html?uu=53f80d6851&vu=" + vu + "&width=760&pu=51373b1a30&height=380";
//            toUrl="http://yuntv.letv.com/bcloud.html?uu=53f80d6851&vu="++"f455aba007&width=760&pu=51373b1a30&height=380";
        }

        umvideo = new UMVideo(toUrl);
//        content = tagContent + " " + toUrl + " 下载正在现场App立即观看现场 @正在现场ModernSkyNow ";
        umvideo.setTitle(tagTitle);
        ProgressDialog dialog = new ProgressDialog(context);
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

    public void performShare(SHARE_MEDIA platform) {
        UMImage umimg;
        int imgId = R.drawable.logo_now;
        String content = tagContent;
        if (platform == SHARE_MEDIA.SINA) {
            content = "#正在现场# " + tagContent + " " + tagUrl + " 下载正在现场App立即观看现场 @正在现场ModernSkyNow ";
            umimg = new UMImage(context, "http://img.zhengzai.tv/common/zzshare.jpg");
            tagUrl = "";
        } else {
            if (TextUtils.isEmpty(imgUrl)) {
                umimg = new UMImage(context, imgId);
            } else {
                umimg = new UMImage(context, imgUrl);
            }
        }
        String friendtitle;

        if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
            friendtitle = content;
        } else {
            friendtitle = tagTitle;
        }
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(tagTitle);
        dialog.setMessage(tagContent);
        Config.dialog = dialog;
        new ShareAction((Activity) context).setPlatform(platform)
                .withText(content)
                .withMedia(umimg)
                .withTitle(friendtitle)
                .withTargetUrl(tagUrl)
                .setCallback(new UMShareListener() {

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        LogUtils.d("onResult: 分享成功啦");
                        Utils.toast((Activity) context, " 分享成功啦");
                        PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_SHARE_TIME, System
                                .currentTimeMillis());
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        LogUtils.d("onResult: 分享失败啦");
                        Utils.toast((Activity) context, " 分享失败啦");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        LogUtils.d("onResult: 分享取消啦");
                        Utils.toast((Activity) context, " 分享取消啦");
                    }
                }).share();
    }


    /**
     * 展示 位置
     */
    public void showBototomPop() {
        showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public void setSinaWeibo(int requestCode, int resultCode, Intent data) {
//        shareTool.getHandler().authorizeCallBack(requestCode, resultCode, data);
        if (mShareAPI != null)
            mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    //
    private void showWindow(final View view) {
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
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

    public void setShareImg(String title, String tagContent, String path, SHARE_MEDIA platform, final UMShareListener
            shareListener) {
        String content = tagContent;
        UMImage umimg = new UMImage(context, new File(path));
        if (platform == SHARE_MEDIA.SINA) {
            content = tagContent + shareUrl + " 下载正在现场App立即观看现场 @正在现场ModernSkyNow ";
            tagUrl = "";
            new ShareAction((Activity) context).setPlatform(platform)
                    .withMedia(umimg).withText(title).withText(content).withTargetUrl(tagUrl).setCallback(shareListener).share();
        } else
            new ShareAction((Activity) context).setPlatform(platform)
                    .withText(null)
                    .withTargetUrl(null)
                    .withTitle(tagContent)
                    .withMedia(umimg)
                    .setCallback(new UMShareListener() {
                        @Override
                        public void onResult(SHARE_MEDIA share_media) {
                            LogUtils.d("onResult: 分享成功啦");
                            PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_SHARE_TIME, System
                                    .currentTimeMillis());
                            shareListener.onResult(share_media);
                        }

                        @Override
                        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                            LogUtils.d("onResult: 分享失败啦");
                            shareListener.onError(share_media, throwable);
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media) {
                            LogUtils.d("onResult: 分享取消啦");
                            shareListener.onCancel(share_media);
                        }
                    }).share();
    }

    public void setShareImgUrl(String title, String tagContent, String imgUrl, SHARE_MEDIA platform) {
        String content = tagContent;
        UMImage umimg;
        if (platform == SHARE_MEDIA.SINA) {
            content = "我正在看【" + tagContent + "】 " + tagUrl + " 下载正在现场App立即观看现场 @正在现场ModernSkyNow ";
            umimg = new UMImage(context, "http://img.zhengzai.tv/common/zzshare.jpg");
            tagUrl = "";
        } else
            umimg = new UMImage(context, imgUrl);
        new ShareAction((Activity) context).setPlatform(platform)
                .withText(content)
                .withTargetUrl(shareUrl)
                .withTitle(title)
                .withMedia(umimg)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        LogUtils.d("onResult: 分享成功啦");
                        Utils.toast(context, " 分享成功啦");
                        PreferencesUtils.saveLongPreferences(context, PreferencesUtils.TYPE_SHARE_TIME, System
                                .currentTimeMillis());
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        LogUtils.d("onResult: 分享失败啦");
                        Utils.toast((Activity) context, " 分享失败啦");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        LogUtils.d("onResult: 分享取消啦");
                        Utils.toast((Activity) context, " 分享取消啦");
                    }
                }).share();
    }
}
