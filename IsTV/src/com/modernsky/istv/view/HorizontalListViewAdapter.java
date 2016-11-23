package com.modernsky.istv.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.modernsky.istv.R;

/**
 * Created by zqg on 2016/2/20.
 */
public class HorizontalListViewAdapter extends BaseAdapter {

    public HorizontalListViewAdapter(Context con) {
        mInflater = LayoutInflater.from(con);
    }

    int lengh;

    @Override
    public int getCount() {
        lengh = 10;
        return lengh;
    }

    private LayoutInflater mInflater;

    @Override
    public Object getItem(int position) {
        return position;
    }

    private ViewHolder vh = new ViewHolder();

    private static class ViewHolder {
        private TextView time;
        private TextView title;
        private ImageView im;
        private View rightView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layoyt_anchor, null);
            vh.im = (ImageView) convertView.findViewById(R.id.img_anchor_pic);
            vh.time = (TextView) convertView.findViewById(R.id.tv_num_anchor);
            vh.title = (TextView) convertView.findViewById(R.id.tv_name_anchor);
            vh.rightView = (View) convertView.findViewById(R.id.view_right);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.time.setText("00:00");
        vh.title.setText("XXXXXX");
//        if (position!=lengh-1){
//            vh.rightView.setVisibility(View.GONE);
//        }else {
//            vh.rightView.setVisibility(View.VISIBLE);
//        }
        return convertView;
    }
}
