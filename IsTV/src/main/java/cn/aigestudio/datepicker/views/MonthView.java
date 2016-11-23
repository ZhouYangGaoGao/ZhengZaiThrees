package main.java.cn.aigestudio.datepicker.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Region;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.modernsky.istv.R;
import com.modernsky.istv.bean.ScheduleVo;
import com.modernsky.istv.bean.ShowInfoVo;
import com.modernsky.istv.service.CalendarService;
import com.modernsky.istv.service.UserService;
import com.modernsky.istv.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.cn.aigestudio.datepicker.bizs.CalendarBiz;
import main.java.cn.aigestudio.datepicker.entities.BGCircle;
import main.java.cn.aigestudio.datepicker.utils.PaintCircle;

/**
 * 月视图
 *
 * @author AigeStudio 2015-05-21
 */
@SuppressLint("NewApi")
public class MonthView extends View implements
        ValueAnimator.AnimatorUpdateListener {
    private Paint mPaint;
    private TextPaint mTextPaint;
    private Scroller mScroller;

    private CalendarBiz mCalendarBiz;

    private OnPageChangeListener onPageChangeListener;
    private OnSizeChangedListener onSizeChangedListener;
    private OnMonthItemClickListener onMonthItemClickListener;

    private int sizeBase, sizeBaseY;
    private int lastPointY;
    private int lastMoveY;
    private int width, height;
    private int criticalWidth;
    private int index;
    public int lastMonth, currentMonth, nextMonth;
    public int lastYear, currentYear, nextYear;
    private int animZoomOut1, animZoomIn1, animZoomOut2;
    private int circleRadius;
    private int colorMain = 0xFFE95344;

    private float textSizeGregorian, textSizeLunar;
    private float offsetYLunar;
    private boolean isLunarShow = false;
    private List<ScheduleVo> scheduleVoList;
    private Map<String, Object> lmap = new HashMap<String, Object>();
    private EventType mEventType;

    private Map<Integer, List<Region>> calendarRegion = new HashMap<Integer, List<Region>>();
    private Region[][] mRegion = new Region[6][7];
    private Map<String, BGCircle> circlesAppear = new HashMap<String, BGCircle>();
    private Map<String, BGCircle> circlesDisappear = new HashMap<String, BGCircle>();
    private List<String> dateSelected = new ArrayList<String>();
    private float scaleNums = 1.0f;// 高度放大倍数

    // private boolean hasOndraw = false;

    /**
     * 设置日期点击事件
     *
     * @param onMonthItemClickListener
     */
    public void setOnMonthItemClickListener(
            OnMonthItemClickListener onMonthItemClickListener) {
        this.onMonthItemClickListener = onMonthItemClickListener;
    }

    private enum EventType {
        SINGLE, MULTIPLE
    }

    /**
     * 页面改变监听接口
     */
    public interface OnPageChangeListener {
        /**
         * 月份改变回调方法
         *
         * @param month 当前页面显示的月份
         */
        void onMonthChange(int month);

        /**
         * 年份改变回调方法
         *
         * @param year 当前页面显示的年份
         */
        void onYearChange(int year);
    }

    /**
     * 尺寸改变监听接口 当月视图的基准边改变时需要回调给标题视图
     */
    public interface OnSizeChangedListener {
        /**
         * 尺寸改变回调方法
         *
         * @param size 改变后的基准边
         */
        void onSizeChanged(int size);
    }

    public interface OnMonthItemClickListener {
        void onMonthItemClickListener(String date, boolean isSelected,
                                      Region region);
    }

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mScroller = new Scroller(context);

        Calendar calendar = Calendar.getInstance();

        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;

        mCalendarBiz = new CalendarBiz(index, currentYear, currentMonth);
        computeDate();


        buildCalendarRegion();
    }

    /**
     * 设置页面改变时的监听器
     *
     * @param onPageChangeListener ...
     */
    public void setOnPageChangeListener(
            OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        if (null != this.onPageChangeListener) {
            this.onPageChangeListener.onYearChange(currentYear);
            this.onPageChangeListener.onMonthChange(currentMonth);
        }
    }

    /**
     * 设置尺寸改变时的监听器
     *
     * @param onSizeChangedListener ...
     */
    public void setOnSizeChangedListener(
            OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }

    /**
     * 获取选择了的日期
     *
     * @return 选择了的日期列表
     */
    public List<String> getDateSelected() {
        return dateSelected;
    }

    /**
     * 设置是否显示农历
     *
     * @param isLunarShow ...
     */
    public void setLunarShow(boolean isLunarShow) {
        this.isLunarShow = isLunarShow;
        invalidate();
    }

    /**
     * 设置主色调
     *
     * @param colorMain ...
     */
    public void setColorMain(int colorMain) {
        this.colorMain = colorMain;
        invalidate();
    }

    private void computeDate() {
        nextYear = lastYear = currentYear;

        nextMonth = currentMonth + 1;
        lastMonth = currentMonth - 1;

        if (null != onPageChangeListener) {
            onPageChangeListener.onYearChange(currentYear);
        }
        if (currentMonth == 12) {
            nextYear++;
            mCalendarBiz.buildSolarTerm(nextYear);
            if (null != onPageChangeListener) {
                onPageChangeListener.onYearChange(nextYear);
            }
            nextMonth = 1;
        }
        if (currentMonth == 1) {
            lastYear--;
            mCalendarBiz.buildSolarTerm(lastYear);
            if (null != onPageChangeListener) {
                onPageChangeListener.onYearChange(lastYear);
            }
            lastMonth = 12;
        }
    }

    private void buildCalendarRegion() {
        if (!calendarRegion.containsKey(index)) {
            List<Region> regions = new ArrayList<Region>();
            calendarRegion.put(index, regions);
        }
    }

    private String[][] gregorianToLunar(String[][] gregorian, int year,
                                        int month) {
        String[][] lunar = new String[6][7];
        for (int i = 0; i < gregorian.length; i++) {
            for (int j = 0; j < gregorian[i].length; j++) {
                String str = gregorian[i][j];
                if (null == str) {
                    str = "";
                } else {
                    str = mCalendarBiz.gregorianToLunar(year, month,
                            Integer.valueOf(str));
                }
                lunar[i][j] = str;
            }
        }
        return lunar;
    }

    // 这里是点击后的圆圈显示效果,颜色
    private BGCircle createCircle(float x, float y) {
        OvalShape circle = new OvalShape();
        circle.resize(0, 0);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        BGCircle circle1 = new BGCircle(drawable);
        circle1.setX(x);
        circle1.setY(y);
        drawable.getPaint().setColor(Color.rgb(23, 23, 23));
        return circle1;
    }

    @SuppressLint("NewApi")
    private void defineContainRegion(int x, int y) {
        for (int i = 0; i < mRegion.length; i++) {
            for (int j = 0; j < mRegion[i].length; j++) {
                Region region = mRegion[i][j];
                if (null == mCalendarBiz.getGregorianCreated().get(index)[i][j]) {
                    continue;
                }
                if (region.contains(x, y)) {
                    List<Region> regions = calendarRegion.get(index);

                    if (regions.contains(region)) {
                        regions.remove(region);
                    } else {
                        regions.add(region);
                    }
                    String day = mCalendarBiz.getGregorianCreated().get(index)[i][j];
                    if (day.length() == 1) {
                        day = 0 + day;
                    }
                    final String date;
                    if (currentMonth < 10) {
                        date = currentYear + "-0" + currentMonth + "-" + day;
                    } else {
                        date = currentYear + "-" + currentMonth + "-" + day;
                    }

                    if (dateSelected.contains(date)) {
                        LogUtils.d("date111=" + date);
                        if (onMonthItemClickListener != null)
                            onMonthItemClickListener.onMonthItemClickListener(
                                    date, false, region);
                        removeSelectCycle(date);
                    } else {
                        LogUtils.d("date222=" + date);
                        if (onMonthItemClickListener != null)
                            onMonthItemClickListener.onMonthItemClickListener(
                                    date, true, region);
                        List<ShowInfoVo> list = CalendarService.getInstance()
                                .getMap().get(date);
                        if (list != null && list.size() >= 1) {
                            addSelectCycle(region, date);
                        }
                    }
                }
            }
        }
    }

    private void addSelectCycle(Region region, final String date) {
        dateSelected.add(date);

        BGCircle circle = createCircle(region.getBounds().centerX(), region
                .getBounds().centerY() + index * sizeBaseY);

        ValueAnimator animScale1 = ObjectAnimator.ofInt(circle, "radius", 0,
                animZoomOut1);
        animScale1.setDuration(250);
        animScale1.setInterpolator(new DecelerateInterpolator());
        animScale1.addUpdateListener(this);

        ValueAnimator animScale2 = ObjectAnimator.ofInt(circle, "radius",
                animZoomOut1, animZoomIn1);
        animScale2.setDuration(100);
        animScale2.setInterpolator(new AccelerateInterpolator());
        animScale2.addUpdateListener(this);

        ValueAnimator animScale3 = ObjectAnimator.ofInt(circle, "radius",
                animZoomIn1, animZoomOut2);
        animScale3.setDuration(150);
        animScale3.setInterpolator(new DecelerateInterpolator());
        animScale3.addUpdateListener(this);

        ValueAnimator animScale4 = ObjectAnimator.ofInt(circle, "radius",
                animZoomOut2, circleRadius);
        animScale4.setDuration(50);
        animScale4.setInterpolator(new AccelerateInterpolator());
        animScale4.addUpdateListener(this);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animScale1, animScale2, animScale3, animScale4);

        animSet.start();

        // Paintcircle(region,canvas,4);
        circlesAppear.put(date, circle);
    }

    public void removeSelectCycle(final String date) {
        if (dateSelected.contains(date))
            dateSelected.remove(date);
        BGCircle circle = circlesAppear.get(date);

        ValueAnimator animScale = ObjectAnimator.ofInt(circle, "radius",
                circleRadius, 0);
        animScale.setDuration(250);
        animScale.setInterpolator(new AccelerateInterpolator());
        animScale.addUpdateListener(this);
        animScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                circlesDisappear.remove(date);
            }
        });
        animScale.start();

        circlesDisappear.put(date, circle);

        circlesAppear.remove(date);
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
                dy, 500);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPointY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int totalMoveY = (int) (lastPointY - event.getY()) + lastMoveY;
                // smoothScrollTo(totalMoveX, 0);
                smoothScrollTo(0, totalMoveY);
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(lastPointY - event.getY()) > 10) {
                    if (lastPointY > event.getY()) {
                        if (Math.abs(lastPointY - event.getY()) >= criticalWidth) {
                            index++;
                            currentMonth = (currentMonth + 1) % 13;

                            if (currentMonth == 0) {
                                currentMonth = 1;
                                currentYear++;

                                mCalendarBiz.buildSolarTerm(currentYear);
                            }
                            computeDate();
                            if (null != onPageChangeListener) {
                                onPageChangeListener.onYearChange(currentYear);
                                onPageChangeListener.onMonthChange(currentMonth);
                            }
                            buildCalendarRegion();
                        }
                        // smoothScrollTo(width * index, 0);
                        // lastMoveY = width * index;
                        smoothScrollTo(0, height * index);
                        lastMoveY = height * index;
                    } else if (lastPointY < event.getY()) {
                        if (Math.abs(lastPointY - event.getY()) >= criticalWidth) {
                            index--;
                            currentMonth = (currentMonth - 1) % 12;
                            if (currentMonth == 0) {
                                currentMonth = 12;
                                currentYear--;
                                mCalendarBiz.buildSolarTerm(currentYear);
                            }
                            computeDate();
                            if (null != onPageChangeListener) {
                                onPageChangeListener.onYearChange(currentYear);
                                onPageChangeListener.onMonthChange(currentMonth);
                            }
                            buildCalendarRegion();
                        }
                        // smoothScrollTo(width * index, 0);
                        // lastMoveY = width * index;
                        smoothScrollTo(0, height * index);
                        lastMoveY = height * index;
                    }
                } else {
                    LogUtils.d("defineContainRegion");
                    defineContainRegion((int) event.getX(), (int) event.getY());
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);

