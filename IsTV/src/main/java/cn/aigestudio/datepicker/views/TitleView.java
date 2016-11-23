package main.java.cn.aigestudio.datepicker.views;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import main.java.cn.aigestudio.datepicker.entities.Language;
import main.java.cn.aigestudio.datepicker.interfaces.OnDateSelected;

/**
 * 日期选择器的标题视图
 * 
 * @author AigeStudio 2015-05-21
 */
public class TitleView extends RelativeLayout implements
		MonthView.OnPageChangeListener, MonthView.OnSizeChangedListener {
	private String[] monthTitles;
	private TextView tvYear, tvMonth, tvConfirm, UnLines;
	private OnDateSelected mOnDateSelected;
	private MonthView monthView;
	private int year, month;
	Context context;
	Calendar calendar;
	public onMonthChangedListenner monthlistenner;
	public onTitleYearChangedListenner listenner;// 用来改变activity的title年份的接口

	public TitleView(Context context) {
		super(context);
		this.context = context;
		calendar = calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		setColor(Color.rgb(36, 36, 37));
		// setOrientation(HORIZONTAL);
		monthTitles = Language.getLanguage(context).monthTitles();
		tvMonth = new TextView(context);
		tvMonth.setLines(1);
		tvMonth.setGravity(Gravity.CENTER);
		tvMonth.setTextColor(Color.rgb(176, 216, 244));
		tvMonth.setText(String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
		UnLines = new TextView(context);
		setMonthStation(Calendar.getInstance().get(Calendar.YEAR), Calendar
				.getInstance().get(Calendar.MONTH) + 1);
		// addView(tvConfirm, llParams);
	}

	public void setOnDateSelected(OnDateSelected onDateSelected,
			MonthView monthView) {
		mOnDateSelected = onDateSelected;
		this.monthView = monthView;

	}

	public void setColor(int color) {
		setBackgroundColor(color);
	}

	@Override
	public void onMonthChange(int month) {
		this.month = month;
		tvMonth.setText(monthTitles[month - 1]);
		setMonthStation(year, month);
		if (monthlistenner != null) {
			monthlistenner.onMonthChanded(month);
		}

	}

	public void setMonthStation(int year, int month) {
		calendar.set(year, month - 1, 1);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
//		LogUtils.d("year=" + year + "month=" + month);
//		LogUtils.d("week===" + week);
		int w = this.getWidth() / 7;
		RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(
				w, LayoutParams.WRAP_CONTENT);
		llParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		if (week == 1) {
			llParams.setMargins(w * 6, 0, 0, 0);
		} else {
			llParams.setMargins(w * (week - 2), 0, 0, 0);
		}
		removeAllViews();
		addView(tvMonth, llParams);
		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 2);
		UnLines.setBackgroundColor(Color.WHITE);
		lParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lParams.addRule(RelativeLayout.BELOW, 1);
		lParams.addRule(RelativeLayout.ALIGN_LEFT, 1);
		addView(UnLines, lParams);
	}

	@Override
	public void onYearChange(int year) {
		this.year = year;
		setMonthStation(year, month);
		if (listenner != null) {
			listenner.onTitleYearChanded(year);
		}
	}

	@Override
	public void onSizeChanged(int size) {
		int padding = (int) (size * 1F / 50F);
		int textSizeSmall = (int) (size * 1F / 25F);
//		int textSizeLarge = (int) (size * 1F / 18F);
		// tvYear.setPadding(padding, padding, 0, padding);
		// tvYear.getPaint().setTextSize(textSizeSmall);

		tvMonth.setPadding(0, padding, 0, padding);
		tvMonth.getPaint().setTextSize(textSizeSmall);
		setMonthStation(year, month);
		// tvConfirm.setPadding(0, padding, padding, padding);
		// tvConfirm.getPaint().setTextSize(textSizeSmall);
	}

	public interface onTitleYearChangedListenner {
		public void onTitleYearChanded(int year);
	}

	public interface onMonthChangedListenner {
		public void onMonthChanded(int month);
	}
}
