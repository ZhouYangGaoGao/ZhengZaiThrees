package com.modernsky.istv.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.CalendarActivity;
import com.modernsky.istv.acitivity.LoginActivity;
import com.modernsky.istv.adapter.CommonAdapter;
import com.modernsky.istv.adapter.ViewHolder;
import com.modernsky.istv.bean.FormWuTaiInfo;
import com.modernsky.istv.bean.RechargeBean;
import com.modernsky.istv.service.CalendarService;
import com.modernsky.istv.service.DianTaiService;
import com.modernsky.istv.utils.LocalCacheUtil;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.utils.WeakHandler;
import com.modernsky.istv.view.LodingDialog;

import java.io.File;
import java.util.List;

@SuppressLint("NewApi")
public class DialogTool {

    private static boolean hasDownLoadClicked;

    /**
     * @param
     * @param -1 默认不穿值
     * @return
     */
    public interface onGoumaiDialog {
        void onLandSpace();
    }

    public static Dialog createGoumai(final Activity context, int stringID, final boolean isLand, final onGoumaiDialog listenner) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_login, null);// 得到加载view
        TextView textView = (TextView) v.findViewById(R.id.pop_login_tip);
        if (stringID > 0) {
            textView.setText(stringID);
        }
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        v.findViewById(R.id.pop_login_sure).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ShareXmlTool xmlTool = new ShareXmlTool(context,
                        // UserConst.USER_DATA_LOCAL);
                        // String acount = xmlTool.getValue(UserConst.COUNT);
                        // if (!GeneralTool.isEmpty(acount)) {
                        listenner.onLandSpace();
//                        if (isLand) {
//                        } else {
//                            context.startActivity(new Intent(context,
//                                    BuyGiftActivity.class));
//                        }
                        // }else {
                        // context.startActivity(new
                        // Intent(context,VerifyLogin.class));
                        // }
                        loadingDialog.dismiss();
                    }
                });
        v.findViewById(R.id.pop_login_cancle).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.dismiss();
                    }
                });
        // loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));// 设置布局
        if (!context.isFinishing()) {
            // if(System.currentTimeMillis()-dialogShowTime>1000){
            // dialogShowTime=System.currentTimeMillis();
            loadingDialog.show();
            // }
        }
        return loadingDialog;
    }

    public static LodingDialog createLoadingDialog(Context context) {
        return createLoadingDialog(context, "");
    }

    public static LodingDialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        if (!TextUtils.isEmpty(msg))
            tipTextView.setText(msg);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.anim_loding);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        // tipTextView.setText(R.string.loading_down);// 设置加载信息
        LodingDialog loadingDialog = new LodingDialog(context,
                R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
        loadingDialog.setAnim(hyperspaceJumpAnimation, spaceshipImage);
        return loadingDialog;
    }

    /**
     * 跳转登录界面
     *
     * @param context
     * @return
     */
    public static Dialog createPayDialog(final Activity context,
                                         final String albumId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_show_goumai, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        // 稍后再说
        v.findViewById(R.id.pop_login_sure).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.dismiss();
                    }
                });
        // 现在购买
        v.findViewById(R.id.pop_login_cancle).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.startPay(albumId, context);
                        loadingDialog.dismiss();
                    }
                });
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));// 设置布局
        if ( !context.isFinishing()) {
            // if(System.currentTimeMillis()-dialogShowTime>1000){
            // dialogShowTime=System.currentTimeMillis();
            loadingDialog.show();
            // }
        }
        return loadingDialog;
    }

    public interface DialogLister {
        void onCancelListener();

        void onCountinue();
    }

    /**
     * 移动网络观看提示
     *
     * @param context
     * @return
     */
    public static Dialog createNetWorkDialog(final Activity context,
                                             final DialogLister listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_network_tishi, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        // 取消观看
        v.findViewById(R.id.pop_login_sure).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.dismiss();
                        listener.onCancelListener();
                    }
                });
        // 继续观看
        v.findViewById(R.id.pop_login_cancle).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.dismiss();
                        listener.onCountinue();
                    }
                });
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));// 设置布局
        if (!context.isFinishing()) {
            // if(System.currentTimeMillis()-dialogShowTime>1000){
            // dialogShowTime=System.currentTimeMillis();
            loadingDialog.show();
            // }
        }
        return loadingDialog;
    }
    /**
     * 跳转下载url;
     *
     * @param context
     * @return
     */

    public static Dialog createCheckDialog(final Activity context,
                                           final String url, String versioninfo, String version, final WeakHandler handler) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_version_update, null);// 得到加载view
        final ProgressBar mProgressBar = (ProgressBar) v
                .findViewById(R.id.mProgressBar);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        // 版本更新
        TextView tv_title = (TextView) v.findViewById(R.id.pop_login_tip);
        tv_title.setText("版本更新");
        // 版本内容
        TextView tv_version = (TextView) v.findViewById(R.id.versionInfo);
