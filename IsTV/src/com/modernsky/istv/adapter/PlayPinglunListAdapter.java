package com.modernsky.istv.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.modernsky.istv.R;
import com.modernsky.istv.bean.Content;
import com.modernsky.istv.bean.Huifu;
import com.modernsky.istv.bean.ResultBean;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.bean.VideoPinglun;
import com.modernsky.istv.fragment.PlayFragment;
import com.modernsky.istv.fragment.PlayFragment.HuifuCallback;
import com.modernsky.istv.fragment.PlayFragment.OnDianZanListener;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.DialogTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.utils.LogUtils;
import com.modernsky.istv.utils.Utils;
import com.modernsky.istv.view.NoScroListView;
import com.modernsky.istv.view.RoundAngleImageView;
import com.modernsky.istv.window.PeopleInfoDialog;

import java.util.ArrayList;
import java.util.List;

public class PlayPinglunListAdapter extends BaseAdapter implements OnScrollListener {
    private List<VideoPinglun> list;
    private LayoutInflater inflater;
    private BitmapUtils bitmapTool;
    private Activity mContext;
    private PlayFragment fragment;
    private boolean isScroll;
    private VideoPinglun videoPinglun = null;

    public PlayPinglunListAdapter(List<VideoPinglun> pingluns, Activity context, PlayFragment fragment) {
        super();
        this.list = pingluns;
        this.mContext = context;
        this.inflater = context.getLayoutInflater();
        bitmapTool = BitmapTool.getInstance().getAdapterUitl();
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dianbo_pinglun_lv, null);
            vh = new ViewHolder();
            vh.imageView = (RoundAngleImageView) convertView.findViewById(R.id.img_grid);
            vh.name = (TextView) convertView.findViewById(R.id.tv_video_name);
            vh.img_zan= (ImageView) convertView.findViewById(R.id.img_zan);
            vh.img_comment= (ImageView) convertView.findViewById(R.id.img_comment);
            vh.text = (TextView) convertView.findViewById(R.id.textView2);
            vh.rank = (TextView) convertView.findViewById(R.id.tv_lable);
            vh.time = (TextView) convertView.findViewById(R.id.time);
            vh.good = (TextView) convertView.findViewById(R.id.pinlun_good);
            vh.addMore = (TextView) convertView.findViewById(R.id.tv_add_more);
            vh.pinglun = (TextView) convertView.findViewById(R.id.pinlun_count);
            vh.rl_pinglun = (RelativeLayout) convertView.findViewById(R.id.rl_pinglun_info);
            vh.myPinglunListView = (NoScroListView) convertView.findViewById(R.id.gridview_paly_pinglun);
            vh.myPinglunListView.setVisibility(View.GONE);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        setViewHolder(vh, position);

        return convertView;
    }

    private void setViewHolder(final ViewHolder vh, final int position) {
        // initData
        final VideoPinglun videoPinglun = list.get(position);
        final UserEntity userEntity = videoPinglun.getUserEntity();
        final List<Huifu> huifus = videoPinglun.getComments() != null ? videoPinglun.getComments() : new ArrayList<Huifu>();
        //
        if (!isScroll && userEntity != null) {
            bitmapTool.display(vh.imageView, userEntity.getFaceUrl());
        }

        vh.imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity == null) {
                    return;
                }
                if (UserService.getInatance().isNeedLogin(mContext)) {
                    DialogTool.createToLoginDialog(mContext);
                    return;
                }
                if (!userEntity.getId().equals(UserService.getInatance().getUserBean(mContext).getId())) {
                    new PeopleInfoDialog(mContext, userEntity.getId()).show();
                }
            }
        });
        String userId = "";
        if (userEntity != null) {
            userId = userEntity.getId();
            vh.name.setText(userEntity.getUserName());
            if (userEntity.getRank() != null) {
                vh.rank.setText(userEntity.getRank().getRank());
            }
        }
        vh.time.setText(TimeTool.getTimeString(videoPinglun.getBuildTime(), mContext));

        vh.pinglunDetailAdapter = new PinglunDetailAdapter(huifus, userId, mContext);
        vh.good.setText("(" + videoPinglun.getPraiseCount() + ")");
        if (videoPinglun.getIsPraise() == 1) {
//            vh.good.setTextColor(Color.GRAY);
//            vh.img_zan.setImageResource(R.drawable.icon_07bofang_good);
        } else {
//            vh.img_zan.setImageResource(R.drawable.icon_06zhuanti_good);
//            vh.good.setTextColor(Color.WHITE);
        }
        vh.img_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoPinglun.getIsPraise() == 1) {
//                    vh.img_zan.setImageResource(R.drawable.icon_07bofang_good);
                    Utils.toast(mContext, "您已经点过了！");
                    return;
                }
                if (userEntity == null) {
                    return;
                }
                fragment.dianZan(videoPinglun.getId(), userEntity.getId(), new OnDianZanListener() {
                    @Override
                    public void onCompletion(String commentId, int count) {
                        if (commentId.equals(videoPinglun.getId())) {
                            if (count != -1) {
                                count = (int) (videoPinglun.getPraiseCount() + 1);
                                vh.good.setText("(" + count + ")");
                            } else {
                                count = (int) (videoPinglun.getPraiseCount());
                            }
//                            vh.good.setTextColor(Color.GRAY);
//                            vh.img_zan.setImageResource(R.drawable.icon_07bofang_good);
                            videoPinglun.setPraiseCount(count);
                            videoPinglun.setIsPraise(1);
                        }
                    }
                });
            }
        });
