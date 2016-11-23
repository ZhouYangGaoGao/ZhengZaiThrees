package com.modernsky.istv.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.modernsky.istv.R;
import com.modernsky.istv.view.DrageLayout;

public class WidgetRadioDragger extends DrageLayout {
    private static WidgetRadioDragger instance = null;
    private DrageLayout mDrageLayout = null;
    private RelativeLayout mDrageView = null;
    private RelativeLayout mDragButtomView = null;

    public WidgetRadioDragger(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_diantai, this);
        initView(view);
    }

    public static WidgetRadioDragger getInstance(Context context) {
        if (instance == null) {
            instance = new WidgetRadioDragger(context);
        }
        return instance;
    }

    private void initView(View view) {
        mDrageLayout = (DrageLayout) findViewById(R.id.drageLayout);
        mDrageView = (RelativeLayout) findViewById(R.id.draglayoutView);
        mDragButtomView = (RelativeLayout) findViewById(R.id.layoutButtom_drag);
        mDrageLayout.setView(mDrageView, mDragButtomView);
    }
}