//        tv_version.setText("最新版本： 3.3.3 \n1.版本更新版本更新版本更新版本更新.\n2. 版本更新版本更新.\n" +
//                "3. 优化果蔬类商品展示，商品信息更清晰");
        tv_version.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_version.setText("最新版本："+version+"\n"+versioninfo
              );
        if (TextUtils.isEmpty(versioninfo)) {
            tv_version.setVisibility(View.GONE);
        }
        // 稍后再说
        final TextView btnLater = (TextView) v.findViewById(R.id.pop_login_sure);
        btnLater.setText("稍后再说");
        btnLater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.dismiss();
            }
        });
        // 现在更新
        mProgressBar.setVisibility(View.INVISIBLE);
        TextView btnKnowe = (TextView) v.findViewById(R.id.pop_login_cancle);
        btnKnowe.setText("现在更新");
        btnKnowe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasDownLoadClicked) {
                    loaderOttApk(url, context, mProgressBar, loadingDialog, handler);
                    hasDownLoadClicked=true;
                } else {
                    Utils.toast(context,"应用正在下载");
                }
            }
        });
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));// 设置布局
        if (!context.isFinishing()) {
            loadingDialog.show();
        }
        return loadingDialog;
    }

    private static void loaderOttApk(String url, final Activity context,
                                     final ProgressBar mProgressBar, final Dialog loadingDialog, final WeakHandler handler) {

        HttpUtils http = new HttpUtils();
        File gifSavePath1 = new File(LocalCacheUtil.cacheFilePath,
                "zhengzaitv.apk");
        if (gifSavePath1.exists()) {
            gifSavePath1.delete();
        }
        // 下载 apk
        http.download(url, gifSavePath1.getAbsolutePath(), false, false,
                new RequestCallBack<File>() {

                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        try {

                            LogUtils.t("onSuccess========",
                                    responseInfo.result.getAbsolutePath());
                            String sourceFilePath = responseInfo.result
                                    .getAbsolutePath();// 下载路径

                            context.startActivity(getApkFileIntent(sourceFilePath));

                            loadingDialog.dismiss();
                            mProgressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            LogUtils.d("Exception========" + e.getMessage());
                        }
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        int pro = (int) ((current * 100) / total);
                        Message message=new Message();
                        message.what=0;
                        message.arg1=pro;
                        handler.sendMessage(message);
                        mProgressBar.setProgress(pro);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e("onFailure========" + msg);
                        mProgressBar.setVisibility(View.GONE);
                        loadingDialog.dismiss();
                    }
                });
    }
    // Android获取一个用于打开APK文件的intent
    private static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }
