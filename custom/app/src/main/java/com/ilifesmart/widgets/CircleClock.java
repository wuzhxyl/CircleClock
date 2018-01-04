package com.ilifesmart.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.ilifesmart.custom.R;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class CircleClock extends View {

    private String TAG = "CircleClock";
    private int mHourColor = Color.DKGRAY;
    private int mMinusColor = Color.DKGRAY;
    private int mSecondColor = Color.DKGRAY;
    private int mBackgroundColor = Color.LTGRAY;

    private int strokeWidth = 1;
    private int mCirclePointWidth = 4;
    private Paint mPaint;
    private boolean isDraw = false;
    private Calendar mCalendar;

    public CircleClock(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircleClock, defStyle, 0);

        mHourColor = a.getColor(
                R.styleable.CircleClock_hourColor, mHourColor);
        mMinusColor = a.getColor(
                R.styleable.CircleClock_minusColor,
                mMinusColor);
        mSecondColor = a.getColor(
                R.styleable.CircleClock_secondColor,
                mSecondColor);
        mBackgroundColor = a.getColor(R.styleable.CircleClock_backgroundColor,
                mBackgroundColor);
        mCirclePointWidth = a.getDimensionPixelSize(R.styleable.CircleClock_circlePointWidth,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCirclePointWidth, getResources().getDisplayMetrics()));

        a.recycle();

        invalidatePaint();
    }

    private void invalidatePaint() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCalendar = Calendar.getInstance();
    }

    private int getContentWidth() {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        return getWidth() - paddingLeft - paddingRight;
    }

    private int getContentHeight() {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        return getHeight() - paddingTop - paddingBottom;
    }

    private int getCircleRadiusX() {
        return getPaddingLeft() + getContentWidth()/2;
    }

    private int getCircleRadiusY() {
        return getPaddingTop() + getContentHeight()/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = Math.min(getContentWidth(), getContentHeight())/2;

        if (!isDraw) {
            // 画背景
            drawClockBackGround(canvas, radius);

            // 画刻度
            drawClockScale(canvas, radius);
        }

        // 画线
        drawClockLines(canvas, radius);

        // 画圆点
        drawClockPoint(canvas);
        postInvalidate();
    }

    protected void drawClockBackGround(Canvas canvas, int radius) {
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getCircleRadiusX(), getCircleRadiusY(), radius, mPaint);
    }

    protected void drawClockPoint(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getCircleRadiusX(), getCircleRadiusY(), mCirclePointWidth, mPaint);
    }

    protected void drawClockScale(Canvas canvas, int radius) {
        int lineLength = (int) Math.floor(radius/16);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(strokeWidth);

        // left
        canvas.drawLine(getPaddingLeft(), getCircleRadiusY(), getPaddingLeft()+lineLength, getCircleRadiusY(), mPaint);
        // top
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY()+radius, getCircleRadiusX(), getCircleRadiusY()+radius-lineLength, mPaint);
        // right
        canvas.drawLine(getWidth()-getPaddingRight(), getCircleRadiusY(), getWidth()-getPaddingRight()-lineLength, getCircleRadiusY(), mPaint);
        // bottom
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY()-radius, getCircleRadiusX(), getCircleRadiusY()-radius+lineLength, mPaint);
    }

    protected void drawClockLines(Canvas canvas, int radius) {
        Time time = getCurrentTime();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2*strokeWidth);
        mPaint.setColor(mHourColor);

        int hour = time.hour;
        hour = (hour > 12 ? (hour-12) :hour );
        Info info = getRadianInfo(hour, 12, 0.4F);
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY(), (getCircleRadiusX() + info.x), (getCircleRadiusY()-info.y), mPaint);

        mPaint.setStrokeWidth(2*strokeWidth);
        mPaint.setColor(mMinusColor);
        info = getRadianInfo(time.minus, 60, 0.6F);
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY(), (getCircleRadiusX()+info.x), (getCircleRadiusY()-info.y), mPaint);

        mPaint.setStrokeWidth(2*strokeWidth);
        mPaint.setColor(mSecondColor);

        int second = time.second*1000 + time.mills;
        info = getRadianInfo(second, 60000, 0.8F);
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY(), (getCircleRadiusX()+info.x), (getCircleRadiusY()-info.y), mPaint);
    }

    protected Info getRadianInfo(int number, int max, float percent) {
        double radian = number*2*Math.PI/max;
        final double radius = Math.min(getContentWidth(), getContentHeight())/2;
        Info i = new Info();
        i.x = (float) (Math.sin(radian) * radius * percent);
        i.y = (float) (Math.cos(radian) * radius * percent);

        return i;
    }

    private Time getCurrentTime() {

        long currMillis = System.currentTimeMillis();
        mCalendar.setTimeInMillis(currMillis);

        Time info = new Time();
        info.hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        info.minus = mCalendar.get(Calendar.MINUTE);
        info.second = mCalendar.get(Calendar.SECOND);
        info.mills = mCalendar.get(Calendar.MILLISECOND);

        return info;
    }

    private class Info {
        public float x = 0;
        public float y = 0;
    }

    private class Time {
        public int hour;
        public int minus;
        public int second;
        public int mills;

        @Override
        public String toString() {
            return new StringBuilder().append("hour ").append(hour).append(" minus ").append(minus)
                    .append(" second ").append(second).append(" mills ").append(mills).toString();
        }
    }

//View的生命周期
/*
[改变可见性] --> 构造View() --> onFinishInflate() -->
onAttachedToWindow() -->
onMeasure() --> onSizeChanged() --> onLayout() --> onDraw() -->
onDetackedFromWindow()
*/

// 详情如下.
/*
* onFinishInflate: 方法当View及其子View从XML文件中加载完成后会被调用.
* */

/*
*  Layout
*  onMeasure
*  onLayout
*  onSzieChanged
*
* */

/*
*  Drawing
*  onDraw
* */

/*
*  Event processing
*
*  onKeyDown： 物理按键按下
*  onKeyUp： 物理按键松开
*  onTrackball: 轨迹球
*  onTouchEvent: 触摸事件
* */

/*
*  Focus
*  onFocusChanged: 当前View获得或失去焦点的时候被调用
*  onWIndowFocusChanged：当前view的Window获得或失去焦点的时候被调用
* */

/*
*  Attaching
*
*  onAttachedToWindow： 添加到Window时
*  onDetachedFromWindow：从Window被移除时
*  onVisibilityChanged： view及其祖先的可见性被调用
*  onWindowVisibilityChanged: 包含view的Window可见性改变的时候
* */


}