//        vh.good.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (videoPinglun.getIsPraise() == 1) {
//                    Utils.toast(mContext, "您已经点过了！");
//                    return;
//                }
//                if (userEntity == null) {
//                    return;
//                }
//                fragment.dianZan(videoPinglun.getId(), userEntity.getId(), new OnDianZanListener() {
//                    @Override
//                    public void onCompletion(String commentId, int count) {
//                        if (commentId.equals(videoPinglun.getId())) {
//                            count = (int) (videoPinglun.getPraiseCount() + 1);
//                            vh.good.setText("(" + count + ")");
//                            vh.good.setTextColor(Color.GRAY);
//                            videoPinglun.setPraiseCount(count);
//                            videoPinglun.setIsPraise(1);
//                        }
//                    }
//                });
//            }
//        });
        List<Content> contents = videoPinglun.getContent();
        if (contents != null) {
            for (int i = contents.size() - 1; i >= 0; i--) {
                Content map = contents.get(i);
                if (map.getType().equals("1")) {
                    vh.text.setText(map.getContent());
                }
            }
        }
        vh.myPinglunListView.setAdapter(vh.pinglunDetailAdapter);
        // TODO
        vh.myPinglunListView.setOnItemClickListener(new OnItemClickListener() {
            private UserEntity userEntity2;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.t("position, long id", position + "---" + id);
                Huifu huifu = huifus.get(position);
                if (huifu == null) {
                    return;
                }
                userEntity2 = huifu.getUserEntity();
                if (userEntity2 != null) {
                    fragment.reply(userEntity2.getId(), userEntity2.getUserName(), videoPinglun.getId(), new HuifuCallback() {
                        @Override
                        public void onHuifuComplet(ResultBean<List<Huifu>> huifuResult) {
                            notifyList(huifuResult, vh, userEntity2, huifus, videoPinglun);
                        }
                    });
                }
            }
        });
        //
        setPingLun(vh, userEntity, huifus, videoPinglun);
        // 点击子项展开列表
        vh.rl_pinglun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 有子评论收起列表
                if (vh.myPinglunListView.getVisibility() == View.VISIBLE) {
                    vh.myPinglunListView.setVisibility(View.GONE);
                    vh.addMore.setVisibility(View.GONE);
                    return;
                }
                // 有子评论展开列表
                vh.myPinglunListView.setVisibility(View.VISIBLE);
                // 无子评论发表评论
                fragment.sendHuifuList(videoPinglun.getId(), "0", new HuifuCallback() {
                    @Override
                    public void onHuifuComplet(ResultBean<List<Huifu>> huifuResult) {
                        vh.myPinglunListView.setVisibility(View.VISIBLE);
                        if (!videoPinglun.getId().equals(huifuResult.commentId)) {
                            return;
                        }
                        List<Huifu> comments = huifuResult.data;
                        if (userEntity == null) {
                            return;
                        }
                        if (comments == null || comments.size() == 0) {
                            fragment.reply(userEntity.getId(), userEntity.getUserName(), videoPinglun.getId(), this);
                            return;
                        }
                        notifyList(huifuResult, vh, userEntity, huifus, videoPinglun);
                        //
                        showAddMore(vh, huifus, videoPinglun);
                    }
                });

            }
        });
        //
        vh.addMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Huifu> tempHuifus = videoPinglun.getComments();
                if (tempHuifus == null || tempHuifus.size() < 1) {
                    return;
                }
                Huifu tempHuifu = tempHuifus.get(huifus.size() - 1);
                fragment.sendHuifuList(videoPinglun.getId(), String.valueOf(tempHuifu.getBuildTime()),//
                        new HuifuCallback() {// 评论回复结果回调
                            @Override
                            public void onHuifuComplet(ResultBean<List<Huifu>> huifuResult) {
                                vh.myPinglunListView.setVisibility(View.VISIBLE);
                                if (!videoPinglun.getId().equals(huifuResult.commentId)) {
                                    return;
                                }
                                vh.pinglun.setText("(" + huifuResult.commentCount + ")");
                                List<Huifu> comments = huifuResult.data;
                                if (comments != null && comments.size() > 0) {
                                    huifus.addAll(comments);
                                    videoPinglun.setComments(huifus);
                                    vh.pinglunDetailAdapter.notifyDataSetChanged();
                                }
                                showAddMore(vh, huifus, videoPinglun);
                            }
                        });
            }
        });
    }

    private static class ViewHolder {
        ImageView img_zan,img_comment;
        RoundAngleImageView imageView;
        TextView name, text, time, good, pinglun, addMore, rank;
        RelativeLayout rl_pinglun;
        NoScroListView myPinglunListView;
        PinglunDetailAdapter pinglunDetailAdapter;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_FLING:
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                isScroll = true;
                break;
            case OnScrollListener.SCROLL_STATE_IDLE:
                isScroll = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    private void showAddMore(ViewHolder vh, List<Huifu> huifus, VideoPinglun videoPinglun) {
        if (vh.myPinglunListView.getVisibility() == View.GONE) {
            vh.addMore.setVisibility(View.GONE);
        } else if (huifus.size() >= 10 && videoPinglun.getCommentCount() > huifus.size()) {
            vh.addMore.setVisibility(View.VISIBLE);
        } else {
            vh.addMore.setVisibility(View.GONE);
        }
    }

    private void setPingLun(final ViewHolder vh, final UserEntity userEntity, final List<Huifu> huifus, final VideoPinglun videoPinglun) {
        vh.pinglun.setText("(" + videoPinglun.getCommentCount() + ")");
        vh.pinglun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity == null) {
                    return;
                }
                fragment.reply(userEntity.getId(), userEntity.getUserName(), videoPinglun.getId(), new HuifuCallback() {
                    @Override
                    public void onHuifuComplet(ResultBean<List<Huifu>> huifuResult) {
                        notifyList(huifuResult, vh, userEntity, huifus, videoPinglun);
                    }
                });
            }
        });
    }

    private void notifyList(ResultBean<List<Huifu>> huifuResult,
                            final ViewHolder vh,//
                            final UserEntity userEntity,//
                            final List<Huifu> huifus,//
                            final VideoPinglun videoPinglun) {
        vh.myPinglunListView.setVisibility(View.VISIBLE);
        if (!videoPinglun.getId().equals(huifuResult.commentId)) {
            return;
        }
        List<Huifu> comments = huifuResult.data;
//        Huifu huifu = comments.get(comments.size() - 1);
        vh.pinglun.setText("(" + huifuResult.commentCount + ")");
        videoPinglun.setCommentCount(huifuResult.commentCount);
        videoPinglun.setComments(comments);
        huifus.clear();
        huifus.addAll(comments);
        vh.pinglunDetailAdapter = new PinglunDetailAdapter(huifus, userEntity.getId(), mContext);
        vh.myPinglunListView.setAdapter(vh.pinglunDetailAdapter);
        vh.pinglunDetailAdapter.notifyDataSetChanged();
    }
}