//    /**
//     * 跳转下载url;
//     *
//     * @param context
//     * @return
//     */
//    public static Dialog createCheckDialog(final Activity context,
//                                           final String url) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View v = inflater.inflate(R.layout.dialog_version_update, null);// 得到加载view
//        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
//        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
//        // 稍后再说
//        v.findViewById(R.id.pop_login_sure).setOnClickListener(
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        loadingDialog.dismiss();
//                    }
//                });
//        // 现在购买
//        v.findViewById(R.id.pop_login_cancle).setOnClickListener(
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Uri uri = Uri.parse(url);
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        context.startActivity(intent);
//                        loadingDialog.dismiss();
//                    }
//                });
//        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
//        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
//                LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));// 设置布局
//        if (context != null && !context.isFinishing()) {
//            loadingDialog.show();
//        }
//        return loadingDialog;
//    }

    /**
     * @author zqg 用户反馈用到的接口 mainactivity用于访问接口时调用
     */
    public interface DialogReportLister {
        void onReport(String data);
    }

    /**
     * 用户反馈dialog
     *
     * @param context
     * @param listener DialogReportLister的接口
     * @return
     */
    public static Dialog createUserReportDialog(final Activity context, final DialogReportLister listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_user_report, null);// 得到加载view
        final TextView tv_num = (TextView) v.findViewById(R.id.tv_num_report);
        final EditText et_report = (EditText) v.findViewById(R.id.et_dia_user_report);
        Button btn_cancle = (Button) v.findViewById(R.id.btn_cancle_user_report);
        btn_cancle.setTextColor(context.getResources().getColor(R.color.white));
        final Button btn_report = (Button) v.findViewById(R.id.btn_report_user_report);
        et_report.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressWarnings("ResourceType")
            @Override
            public void afterTextChanged(Editable s) {
//                int num = (int) (140 - Utils.calculateWeiboLength(s.toString()));
                int num = (int) ( Utils.calculateWeiboLength(s.toString()));
                tv_num.setText(num + "/140");
                if (num>0&&num<=140) {
                        btn_report.setBackgroundResource(R.drawable.shape_oval_blue_report);
                        btn_report.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    btn_report.setBackgroundResource(R.drawable.shape_oval_black_report);
                    btn_report.setTextColor(context.getResources().getColor(R.color.white));
                }
            }
        });
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_user_report);// 加载布局
        // 取消
        btn_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.dismiss();
            }
        });
        // 反馈
        btn_report.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = et_report.getText().toString();
                if (data.length() > 1) {
                    int size = (int) (140 - Utils.calculateWeiboLength(data));
                    if (size >= 0 && size < 140) {
                        listener.onReport(data);
                    } else {
                        Utils.toast(context, "您提交的字数已经超出140个字符");
//                        Utils.toast(context, "您提交的字数已经超出140个字符，请减少" + -size
//                                + "个字符，再次发送");
                    }
                } else {
                    Utils.toast(context, "请输入内容");
                }
            }
        });
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));// 设置布局
        if ( !context.isFinishing()) {
            loadingDialog.show();
        }
        return loadingDialog;
    }

    /**
     * 评分dialog提示
     *
     * @param context
     * @return
     */
    public static Dialog createPingFenDialog(final Activity context,
                                             final DialogLister listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.pingfen_dialog, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v
                .findViewById(R.id.layout_pingfendialog);// 加载布局
        final Dialog pingfenDialog = new Dialog(context, R.style.pingfen_dialog);// 创建自定义样式dialog
        // 不，谢谢
        v.findViewById(R.id.btn_nothank).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pingfenDialog.dismiss();
                        listener.onCancelListener();
                    }
                });
        // 去评分
        v.findViewById(R.id.btn_pingfen).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pingfenDialog.dismiss();
                        listener.onCountinue();
                    }
                });
        pingfenDialog.setCancelable(true);// 不可以用“返回键”取消
        pingfenDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));// 设置布局
        if (!context.isFinishing()) {
            // if(System.currentTimeMillis()-dialogShowTime>1000){
            // dialogShowTime=System.currentTimeMillis();
            pingfenDialog.show();
            // }
        }
        return pingfenDialog;
    }

    /**
     * 引导dialog
     *
     * @param
     * @param
     * @return
     */
    // 用户反馈用到的接口 mainactivity用于访问接口时调用
    public interface DialogGuideListener {
        void onGuide(int index);
    }

    /**
     * @param context
     * @param index    1234567:
     * @param listener
     * @return
     */
    public static Dialog createGuideDialog(final Activity context,
                                           final int index, boolean fullScreen, final DialogGuideListener listener) {
        // initData
        String[] fileNames = {//
                "yindao0.png",//
                "yindao1.png",//
                "yindao2.png",//
                "yindao3.png", //
                "yindao4.png", //
                "yindao5.png", //
                "yindao6.png",//
                "yindao7.png",//
                "yindao8.png"//
        };
        // findView
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_guide, null);
        RelativeLayout layout = (RelativeLayout) v
                .findViewById(R.id.rl_dialog_guide);
        ImageView guideImg = (ImageView) v.findViewById(R.id.img_bg);
