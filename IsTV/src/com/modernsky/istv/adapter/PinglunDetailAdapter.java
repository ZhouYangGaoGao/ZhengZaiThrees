package com.modernsky.istv.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.modernsky.istv.R;
import com.modernsky.istv.bean.Content;
import com.modernsky.istv.bean.Huifu;
import com.modernsky.istv.bean.UserEntity;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.TimeTool;
import com.modernsky.istv.window.PeopleInfoDialog;

import java.util.List;

public class PinglunDetailAdapter extends BaseAdapter {
    private List<Huifu> huifus;
    private BitmapUtils bitTools;
    private Context context;
    private LayoutInflater inflater;
    private String toUserId;
    private ForegroundColorSpan blueSpan;

    public PinglunDetailAdapter(List<Huifu> huifus, String toUserId, Context context) {
        super();
        this.huifus = huifus;
        this.toUserId = toUserId;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.bitTools = BitmapTool.getInstance().getAdapterUitl();
        blueSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.write));
    }

    @Override
    public int getCount() {
        if (huifus == null) {
            return 0;
        }
        return huifus.size();
    }

    @Override
    public Object getItem(int position) {
        if (huifus == null) {
            return null;
        }
        return huifus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_pinglun_detail, null);
            vh = new ViewHolder();
            vh.img = (ImageView) convertView.findViewById(R.id.img_grid);
            vh.name = (TextView) convertView.findViewById(R.id.tv_video_name);
            vh.rank = (TextView) convertView.findViewById(R.id.tv_lable);
            vh.time = (TextView) convertView.findViewById(R.id.item_pinglun_detial_time);
            vh.text = (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        //


        Huifu pinglun = (Huifu) getItem(position);
        List<Content> contents = pinglun.getContent();
        vh.time.setText(TimeTool.getTimeString(pinglun.getBuildTime(), context));
        final UserEntity userEntity = pinglun.getUserEntity();
        UserEntity toUserEntity = pinglun.getToUserEntity();
        boolean showHuifu = false;
        if (userEntity != null) {
            vh.name.setText(userEntity.getUserName());
            bitTools.display(vh.img, userEntity.getFaceUrl());
            if (userEntity.getRank() != null) {
                vh.rank.setText(userEntity.getRank().getRank());
            }
            if (toUserEntity != null) {
                showHuifu = true;
            }
            vh.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!userEntity.getId().equals(UserService.getInatance().getUserBean(context).getId())) {
                        new PeopleInfoDialog(context, userEntity.getId()).show();
                    }
                }
            });
        }
        if (contents != null && contents.size() > 0) {
            Content mContent = contents.get(0);
            if (showHuifu) {
                String content = "回复 " + toUserEntity.getUserName() + " " + mContent.getContent();
                Spannable word = new SpannableString(content);
                word.setSpan(blueSpan, 3,
                        3 + toUserEntity.getUserName().length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                vh.text.setText(word);
            } else {
                vh.text.setText(mContent.getContent());
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView time;
        TextView text;
        TextView rank;
        ImageView img;
    }
}
