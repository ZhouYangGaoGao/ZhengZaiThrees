package com.modernsky.istv.window;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.utils.Utils;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-8-25 上午10:46:15
 * @类说明：播放设置弹窗
 */
public class TanmuDialog extends Dialog implements
        android.content.DialogInterface.OnCancelListener,
        android.content.DialogInterface.OnShowListener {
    private Context mActivity;
    private TextView tv_size;
    private EditText ed_msg;

    private String edtStr;
    private InputMethodManager mInputMethodManager;
    private OnSendTanmuListener mTanmuListener;

    public interface OnSendTanmuListener {
         void sendMessage(String edtStr);
    }

    /**
     * @param context
     */
    public TanmuDialog(Context context, OnSendTanmuListener onSendTanmuListener) {
        super(context, R.style.DialogStyleBottom);
        this.mActivity = context;
        this.mTanmuListener = onSendTanmuListener;
    }

    public void setHintMethod(String hint) {
        ed_msg.setHint(hint);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        // 定义全屏参数
        // int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        // // 设置当前窗体为全屏显示
        // getWindow().setFlags(flag, flag);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.dialog_tanmu);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        initView();
    }

    /**
     *
     */
    @SuppressLint("InlinedApi")
    private void initView() {
        mInputMethodManager = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.rl_tanmu).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
        ed_msg = (EditText) findViewById(R.id.ed_sendmsg);
        tv_size = (TextView) findViewById(R.id.img_show_tanmu);
        ed_msg.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    sendTanmu();
                }
                return false;
            }

        });
        ed_msg.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtStr = ed_msg.getText().toString();
                int size = (int) (50 - Utils.calculateWeiboLength(edtStr));
                tv_size.setText(String.valueOf(size ));
            }
        });
        Button img_send = (Button) findViewById(R.id.img_send);
        img_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendTanmu();
            }

        });
        setOnCancelListener(this);
        setCanceledOnTouchOutside(true);
        setOnShowListener(this);
        ed_msg.requestFocus();
        ed_msg.postDelayed(new Runnable() {

            @Override
            public void run() {

                mInputMethodManager.showSoftInput(ed_msg,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        }, 50);
    }

    private void sendTanmu() {
        if (TextUtils.isEmpty(edtStr)) {
            Utils.toast(mActivity, "发送的内容不能为空哦");
            return;
        }
        int size = (int) (50 - Utils.calculateWeiboLength(edtStr));
        if (size >= 0) {
            if (mTanmuListener != null)
                mTanmuListener.sendMessage(edtStr);
            edtStr = "";
            ed_msg.setText(edtStr);
            dismiss();
        } else {
            Utils.toast(mActivity, "您发送的弹幕已经超出50个字符，请减少" + -size + "个字符，再次发送");
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        ed_msg.requestFocus();
        ed_msg.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputMethodManager.showSoftInput(ed_msg,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
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
                    ed_msg.getWindowToken(), 0);
    }

    @Override
    public void dismiss() {
        if (mInputMethodManager != null)
            mInputMethodManager.hideSoftInputFromWindow(
                    ed_msg.getWindowToken(), 0);

        super.dismiss();
    }
}