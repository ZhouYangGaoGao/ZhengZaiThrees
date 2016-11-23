package com.modernsky.istv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.modernsky.istv.R;
import com.modernsky.istv.utils.LogUtils;

//import com.modernsky.istv.fragment.ChatRoomShowFragment;

public class WidgetRadioSwitch extends RadioGroup implements OnClickListener {
    private View view = null;
    private RadioGroup radiogroup = null;
    public RadioButton gc1 = null;
    public RadioButton gc2 = null;
    public RadioButton gc3 = null;
    public RadioButton gc4 = null;
    public ImageView iv_back = null;
    private int index = 0;
    //
    private int[] backImage = {R.drawable.widget_switch_1_close,//
            R.drawable.widget_switch_2_close,//
            R.drawable.widget_switch_3_close//
    };

    public boolean islookforward() {
        return islookforward;
    }

    public void setIslookforward(boolean islookforward) {
        this.islookforward = islookforward;
    }

    private boolean islookforward;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gc1:
                LogUtils.d("gc1");
                index = 0;
                send();
                gc4.performClick();
                break;
            case R.id.gc2:
                LogUtils.d("gc2");
                index = 1;
                send();
                gc4.performClick();
                break;
            case R.id.gc3:
                LogUtils.d("gc3");
                index = 2;
                send();
                gc4.performClick();
                break;
            case R.id.gc4:
                LogUtils.d("gc4");
                radiogroup.setVisibility(View.GONE);
                iv_back.setVisibility(View.VISIBLE);
                if (islookforward) {
                    if (index==1) {
                        iv_back.setImageResource(R.drawable.widget_switch_1_close);
//                        iv_back.setImageResource(R.drawable.widget_switch_1_on);
                    }

                    if (index==0) {
                        iv_back.setImageResource(R.drawable.widget_switch_3_close);
//                        iv_back.setImageResource(R.drawable.widget_switch_3_on);
                    }

                } else {
                    iv_back.setImageResource(backImage[index]);
                    switch (index) {
                        case  0:
                            radiogroup.check(R.id.gc1);
                            break;
                        case  1:
                            radiogroup.check(R.id.gc2);
                            break;
                        case  2:
                            radiogroup.check(R.id.gc3);
                            break;
                        case  3:
                            radiogroup.check(R.id.gc4);
                            break;
                    }

                }
                break;
            default:
            case R.id.iv_back:
                iv_back.setVisibility(View.GONE);
                radiogroup.setVisibility(View.VISIBLE);
                if (islookforward) {
                    if (index == 0) {
                        gc1.setBackgroundResource(R.drawable.widget_switch_3_on);
                        gc2.setBackgroundResource(R.drawable.widget_switch_1_off);
                    }
                    if (index == 1) {
                        gc2.setBackgroundResource(R.drawable.widget_switch_1_on);
                        gc1.setBackgroundResource(R.drawable.widget_switch_3_off);
                    }
                }
//                if (islookforward) {
//                    gc3.setVisibility(View.GONE);
//                    gc1.setVisibility(View.VISIBLE);
//                    gc2.setVisibility(View.VISIBLE);
//                }
                break;
        }
    }

    // 1 声明的几个接口用来处理完成、跳过、时间等事件
    public interface SwitchListener {
         void invoke(int str);
    }

    // 2
    private SwitchListener instance = null;
    public Button btn_send;

    // 3
    public void send() {
        if (instance != null) {
            instance.invoke(index + 1);
        }
    }

    // 4 绑定事件的方法
    public void bindListener(SwitchListener instance) {
        this.instance = instance;
    }

    public WidgetRadioSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.widget_switch, this);
        radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        radiogroup.setVisibility(View.GONE);
        gc1 = (RadioButton) view.findViewById(R.id.gc1);
        gc1.setOnClickListener(this);
        gc2 = (RadioButton) view.findViewById(R.id.gc2);
        gc2.setOnClickListener(this);
        gc3 = (RadioButton) view.findViewById(R.id.gc3);
        gc3.setOnClickListener(this);
        gc4 = (RadioButton) view.findViewById(R.id.gc4);
        gc4.setOnClickListener(this);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(this);
    }

    public void setImage(int index) {
        this.index = index;
        gc4.performClick();
    }
}