//        String[][] currentGregorian = mCalendarBiz.getGregorianCreated().get(
//                index);

        // if (null == currentGregorian[4][0]) {
        // setMeasuredDimension(measureWidth, (int) (measureWidth * 4 / 7F));
        // } else if (null == currentGregorian[5][0]) {
        // setMeasuredDimension(measureWidth, (int) (measureWidth * 5 / 7F));
        // } else {
        setMeasuredDimension(measureWidth,
                (int) (measureWidth * 6 / 7F * scaleNums));
        // }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;

        criticalWidth = (int) (1F / 5F * width);

        sizeBase = width;
        sizeBaseY = height;

        if (null != onSizeChangedListener) {
            onSizeChangedListener.onSizeChanged(sizeBase);
        }
        int sizeCell = (int) (sizeBase / 7F);
        circleRadius = sizeCell;
        animZoomOut1 = (int) (sizeCell * 1.0F);
        animZoomIn1 = (int) (sizeCell * 1.0F);
        animZoomOut2 = (int) (sizeCell * 1.0F);

        textSizeGregorian = sizeBase / 20F;
        mTextPaint.setTextSize(textSizeGregorian);

        float gregorianH = mTextPaint.getFontMetrics().bottom
                - mTextPaint.getFontMetrics().top;

        textSizeLunar = sizeBase / 35F;
        mTextPaint.setTextSize(textSizeLunar);

        float lunarH = mTextPaint.getFontMetrics().bottom
                - mTextPaint.getFontMetrics().top;

        offsetYLunar = (((Math.abs(mTextPaint.ascent() + mTextPaint.descent())) / 2)
                + lunarH / 2 + gregorianH / 2) * 3F / 4F;

        for (int i = 0; i < mRegion.length; i++) {
            for (int j = 0; j < mRegion[i].length; j++) {
                Region region = new Region();
                region.set((j * sizeCell), (int) (i * sizeCell * scaleNums),
                        sizeCell + (j * sizeCell), (int) (sizeCell + (i
                                * sizeCell * scaleNums)));
                mRegion[i][j] = region;
            }
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // LogUtils.d("onDraw");
        drawCircle(canvas);
        drawMonths(canvas);
        // if (hasOndraw) {
        // }
        // hasOndraw = true;
    }

    public void update() {
        invalidate();
        LogUtils.d("update();");
    }

    private void drawCircle(Canvas canvas) {
        for (String s : circlesDisappear.keySet()) {
            BGCircle circle = circlesDisappear.get(s);
            drawBGCircle(canvas, circle);
        }
        for (String s : circlesAppear.keySet()) {
            BGCircle circle = circlesAppear.get(s);
            drawBGCircle(canvas, circle);
        }
    }

    private void drawBGCircle(Canvas canvas, BGCircle circle) {
        if (circle == null) {
            return;
        }
        canvas.save();
        canvas.translate(circle.getX() - circle.getRadius() / 2, circle.getY()
                - circle.getRadius() / 2);
        circle.getShape().getShape()
                .resize(circle.getRadius(), circle.getRadius());
        circle.getShape().draw(canvas);
        canvas.restore();
    }

    public void drawMonths(Canvas canvas) {

        // LogUtils.t("currentYear()===", currentYear + "==" + lastMonth + "=="
        // + currentMonth + "==" + nextMonth);
        drawMonth(canvas, (index - 1) * sizeBaseY, lastYear, lastMonth);
        drawMonth(canvas, index * sizeBaseY, currentYear, currentMonth);
        drawMonth(canvas, (index + 1) * sizeBaseY, nextYear, nextMonth);
    }

    @SuppressLint("ResourceAsColor")
    private void drawMonth(Canvas canvas, float offsetY, int year, int month) {
        canvas.save();
        canvas.translate(0, offsetY);

        Calendar calendar = Calendar.getInstance();
        int years = calendar.get(Calendar.YEAR);
        int months = calendar.get(Calendar.MONTH) + 1;
        int days = calendar.get(Calendar.DATE);
        int current = (int) (offsetY / sizeBaseY);
        mTextPaint.setTextSize(textSizeGregorian);
        mTextPaint.setColor(Color.WHITE);

        String[][] gregorianCurrent = mCalendarBiz.getGregorianCreated().get(
                current);

        if (null == gregorianCurrent) {
            gregorianCurrent = mCalendarBiz.buildGregorian(year, month);
        }
        for (int i = 0; i < gregorianCurrent.length; i++) {
            for (int j = 0; j < gregorianCurrent[i].length; j++) {
                String str = gregorianCurrent[i][j];
                if (null == str) {
                    str = "";
                }
                int x = mRegion[i][j].getBounds().centerX();// 圆心的x坐标
                int y = mRegion[i][j].getBounds().centerY();// 圆心的y坐标
                int radius = mRegion[i][j].getBounds().width() / 2;// 大圆 ，半径
                int smCircle = mRegion[i][j].getBounds().width() / 15;// 小圆
//                int colorsMR = 0;// 默认颜色为0，蓝色 ,1为粉色，2为黑色,黑色为本月本天有活动，且未预约
//                Point point;
                boolean isToday = false;
                // 设置画笔颜色
                int day = 0;
                if (TextUtils.isEmpty(str)) {
                    day = 0;
                } else {
                    day = Integer.valueOf(str);
                }
                boolean hasPast;
                if (year < years || (year == years && month < months)
                        || (year == years && month == months && day < days)) {
                    mTextPaint.setColor(getResources()
                            .getColor(R.color.hui7c7c));// 黑色画笔
                    hasPast = true;
                } else {
                    hasPast = false;
                    mTextPaint.setColor(Color.WHITE);//
                }
                if (!TextUtils.isEmpty(str)) {

                    int strs = Integer.valueOf(str);
                    isToday = year == years && month == months && strs == days;
                    if (isToday) {
                        // 今天的日期,以蓝色圆圈显示
                        mPaint.setColor(Color.rgb(176, 216, 244));// 蓝色
                        // mPaint.setColor(Color.GRAY);// 灰色
                        canvas.drawCircle(x, y, radius, mPaint);// 大圆
//                        colorsMR = 2;
                        mTextPaint.setColor(getResources().getColor(
                                R.color.plugin_camera_black));//
                    }

                }

                canvas.drawText(str, x - smCircle / 2, y + 2 * smCircle, mTextPaint);
                if (str.length() == 1) {
                    str = 0 + str;
                }
                String monStr = String.valueOf(month);
                if (monStr.length() < 2) {
                    monStr = 0 + monStr;
                }
                String date = year + "-" + monStr + "-" + str;
                // LogUtils.d("111date=" + date);
                List<ShowInfoVo> data = null;
                data = CalendarService.getInstance().getMap().get(date);
                // LogUtils.d("111data=" + data);
                if (data != null) {
                    ShowInfoVo info;
                    int lenth = data.size();
                    for (int k = 1; k <= lenth; k++) {
                        info = data.get(k - 1);
                        drawSmallCycle(hasPast, canvas, x, y, radius, smCircle,
                                info, 5 - lenth + 2 * k, isToday);

                    }

                }

            }
        }
        if (isLunarShow) {
            String[][] lunarCurrent = mCalendarBiz.getLunarCreated().get(
                    current);
            if (null == lunarCurrent) {
                lunarCurrent = gregorianToLunar(gregorianCurrent, year, month);
            }
            mTextPaint.setTextSize(textSizeLunar);
            for (int i = 0; i < lunarCurrent.length; i++) {
                for (int j = 0; j < lunarCurrent[i].length; j++) {
                    String str = lunarCurrent[i][j];
                    if (str.contains(" ")) {
                        str.trim();
                        mTextPaint.setColor(colorMain);
                    } else {
                        mTextPaint.setColor(Color.GRAY);
                    }
                    canvas.drawText(str, mRegion[i][j].getBounds().centerX(),
                            mRegion[i][j].getBounds().centerY() + offsetYLunar,
                            mTextPaint);
                }
            }
            mCalendarBiz.getLunarCreated().put(current, lunarCurrent);
        }
        mCalendarBiz.getGregorianCreated().put(current, gregorianCurrent);
        canvas.restore();
    }

    private void drawSmallCycle(boolean hasPast, Canvas canvas, int x, int y,
                                int radius, int smCircle, ShowInfoVo info, int sation,
                                boolean isToday) {
        int colorsMR;
        Point point;
/**
 * 预约状态：0:表示没有预约 1：表示本天已经预约 2：表示已经预约全场所有天 3:表示已结束
 */
        // 画笔颜色    蓝色画笔    粉色画笔   黑色画笔  灰色画笔   0123
        if (info.getOpointStatus().equals("0")) {
            colorsMR = 0;
            if (hasPast) {
                colorsMR = 3;
            }
            if (isToday) {
                colorsMR = 2;
            }
            point = PaintCircle.PaintCircless(sation, x, y, radius);
            PaintCircle.paintCircles(this, colorsMR, canvas, point.x, point.y,
                    smCircle);
        } else if(info.getOpointStatus().equals("3")){
            colorsMR = 3;
            point = PaintCircle.PaintCircless(sation, x, y, radius);
            PaintCircle.paintCircles(this, colorsMR, canvas, point.x, point.y,
                    smCircle);
        }else {
            colorsMR = 1;
            if (UserService.getInatance().isNeedLogin(getContext())){
                colorsMR = 0;
            }
            if (hasPast) {
                colorsMR = 3;
            }
            point = PaintCircle.PaintCircless(sation, x, y, radius);
            PaintCircle.paintCircles(this, colorsMR, canvas, point.x, point.y,
                    smCircle);
        }
    }

}
