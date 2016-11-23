package main.java.cn.aigestudio.datepicker.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.modernsky.istv.R;

import main.java.cn.aigestudio.datepicker.interfaces.IPick;
import main.java.cn.aigestudio.datepicker.interfaces.OnDateSelected;
import main.java.cn.aigestudio.datepicker.views.TitleView.onTitleYearChangedListenner;

/**
 * 日期选择器
 *
 * @author AigeStudio 2015-05-21
 */
public class DatePicker extends LinearLayout implements IPick {
    public  MonthView monthView;
    public  TitleView titleView;
    public DatePicker(Context context) {
        this(context, null);
    }
    public void setTitleViewYearChangedLitenner(onTitleYearChangedListenner listenner){
    	titleView.listenner=listenner;
    }
   
    
    
    
    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.rgb(36, 36, 37));
        setOrientation(VERTICAL);

        LayoutParams llParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        titleView = new TitleView(context);
        addView(titleView, llParams);
        titleView.setBackgroundResource(R.color.hui1a);

        monthView = new MonthView(context);
        monthView.setOnPageChangeListener(titleView);
        monthView.setOnSizeChangedListener(titleView);
        monthView.setBackgroundResource(R.color.hui1f);
        addView(monthView, llParams);
    }

    @Override
    public void setOnDateSelected(OnDateSelected onDateSelected) {
        titleView.setOnDateSelected(onDateSelected, monthView);
    }

    @Override
    public void setOnDateClicked(MonthView.OnMonthItemClickListener onMonthItemClickListener) {
        monthView.setOnMonthItemClickListener(onMonthItemClickListener);
    }

    @Override
    public void setColor(int color) {
        titleView.setColor(color);
        monthView.setColorMain(color);
    }

    @Override
    public void isLunarDisplay(boolean display) {
        monthView.setLunarShow(display);
    }
}