//        if (fullScreen)
//            v.findViewById(R.id.v_top).setVisibility(View.GONE);
//        else
//            v.findViewById(R.id.v_top).setVisibility(View.VISIBLE);
        // setText
        guideImg.setImageBitmap(Utils.getImageFromAssetsFile(context,
                fileNames[index]));


        // 创建自定义样式dialog
        final Dialog guideDialog;
        guideDialog = new Dialog(context, R.style.guide_dialog_fullscreen);
        guideDialog.setOwnerActivity((Activity) context);
        // 不可以用“返回键”取消
        guideDialog.setCancelable(true);
        guideDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                context.getWindow().getAttributes().height));
        guideImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                guideDialog.dismiss();
                listener.onGuide(index);
            }
        });
        //
        Window win = guideDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        if ( !context.isFinishing()) {
            guideDialog.show();
        }
        return guideDialog;
    }

    /**
     * 评分dialog提示
     *
     * @param context
     * @return
     */
    public static Dialog createAppointSuccessDialog(
            final CalendarActivity context, String str) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_appoint_success, null);// 得到加载view
        TextView text = (TextView) v.findViewById(R.id.tv_oppint_success);
        text.setText(str);
        LinearLayout layout = (LinearLayout) v
                .findViewById(R.id.dialog_oppint_success);// 加载布局

        final Dialog appointDialog = new Dialog(context, R.style.pingfen_dialog);// 创建自定义样式dialog
        // 确定
        v.findViewById(R.id.img_oppint_success).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appointDialog.dismiss();
                    }
                });
        appointDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // 下面是更新年月
                String yearMonth = "";
                yearMonth = yearMonth + CalendarService.getInstance().getYear();
                if (CalendarService.getInstance().getMonth().length() == 1) {
                    yearMonth = yearMonth + "0"
                            + CalendarService.getInstance().getMonth();
                } else {
                    yearMonth = yearMonth
                            + CalendarService.getInstance().getMonth();
                }
                context.getCalendarValData(yearMonth);

            }
        });

        appointDialog.setCancelable(true);// 不可以用“返回键”取消
        appointDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
        if ( !context.isFinishing()) {
            // if(System.currentTimeMillis()-dialogShowTime>1000){
            // dialogShowTime=System.currentTimeMillis();
            appointDialog.show();
            // }
        }
        return appointDialog;
    }

    public static void showNetDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setPositiveButton("确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                DianTaiService.getInstance()
                                        .setShouldPlayInYiDong(true);
                                Utils.sendBroadcastToService(0, context);
                            }
                        })
                .setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setMessage("正在使用移动网络，电台是否允许播放？").create().show();
    }

    /**
     * 登陆弹窗
     *
     * @param context
     * @return
     */
    public static Dialog createToLoginDialog(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_tologin, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        final Dialog toLogoinDialog = new Dialog(context,
                R.style.pingfen_dialog);// 创建自定义样式dialog
        // 确定
        v.findViewById(R.id.confirm_logain).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context,
                                LoginActivity.class));
                        toLogoinDialog.dismiss();
                    }
                });
        v.findViewById(R.id.cancle_logain).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toLogoinDialog.dismiss();
                    }
                });

        toLogoinDialog.setCancelable(true);// 不可以用“返回键”取消
        toLogoinDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
            toLogoinDialog.show();
        return toLogoinDialog;
    }

    /**
     * 删除作品弹窗
     *
     * @param context
     * @return
     */
    public static Dialog createToDeleteDialog(final Context context, final String addVideoId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_tologin, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        TextView title = (TextView) v.findViewById(R.id.text2_tologain);
        title.setText("您的作品数量已经超过上限,是否删除以前的作品保存此作品?");
        final Dialog toLogoinDialog = new Dialog(context,
                R.style.pingfen_dialog);// 创建自定义样式dialog
        TextView titleOK = (TextView) v.findViewById(R.id.confirm_logain);
        titleOK.setText("删除以前的作品");
        TextView titleNO = (TextView) v.findViewById(R.id.cancle_logain);
        titleNO.setText("删除本作品");
        // 确定
        titleOK.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.OpenUserInfo2(context, null, addVideoId, "3");
                        Utils.toast(context, "请选择您的作品左划删除,以保存新作品");
                        toLogoinDialog.dismiss();
                    }
                });
        titleNO.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toLogoinDialog.dismiss();
                    }
                });

        toLogoinDialog.setCancelable(true);// 不可以用“返回键”取消
        toLogoinDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
            toLogoinDialog.show();
        return toLogoinDialog;
    }


    public static Dialog createFormDialog(final Context context, List<FormWuTaiInfo> list, String title) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_form_perform, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v
                .findViewById(R.id.dialog_form);// 加载布局
        final Dialog formDialog = new Dialog(context, R.style.form_dialog);// 创建自定义样式dialog
        int count = list.size();
