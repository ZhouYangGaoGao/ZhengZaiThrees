package com.modernsky.istv.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.modernsky.istv.acitivity.ImagePreviewActivity;
import com.modernsky.istv.R;
import com.modernsky.istv.bean.Content;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.utils.Constants;

public class PinglunGridAdapter extends BaseAdapter {
    private List<Content> list;
    private ArrayList<String> strList;
    private LayoutInflater inflater;
    private BitmapUtils bitmapTool;
    private Context mContext;

    public PinglunGridAdapter(List<Content> contents, Context context) {
        super();
        this.list = contents;
        this.inflater = LayoutInflater.from(context);
        bitmapTool = BitmapTool.getInstance().initAdapterUitl(context);
        this.mContext = context;
        strList = new ArrayList<String>();
        for (Content content : contents) {
            strList.add(content.getContent());
        }
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflater
                    .inflate(R.layout.item_gridview_pinglun, null);
            vh = new ViewHolder();
            vh.imageView = (ImageView) convertView.findViewById(R.id.img_grid);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Content map = list.get(position);
        bitmapTool.display(vh.imageView, map.getContent());
        vh.imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImagePreviewActivity.class);
                intent.putStringArrayListExtra(Constants.DATA, strList);
                intent.putExtra(Constants.POSOTION, position);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }

}
