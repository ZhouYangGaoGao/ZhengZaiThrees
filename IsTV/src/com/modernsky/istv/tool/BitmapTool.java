package com.modernsky.istv.tool;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;

/**
 * @author zhaoweiChuang
 * @2015年1月22日
 * @descripte 图片加载器
 */
public class BitmapTool {
    private BitmapUtils bitmapUtils;
    static BitmapTool TOOL;
//    private static BitmapGlobalConfig globalConfig;
    private BitmapTool() {
    }

    /**
     * 得到实例
     */
    public static BitmapTool getInstance() {
        return TOOL = TOOL == null ? new BitmapTool() : TOOL;
    }

    /**
     * @param context
     * @return 加载图片工具 对象
     */
    public BitmapUtils initAdapterUitl(Context context) {
        initBitmapUtils(context);
        return bitmapUtils;
    }

    /**
     * 展示网络网络图片
     *
     * @return
     */
    public BitmapUtils getAdapterUitl() {
        return bitmapUtils;
    }

    /**
     * 展示本地图拍
     *
     * @param view
     * @param value
     */
    public void showLocalView(ImageView view, String value) {
        view.setVisibility(View.VISIBLE);
        bitmapUtils.display(view, value);
    }

    /**
     * 初始化对象
     *
     * @param context
     */
    private void initBitmapUtils(Context context) {
        bitmapUtils = bitmapUtils == null ? new BitmapUtils(context,context.getExternalCacheDir().getAbsolutePath())
                : bitmapUtils;
//        globalConfig = globalConfig == null ? BitmapGlobalConfig.getInstance(
//                context, context.getExternalCacheDir().getAbsolutePath()) : globalConfig;
    }
}
