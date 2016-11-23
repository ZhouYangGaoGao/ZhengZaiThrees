package com.modernsky.istv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.bean.YouHuiQuan;
import com.modernsky.istv.tool.TimeTool;

import java.util.List;

public class YouHuiQuanListAdapter extends BaseAdapter {
    private List<YouHuiQuan> list;
    private LayoutInflater inflater;

    public YouHuiQuanListAdapter(List<YouHuiQuan> list, Context context) {
        super();
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_youhuiquan,
                    null);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.tv_video_name);
            vh.youhuima = (TextView) convertView.findViewById(R.id.tv_youhuima);
            vh.isUse = (TextView) convertView.findViewById(R.id.tv_shiyong);
            vh.endTime = (TextView) convertView.findViewById(R.id.textView2);
            vh.youhuiMoney = (TextView) convertView
                    .findViewById(R.id.textView4);
            vh.moeny = (TextView) convertView
                    .findViewById(R.id.tv_youhui_moeny);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        YouHuiQuan ticket = list.get(position);
        if (ticket != null) {
            String name = ticket.getTitle();

            vh.name.setText(name);
            vh.youhuima.setText("优惠码：" + ticket.getCode());
            if (ticket.getIsUse() == 0) {
                vh.isUse.setText("未使用");
            } else
                vh.isUse.setText("已使用");
            vh.endTime.setText("使用有效期："
                    + TimeTool.getDayTime(ticket.getEndTime()));
            if (ticket.getType() == 3) {
                vh.youhuiMoney.setVisibility(View.VISIBLE);
                vh.moeny.setVisibility(View.VISIBLE);
                vh.moeny.setText(ticket.getMoney() + "元");
            } else {
                vh.youhuiMoney.setVisibility(View.GONE);
                vh.moeny.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView name, youhuima, isUse, endTime, moeny, youhuiMoney;
    }

}
