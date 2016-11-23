package com.modernsky.istv.window;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.modernsky.istv.R;
import com.modernsky.istv.bean.SearchTypeBean;

import java.util.ArrayList;

public class TitlePop extends PopupWindow {
    public static final int TITLE_LEFT = 0;
    public static final int TITLE_RIGHT = 1;
    protected final int LIST_PADDING = 10;

    private Context mContext;
    private Rect mRect = new Rect();
    private final int[] mLocation = new int[2];

    private int mScreenWidth;
    @SuppressWarnings("unused")
    private int mScreenHeight;
    private boolean mIsDirty;

    private int popupGravity = Gravity.START;

    @SuppressWarnings("unused")
    private int mDirection = TITLE_RIGHT;

    private OnItemOnClickListener mItemOnClickListener;

    private ListView mListView;
    private ArrayList<SearchTypeBean> mActionItems = new ArrayList<SearchTypeBean>();
    //
    public BaseAdapter ba = null;

    public static interface OnItemOnClickListener {
        public void onItemClick(SearchTypeBean mActionItems, int position);
    }

    public TitlePop(Context context) {
        this(context, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public TitlePop(Context context, int width, int height) {
        this.mContext = context;
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        mScreenWidth = TitleUtil.getScreenWidth(mContext);
        mScreenHeight = TitleUtil.getScreenHeight(mContext);
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());
        setContentView(LayoutInflater.from(mContext).inflate(R.layout.title_popup, null));
        setAnimationStyle(R.style.AnimationPreview);
        //
        initUI();
    }

    //
    private void initUI() {
        mListView = (ListView) getContentView().findViewById(R.id.title_list);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                dismiss();
                if (mItemOnClickListener != null)
                    mItemOnClickListener.onItemClick(mActionItems.get(index), index);
            }
        });
    }

    public void addAction(SearchTypeBean action) {
        if (action != null) {
            mActionItems.add(action);
            mIsDirty = true;
        }
    }

    public void addList(ArrayList<SearchTypeBean> mActionItems) {
        this.mActionItems = mActionItems;
    }

    public void cleanAction() {
        if (mActionItems.isEmpty()) {
            mActionItems.clear();
            mIsDirty = true;
        }
    }

    public SearchTypeBean getAction(int position) {
        if (position < 0 || position > mActionItems.size())
            return null;
        return mActionItems.get(position);
    }

    public void setDirection(int direction) {
        this.mDirection = direction;
    }

    public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    private void populateActions() {
        mIsDirty = false;
        ba = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = null;
                if (convertView == null) {
                    textView = new TextView(mContext);
                    textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    textView.setTextSize(18);
                    // textView.setPadding(0, 10, 0, 10);
                    textView.setSingleLine(true);
                    textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                            AbsListView.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.CENTER);
                } else {
                    textView = (TextView) convertView;
                }
                SearchTypeBean item = mActionItems.get(position);
                textView.setText(item.getName());
                textView.setCompoundDrawablePadding(10);
                // textView.setCompoundDrawablesWithIntrinsicBounds(item.mDrawable,
                // null, null, null);
                return textView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return mActionItems.get(position);
            }

            @Override
            public int getCount() {
                return mActionItems.size();
            }
        };
        mListView.setAdapter(ba);
    }

    public void show(View view) {
        // view.getLocationOnScreen(mLocation);
        // mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),
        // mLocation[1] + view.getHeight());
        // if (mIsDirty) {
        populateActions();
        // }
        // showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING -
        // (getWidth() / 2), mRect.bottom);
        showAsDropDown(view);
    }
}