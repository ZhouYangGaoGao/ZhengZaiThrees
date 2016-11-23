package com.modernsky.istv.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.modernsky.istv.R;

/**
 * @author : Administrator
 * @describe : 照片 选择弹出框
 * <p/>
 * 2013-10-23
 */
public class PopPictureView extends PopupWindow {
    private Button btn_cancel;
    private View mMenuView;
    private Activity activity;

    public PopPictureView(Activity context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = context;
        mMenuView = inflater.inflate(R.layout.pop_share_picture, null);
        mMenuView.findViewById(R.id.btn_pick_take).setOnClickListener(
                itemsOnClick);
        mMenuView.findViewById(R.id.btn_pick_photo).setOnClickListener(
                itemsOnClick);
        btn_cancel = (Button) mMenuView.findViewById(R.id.btn_pick_cancel);
        // 取消按钮
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    /**
     * 展示 位置
     */
    public void showBototomPop() {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0,
                0);

    }
}
