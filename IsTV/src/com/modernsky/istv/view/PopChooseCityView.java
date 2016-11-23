package com.modernsky.istv.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.DormMusicActivity;
import com.modernsky.istv.choiseCity.ChoiseCityActivity;

/**
 * @author : zxm
 * @describe : 选择城市
 * <p/>
 * 2016-3-10
 */
public class PopChooseCityView extends PopupWindow {
    private View mMenuView;
    private Activity mcontext;
    private String mCity;
//    private int sort;
    private ChooseCityListener mCityListener;
//    private String[] cityName = {"北京", "上海", "成都", "武汉", "西安", "重庆"};
    private String[] cityId = {"1", "2", "228", "120", "75", "4"};
    private View closeView;
    private RadioGroup vg_count, vg_time, vg;

    private TextView tv_choose;


    public void setmCityListener(ChooseCityListener mCityListener) {
        this.mCityListener = mCityListener;
    }

    public interface ChooseCityListener {
        void onCityChoose(String city);

        void onSortChoose(int sort);

    }

    public PopChooseCityView(Activity context) {
        super(context);
        this.mcontext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.dialog_choosecity, null);
        initView(mMenuView);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimTop);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xf0202020);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }


    public void clearFlag() {
        tv_choose.setText("");
        tv_choose.setVisibility(View.GONE);
        vg.clearCheck();
        vg_time.clearCheck();
        vg_count.clearCheck();
    }

    public void setmCityText(String cityText) {
        tv_choose.setText(cityText);
        tv_choose.setVisibility(View.VISIBLE);
    }


    private void initView(final View view) {
        closeView=view.findViewById(R.id.img_close);
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopChooseCityView.this.dismiss();
            }
        });
        tv_choose = (TextView) view.findViewById(R.id.tv_choose);
        tv_choose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_choose.setText("");
                tv_choose.setVisibility(View.GONE);
                vg.clearCheck();
                if (mCityListener != null)
                    mCityListener.onCityChoose("");
            }
        });
        view.findViewById(R.id.rb_all).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vg.clearCheck();
                dismiss();
                ((DormMusicActivity) mcontext).startActivityForResult(new Intent((DormMusicActivity) mcontext, ChoiseCityActivity.class), 100);
            }
        });

//        view.findViewById(R.id.imgbtn_up).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
        vg = (RadioGroup) view.findViewById(R.id.rg_choose);
        vg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_bj:
                    case R.id.rb_sh:
                    case R.id.rb_cd:
                    case R.id.rb_wh:
                    case R.id.rb_xa:
                    case R.id.rb_cq:
                        RadioButton rb = (RadioButton) view.findViewById(checkedId);
                        int i = group.indexOfChild(rb);
                        if (mCityListener != null && rb.isChecked()) {
                            mCity = rb.getText().toString();
                            tv_choose.setText(mCity);
                            tv_choose.setVisibility(View.VISIBLE);
                            mCityListener.onCityChoose(cityId[i]);
                            dismiss();
                        }
                        break;

                }

            }
        });
        vg_time = (RadioGroup) view.findViewById(R.id.rg_choose_time);
        final RadioButton rb_zj = (RadioButton) vg_time.findViewById(R.id.rb_zj);
        final RadioButton rb_zy = (RadioButton) vg_time.findViewById(R.id.rb_zy);

        BtnSelected btnListener1 = new BtnSelected("1");
        BtnSelected btnListener2 = new BtnSelected("2");
        rb_zj.setOnClickListener(btnListener1);
        rb_zy.setOnClickListener(btnListener2);


        vg_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int sort = 0;
                if (checkedId == R.id.rb_zj && rb_zj.isChecked()) {
                    sort = 1;
                    if (mCityListener != null) {
                        mCityListener.onSortChoose(sort);
                    }
                    dismiss();
                } else if (checkedId == R.id.rb_zy && rb_zy.isChecked()) {
                    sort = 2;
                    if (mCityListener != null) {
                        mCityListener.onSortChoose(sort);
                    }
                    dismiss();
                }
            }
        });
        vg_count = (RadioGroup) view.findViewById(R.id.rg_choose_count);
        final RadioButton rb_gao = (RadioButton) vg_count.findViewById(R.id.rb_gao);
        final RadioButton rb_di = (RadioButton) vg_count.findViewById(R.id.rb_di);
        BtnSelected btnListener3 = new BtnSelected("3");
        BtnSelected btnListener4 = new BtnSelected("4");
        rb_gao.setOnClickListener(btnListener3);
        rb_di.setOnClickListener(btnListener4);

        vg_count.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int sort = 0;
                if (checkedId == R.id.rb_gao && rb_gao.isChecked()) {
                    sort = 3;
                    if (mCityListener != null) {
                        mCityListener.onSortChoose(sort);
                    }
                    dismiss();
                } else if (checkedId == R.id.rb_di && rb_di.isChecked()) {
                    sort = 4;
                    if (mCityListener != null) {
                        mCityListener.onSortChoose(sort);
                    }
                    dismiss();
                }
            }
        });
    }


    class BtnSelected implements OnClickListener {

        public String btnId;

        public BtnSelected(String selctedId) {
            this.btnId = selctedId;
        }

        @Override
        public void onClick(View v) {
            if (btnId.equals("1") || btnId.equals("2")) {
                vg_count.clearCheck();
            } else if (btnId.equals("3") || btnId.equals("4")) {
                vg_time.clearCheck();
            }
        }
    }

    /**
     * 展示弹窗
     *
     * @param v
     */
    public void showPopLogin(View v) {
//        this.showAtLocation(v, Gravity.CLIP_HORIZONTAL | Gravity.TOP, 0, 200);
//        this.showAtLocation(v,  Gravity.TOP, 0, 200);
        this.showAsDropDown(v);
//        this.showAsDropDown(v,200,0);
    }
}