//        RelativeLayout layout1 = (RelativeLayout) v.findViewById(R.id.rl1_form);
        RelativeLayout layout2 = (RelativeLayout) v.findViewById(R.id.rl2_form);
        RelativeLayout layout3 = (RelativeLayout) v.findViewById(R.id.rl3_form);
        List<FormWuTaiInfo.FormWuTaiItemInfo> list1s = null;
        List<FormWuTaiInfo.FormWuTaiItemInfo> list2s = null;
        List<FormWuTaiInfo.FormWuTaiItemInfo> list3s = null;
        TextView textTitle = (TextView) v.findViewById(R.id.tile_dialog_form);
        textTitle.setText(title);
        ListView list1 = (ListView) v.findViewById(R.id.listview1_perform);
        ListView list2 = (ListView) v.findViewById(R.id.listview2_perform);
        ListView list3 = (ListView) v.findViewById(R.id.listview3_perform);
        TextView text1 = (TextView) v.findViewById(R.id.name1_form);
        TextView text2 = (TextView) v.findViewById(R.id.name2_form);
        TextView text3 = (TextView) v.findViewById(R.id.name3_form);
        ImageView img = (ImageView) v.findViewById(R.id.img_form);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                formDialog.dismiss();
            }
        });
        LogUtils.d("count===" + count);
        switch (count) {
            case 1:
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                list1s = list.get(0).getDetail();
                text1.setText(list.get(0).getStageName());

                break;
            case 2:
                list1s = list.get(0).getDetail();
                list2s = list.get(1).getDetail();
                layout3.setVisibility(View.GONE);
                text1.setText(list.get(0).getStageName());
                text2.setText(list.get(1).getStageName());

                break;
            case 3:
                list1s = list.get(0).getDetail();
                list2s = list.get(1).getDetail();
                list3s = list.get(2).getDetail();
                text1.setText(list.get(0).getStageName());
                text2.setText(list.get(1).getStageName());
                text3.setText(list.get(2).getStageName());
                break;
            default:
                break;
        }

        list1.setAdapter(new CommonAdapter<FormWuTaiInfo.FormWuTaiItemInfo>(context, list1s, R.layout.item_text) {
            @Override
            public void convert(ViewHolder helper, FormWuTaiInfo.FormWuTaiItemInfo item) {
                helper.setText(R.id.text1, item.getTime() + " " + item.getStarrName());
            }
        });
        list2.setAdapter(new CommonAdapter<FormWuTaiInfo.FormWuTaiItemInfo>(context, list2s, R.layout.item_text) {
            @Override
            public void convert(ViewHolder helper, FormWuTaiInfo.FormWuTaiItemInfo item) {
                helper.setText(R.id.text1, item.getTime() + " " + item.getStarrName());
            }
        });
        list3.setAdapter(new CommonAdapter<FormWuTaiInfo.FormWuTaiItemInfo>(context, list3s, R.layout.item_text) {
            @Override
            public void convert(ViewHolder helper, FormWuTaiInfo.FormWuTaiItemInfo item) {
                helper.setText(R.id.text1, item.getTime() + " " + item.getStarrName());
            }
        });

        formDialog.setCancelable(true);// 不可以用“返回键”取消
        formDialog.setContentView(layout, new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
        Window win = formDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
            formDialog.show();
        return formDialog;
    }

    /**
     * @author zqg  人物信息调用activity信息 关注 和 进入主页的操作
     */
    public interface OnPeopleInfoListenner {
        // true  关注    false 进入主页
        void onFocus(boolean isHasFocious);

    }

    /**
     * @author activity 调用dialog 关注 主要是关注成功 调用dialog
     */
    public interface OnActivitySuccessListenner {
        // true  关注    false 进入主页
        void Onsuccess(boolean attentionOrCancelAttention);

    }


    public interface OnGridViewItemClicked {
        void onGridViewClicked(int pozition);
    }

    public static Dialog createHorizonMB(final Context context, final OnGridViewItemClicked listenner, List<RechargeBean> datas) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_horizon_mb, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_root);// 加载布局
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        gridView.setAdapter(new CommonAdapter<RechargeBean>(context, datas, R.layout.item_grid_item_mb) {
            @Override
            public void convert(ViewHolder helper, RechargeBean item) {
                TextView tetMb = helper.getView(R.id.num_MB);
                TextView tetgivi = helper.getView(R.id.num_send);
                TextView tetRmb = helper.getView(R.id.tv_money);
                tetMb.setText(item.getMb());
                tetgivi.setText(item.getGiveMB());
                tetRmb.setText("¥ " + item.getMoney());
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listenner.onGridViewClicked(position);
            }
        });
        final Dialog buyMbDialog = new Dialog(context,
                R.style.form_dialog);// 创建自定义样式dialog

        buyMbDialog.setCancelable(true);// 不可以用“返回键”取消
        buyMbDialog.setContentView(layout, new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
        Window win = buyMbDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
            buyMbDialog.show();
        return buyMbDialog;
    }

    public interface OnChoisePayStyleListenner {
        void onChoisePayStyle(boolean isAliPay);
    }

    public static Dialog createBuyMBDialog(final Context context, final OnChoisePayStyleListenner listenner) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_choise_pay, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_root);// 加载布局
        v.findViewById(R.id.aliPayBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listenner.onChoisePayStyle(true);
            }
        });
        v.findViewById(R.id.weChatPayBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listenner.onChoisePayStyle(false);
            }
        });
        final Dialog buyMbDialog = new Dialog(context,
                R.style.form_dialog);// 创建自定义样式dialog

        buyMbDialog.setCancelable(true);// 不可以用“返回键”取消
        buyMbDialog.setContentView(layout, new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
        Window win = buyMbDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
            buyMbDialog.show();
        return buyMbDialog;
    }


}
