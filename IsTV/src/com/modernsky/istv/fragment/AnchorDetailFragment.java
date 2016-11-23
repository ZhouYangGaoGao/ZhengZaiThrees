package com.modernsky.istv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.acitivity.LookForwardActivity;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.view.RoundAngleImageView;
import com.modernsky.istv.widget.WidgetRadioSwitch;
import com.modernsky.istv.window.PeopleInfoDialog;

/**
 * Created by zqg on 2016/3/10.
 */
public class AnchorDetailFragment extends BaseFragment {
    TextView nameText, timeText, locationText, rankText, meiliText, powerText, zanText, introDuceText, levelText;
    ImageView sexImg;
    RoundAngleImageView picImg;
    WidgetRadioSwitch wrs;
    private boolean isLive;
    private UserEntity userEntity;

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.isLive=getArguments().getBoolean("isLive");
        return inflater.inflate(R.layout.fragment_anchor_detail, container, false);

    }

//    public AnchorDetailFragment() {
//        super();
//    }
//
//    public AnchorDetailFragment(boolean isLive) {
//        this.isLive = isLive;
//    }

    @Override
    public void initView(View rootView) {
        nameText = (TextView) rootView.findViewById(R.id.tv_name_band);
        timeText = (TextView) rootView.findViewById(R.id.tv_time);
        locationText = (TextView) rootView.findViewById(R.id.tv_location);
        rankText = (TextView) rootView.findViewById(R.id.tv_num_rank);
        meiliText = (TextView) rootView.findViewById(R.id.tv_num_meili);
        powerText = (TextView) rootView.findViewById(R.id.tv_num_Power);
        zanText = (TextView) rootView.findViewById(R.id.tv_num_prize);
        introDuceText = (TextView) rootView.findViewById(R.id.tv_introduce);
        levelText = (TextView) rootView.findViewById(R.id.tv_lv_anchor);
        sexImg = (ImageView) rootView.findViewById(R.id.img_sex);
        picImg = (RoundAngleImageView) rootView.findViewById(R.id.img_anchor_pic);
        picImg.setOnClickListener(this);
        wrs = (WidgetRadioSwitch) rootView.findViewById(R.id.wrs);
        wrs.bindListener(new WidgetRadioSwitch.SwitchListener() {
            @Override
            public void invoke(int str) {
                if (isLive) {
                    android.os.Message message = new android.os.Message();
                    message.what = str;
                    ((ChatRoomShowFragment) getParentFragment()).getHandler()
                            .sendMessage(message);
                } else {
                        wrs.gc3.setVisibility(View.GONE);
                        wrs.gc1.setBackgroundResource(R.drawable.tabbar_user_selector);
                        wrs.gc2.setBackgroundResource(R.drawable.tabbar_find_selector);
                        ( (LookForwardActivity) getActivity()).setViewpager(str-1);
                }
            }
        });
        if (!isLive) {
            wrs.setVisibility(View.VISIBLE);
            wrs.gc3.setVisibility(View.GONE);
            wrs.gc1.setBackgroundResource(R.drawable.tabbar_user_selector);
            wrs.gc2.setBackgroundResource(R.drawable.tabbar_find_selector);
            wrs.setIslookforward(true);
            wrs.setImage(0);
        } else {
            wrs.setVisibility(View.VISIBLE);
            ChatRoomShowFragment fragment = ((ChatRoomShowFragment) getParentFragment());
            try {
                updateVideoInfo(fragment.getTime(), fragment.getLocation(), fragment.getDescreption());
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUserEnty(fragment.getSingerEntity());
        }
    }

    public void updateVideoInfo(String time, String location, String descreption) {
        timeText.setText("时间: " + TimeTool.getTime(Long.parseLong(time)));
        locationText.setText("地点: " + location);
        introDuceText.setText(descreption);
    }


    public void updateUserEnty(UserEntity userEntity) {
        this.userEntity = userEntity;
        if (userEntity == null)
            return;
//        if (isLive) {
//            timeText.setText("时间: " + TimeTool.getTime(userEntity.getHerald().getStartTime()));
//            locationText.setText("地点: " + userEntity.getHerald().getLocation());
//            introDuceText.setText(userEntity.getHerald().getLiveProfile());
//        }
        BitmapTool.getInstance().getAdapterUitl().display(picImg, userEntity.getFaceUrl());
        if (userEntity.getRank() != null) {
            levelText.setText(String.valueOf(userEntity.getRank().getRank()));
        }
        nameText.setText(userEntity.getUserName());
        rankText.setText(String.valueOf(userEntity.getRanking()));
        meiliText.setText(String.valueOf(userEntity.getExper()));
        powerText.setText(String.valueOf(userEntity.getStrawCount()));
        zanText.setText(String.valueOf(userEntity.getPraiseCount()));
        if (userEntity.getSex() == 1) {
            sexImg.setImageResource(R.drawable.icon_man);
        } else
            sexImg.setImageResource(R.drawable.icon_woman);

    }

    PeopleInfoDialog anchorDialog;

    private void initAnchorDialog(String userID) {
        if (anchorDialog == null) {
            anchorDialog = new PeopleInfoDialog(getActivity(), userID);
        }
        anchorDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_anchor_pic:
                if (UserService.getInatance().isNeedLogin(this.getActivity())) {
                    DialogTool.createToLoginDialog(this.getActivity());
                } else {
                    if (this.userEntity != null) {
                        if (!UserService.getInatance().getUserBean(this.getActivity()).getId().equals(userEntity.getId())) {
                            initAnchorDialog(userEntity.getId());
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isLive) {
            if (isVisibleToUser && wrs != null) {
                wrs.setImage(2);
            }
        } else {
            if (isVisibleToUser && wrs != null) {
                wrs.setImage(0);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


}
