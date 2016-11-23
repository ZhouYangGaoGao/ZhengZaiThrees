package com.modernsky.istv.window;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.PlayActivity;
import com.modernsky.istv.bean.Content;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.Constants.ResultConst;
import com.modernsky.istv.utils.GeneralTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.MediaUtil;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.PopPictureView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-4-14 上午11:22:49
 * @类说明：
 */
public class PinglunDialog extends Dialog implements
        android.content.DialogInterface.OnCancelListener,
        android.content.DialogInterface.OnShowListener {

    public interface OnPinglunCompleteLisenter {
        void onComplete(List<Content> content);
    }

    private List<String> datas = new ArrayList<String>();
    private List<String> resouses = new ArrayList<String>();
    PopPictureView popView;
    private OnPinglunCompleteLisenter completeLisenter;
    private InputMethodManager mInputMethodManager;
    private Activity mContext;
    private EditText mEditText;
    private String toUserName;
    private ImageView imgOne, imgTwo, imgThree;
    private View pictures;
    private boolean isShowPic;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pinglun);
        initView();
    }

    /**
     * @param context
     */
    public PinglunDialog(Activity context, boolean isShowPic) {
        this(context, 0, null, isShowPic);
    }

    public PinglunDialog(Activity context, int theme,
                         OnPinglunCompleteLisenter completeLisenter, boolean isShowPic) {
        this(context, theme, null, completeLisenter, isShowPic);
    }

    public PinglunDialog(Activity context, int theme, String toUserName,
                         OnPinglunCompleteLisenter completeLisenter, boolean isShowPic) {
        super(context, theme);
        this.mContext = context;
        this.completeLisenter = completeLisenter;
        this.toUserName = toUserName;
        this.isShowPic = isShowPic;
    }

    private void initView() {
        mInputMethodManager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText = (EditText) findViewById(R.id.ed_pinglun);
        if (!TextUtils.isEmpty(toUserName)) {
            mEditText.setHint("回复 " + toUserName);
        }

        if (isShowPic) {
            pictures = findViewById(R.id.pinglun_pictures);
            pictures.setVisibility(View.VISIBLE);
            imgOne = (ImageView) findViewById(R.id.img_one);
            imgTwo = (ImageView) findViewById(R.id.img_two);
            imgThree = (ImageView) findViewById(R.id.img_three);
        }
        findViewById(R.id.tv_complete).setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string = mEditText.getText().toString().trim();
                        if (TextUtils.isEmpty(string)) {
                            Utils.toast(mContext, "评论为空");
                            return;
                        }
                        int lengh = (int) Utils.calculateWeiboLength(string) - 140;
                        if (lengh > 0) {
                            Utils.toast(mContext, "评论长度最大为140个汉字,已经超出" + lengh
                                    + "个字,请删减后再试。");
                            return;
                        }
                        // 上传文字
                        if (completeLisenter != null) {
                            List<Content> contents = new ArrayList<Content>();
                            Content content = new Content();
                            content.setType("1");
                            content.setContent(string);
                            contents.add(0, content);
                            if (resouses != null && resouses.size() > 0) {
                                for (String dataString : resouses) {
                                    contents.add(new Content("2", dataString));
                                }
                            }

                            if ( contents.size() > 0) {
                                if (TextUtils.isEmpty(string)
                                        && contents.size() <= 1) {
                                    dismiss();
                                } else
                                    completeLisenter.onComplete(contents);
                            }
                        }
                        dismiss();
                    }
                });
        findViewById(R.id.img_cancel).setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
        findViewById(R.id.img_add_pic).setOnClickListener(
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (datas.size() >= 3) {
                            Toast.makeText(mContext, "最多上传三个图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (popView == null) {
                            popView = new PopPictureView(mContext, listener);
                        }
                        if (mInputMethodManager != null)
                            mInputMethodManager.hideSoftInputFromWindow(
                                    mEditText.getWindowToken(), 0);
                        popView.showAtLocation(v, 0, 0, 0);
                    }
                });
        setOnCancelListener(this);
        setCanceledOnTouchOutside(true);
        setOnShowListener(this);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        mInputMethodManager.showSoftInput(mEditText,
                InputMethodManager.SHOW_IMPLICIT);
        mEditText.requestFocus();
    }

    android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_pick_photo:

                    GeneralTool.showFileChooser(mContext,
                            ResultConst.RESULT_SHARE_LOCAL_PHOTO);
                    popView.dismiss();
                    break;
                case R.id.btn_pick_take:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 处理拍照后的图片保存路径
                    File tempFile = new File(MediaUtil.createPictureFile());
                    ((PlayActivity) mContext).setTempFile(tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    mContext.startActivityForResult(intent,
                            ResultConst.RESULT_TAKE_PHOTO);
                    popView.dismiss();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onShow(DialogInterface dialog) {
        LogUtils.t("onShow--", "onShow");
        mEditText.requestFocus();
        mInputMethodManager.showSoftInput(mEditText,
                InputMethodManager.SHOW_IMPLICIT);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.content.DialogInterface.OnCancelListener#onCancel(android.content
     * .DialogInterface)
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    public void finish() {
        if (mInputMethodManager != null)
            mInputMethodManager.hideSoftInputFromWindow(
                    mEditText.getWindowToken(), 0);
    }

    @Override
    public void dismiss() {
        if (mInputMethodManager != null)
            mInputMethodManager.hideSoftInputFromWindow(
                    mEditText.getWindowToken(), 0);
        super.dismiss();
        datas.clear();
        resouses.clear();
    }

    /**
     *
     */
    public void cleanText() {
        // TODO Auto-generated method stub
        if (mEditText != null) {
            mEditText.setText("");
        }
        if (datas != null) {
            datas.clear();
        }
        if (resouses != null) {
            resouses.clear();
        }
        if (imgOne != null) {
            imgOne.setVisibility(View.GONE);
            imgOne.setImageResource(R.drawable.icon_02buytickets_5address_jia);
        }

        if (imgTwo != null) {
            imgTwo.setVisibility(View.GONE);
            imgTwo.setImageResource(R.drawable.icon_02buytickets_5address_jia);
        }
        if (imgThree != null) {
            imgThree.setVisibility(View.GONE);
            imgThree.setImageResource(R.drawable.icon_02buytickets_5address_jia);
        }
    }

    public void setPicture(String value, String string) {
        datas.add(value);
        resouses.add(string);
        for (int i = 0; i < datas.size(); i++) {
            switch (i) {
                case 0:
                    BitmapTool.getInstance().showLocalView(imgOne, datas.get(i));
                    break;
                case 1:
                    BitmapTool.getInstance().showLocalView(imgTwo, datas.get(i));
                    break;
                case 2:
                    BitmapTool.getInstance().showLocalView(imgThree, datas.get(i));
                    break;
                default:
                    break;
            }
        }
        // BitmapTool.getInstance().getAdapterUitl().display(container, uri);
    }

//    private void updatePic() {
//
//    }
//
//    /**
//     * 更换选中的新头像
//     */
//    private void updateUserIcon(String value) {
//        GeneralTool.uploadFile(value, new SaveCallback() {
//            @Override
//            public void onProgress(String arg0, int arg1, int arg2) {
//            }
//
//            @Override
//            public void onFailure(String arg0, OSSException arg1) {
//                Utils.toast(mContext, "上传失败");
//            }
//
//            // 上传成功
//            @Override
//            public void onSuccess(String arg0) {
//
//            }
//        });
//    }

}
