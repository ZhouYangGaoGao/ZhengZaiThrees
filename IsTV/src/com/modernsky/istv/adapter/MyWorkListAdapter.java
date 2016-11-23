package com.modernsky.istv.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.bean.WorkBean;
import com.modernsky.istv.tool.BitmapTool;
import com.modernsky.istv.tool.TimeTool;

import java.util.List;

/**
 * @author mufaith
 */
public class MyWorkListAdapter extends BaseAdapter {
    private List<WorkBean> data;
    private LayoutInflater inflater;
    private int width;
    private View.OnClickListener listener;
    private boolean isSelf = false, isAnchor = false;

    public MyWorkListAdapter(List<WorkBean> data, LayoutInflater inflater, int width, boolean isSelf, boolean
            isAnchor) {
        super();
        this.data = data;
        this.inflater = inflater;
        this.width = width;
        this.isSelf = isSelf;
        this.isAnchor = isAnchor;
    }

    public void setOnclickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        WorkBean bean = data.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_works_list, null);
            holder = new ViewHolder();
            holder.rl_poter = (RelativeLayout) convertView.findViewById(R.id.rl_poster);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_num_zan = (TextView) convertView.findViewById(R.id.tv_num_zan);
            holder.iv_tag = (ImageView) convertView.findViewById(R.id.iv_tag);
            holder.iv_flag = (ImageView) convertView.findViewById(R.id.iv_flag);
            holder.btn_delete = (Button) convertView.findViewById(R.id.item_delete_shoucangBtn);

            holder.leftContent = convertView.findViewById(R.id.ll_left_content);
            holder.horizontalScrollView = (HorizontalScrollView) convertView;
            //删除选择
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ViewGroup.LayoutParams params = holder.leftContent.getLayoutParams();
        params.width = width;
        holder.leftContent.setLayoutParams(params);

        if (bean.getVideoType() == 1 && isSelf && isAnchor) {

            holder.horizontalScrollView.setOnTouchListener(touchlistener);
            holder.btn_delete.setVisibility(View.VISIBLE);

        } else {
            holder.btn_delete.setVisibility(View.GONE);
            holder.horizontalScrollView.setOnTouchListener(null);
        }

        if (listener != null) {
            holder.btn_delete.setOnClickListener(listener);
            holder.btn_delete.setTag(bean);

        }
        if (listener != null) {
            holder.leftContent.setOnClickListener(listener);
            holder.leftContent.setTag(bean);
        }
        holder.tv_title.setText(bean.getVideoName());
//        BitmapTool.getInstance().getAdapterUitl()
//                .display(holder.iv_tag, bean.getVideoPic());

//            Drawable drawable = inflater.getContext().getResources()
//                    .getDrawable(R.drawable.icon_10_1_bfjl_pc);
//            // / 这一步必须要做,否则不会显示.
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//                    drawable.getMinimumHeight());
//            holder.tv_time.setCompoundDrawables(drawable, null, null, null);

        holder.tv_time.setText(TimeTool.getTime(bean.getShowTime()));
        holder.tv_title.setText(bean.getVideoName());

        BitmapTool.getInstance().getAdapterUitl().display(holder.rl_poter, bean.getVideoPic());
        if (bean.getVideoType() == 1) {
            holder.iv_tag.setVisibility(View.GONE);
            holder.iv_flag.setImageResource(R.drawable.icon_looking);
            holder.tv_num_zan.setText(String.valueOf(bean.getViewCount()));

        } else {
            holder.iv_tag.setVisibility(View.VISIBLE);
            if(bean.getVideoType()==4){
                holder.iv_tag.setImageResource(R.drawable.icon_onair);
                holder.iv_flag.setImageResource(R.drawable.icon_looking);
                holder.tv_num_zan.setText(String.valueOf(bean.getOnlineCount() ));

            }else{
                holder.iv_tag.setImageResource(R.drawable.icon_booking);
                holder.iv_flag.setImageResource(R.drawable.icon_like);
                holder.tv_num_zan.setText(String.valueOf(bean.getSubscribeCount()));
            }

        }
        return convertView;
    }

    View.OnTouchListener touchlistener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    // 获得ViewHolder
                    ViewHolder viewHolder = (ViewHolder) v.getTag();

                    // 获得HorizontalScrollView滑动的水平方向值.
                    int scrollX = viewHolder.horizontalScrollView.getScrollX();
                    // 获得操作区域的长度
                    int actionW = viewHolder.btn_delete.getWidth();

                    // 注意使用smoothScrollTo,这样效果看起来比较圆滑,不生硬
                    // 如果水平方向的移动值<操作区域的长度的一半,就复原
                    if (scrollX < actionW / 2) {
                        viewHolder.horizontalScrollView.smoothScrollTo(0, 0);
                    } else// 否则的话显示操作区域
                    {
                        viewHolder.horizontalScrollView.smoothScrollTo(actionW * 2, 0);
                    }
            }
            return false;
        }
    };

    class ViewHolder {
        RelativeLayout rl_poter;
        TextView tv_title, tv_time, tv_num_zan;
        ImageView iv_tag, iv_flag;
        Button btn_delete;
        HorizontalScrollView horizontalScrollView;
        View leftContent;


    }
}
