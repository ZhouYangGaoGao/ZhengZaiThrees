package com.modernsky.istv.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 万能适配器
 *
 * @param <T>
 * @author fengqingyun
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;
    private SparseArray<View> views;
    // zwz
    private int mPosition;


    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
        this.views = new SparseArray<View>();
    }

    @Override
    public int getCount() {
        if (mDatas == null)
            return 0;
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (mDatas == null)
            return null;
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //zwz
        mPosition = position;
        convertView = views.get(position);
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position));
        views.put(position, convertView);
        return viewHolder.getConvertView();
    }

    public abstract void convert(ViewHolder helper, T item);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

    public int getPosition() {
        return mPosition;
    }

}