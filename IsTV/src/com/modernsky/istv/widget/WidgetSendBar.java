package com.modernsky.istv.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.utils.Utils;

/**
 * 解决问题：底部评论栏样式和位置统一。
 *
 * @author 谢秋鹏
 */
public class WidgetSendBar extends LinearLayout {

    private InputMethodManager imm = null;
    public EditText et_input = null;

    //
    // 发送的目的，1添加评论，2回复评论
    // public static int COMMENT_ADD = 1;
    // public static int COMMENT_REPLY = 2;
    // private int sendFor = 0;

    // 1 声明的几个接口用来处理完成、跳过、时间等事件
    public interface SendBarListener {
         void invoke(String str);
    }

    // 2
    private SendBarListener instance = null;
    public Button btn_send;

    // 3
    public void send(String str) {
        if (instance != null) {
            instance.invoke(str);
        }
    }

    // 4 绑定事件的方法
    public void bindListener(SendBarListener instance) {
        this.instance = instance;
    }

    public WidgetSendBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_send_bar, this);
        initView(view);
    }

    public void replyComment(String toUserName) {
        et_input.setHint(toUserName);
        // et_input.requestFocus();
        et_input.setFocusableInTouchMode(true);
    }

    private void initView(View rootView) {
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 初始化控件
        et_input = (EditText) rootView.findViewById(R.id.et_input);
        et_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 得到焦点

                } else {
                    // 失去焦点
                    et_input.setText("");
                }
            }
        });
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEND) {
                    finishInput();
                    return true;
                }
                return false;
            }
        });
        btn_send = (Button) rootView.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishInput();
            }
        });
    }


    private void finishInput() {
        if (UserService.getInatance().isNeedLogin(getContext())) {
//			new AlertDialogLogin(getContext()).setMessage("您未登录\n是否现在登陆");
            DialogTool.createToLoginDialog(getContext());
            return;
        }
        imm.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
        String string = et_input.getText().toString().trim();
        if (TextUtils.isEmpty(string)) {
            Utils.toast(getContext(), "评论为空");
            return;
        }
        int lengh = (int) Utils.calculateWeiboLength(string) - 140;
        if (lengh > 0) {
            Utils.toast(getContext(), "评论长度最大为140个汉字,已经超出" + lengh + "个字,请删减后再试。");
            return;
        }
        send(string);
        et_input.setText("");
        et_input.setHint("");
    }

}
