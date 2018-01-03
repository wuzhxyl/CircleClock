package com.ilifesmart.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ilifesmart.custom.R;

/**
 * TODO: document your custom view class.
 */
public class CircleClock extends View {
    private int mHourColor = Color.LTGRAY;
    private int mMinusColor = Color.LTGRAY;
    private int mSecondColor = Color.LTGRAY;
    private int mBackgroundColor = Color.DKGRAY;

    private int strokeWidth = 1;
    private int mCirclePointWidth = 4;
    private Paint mPaint;

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

        // 画背景
        drawClockBackGround(canvas, radius);

        // 画刻度
        drawClockScale(canvas, radius);

        // 画线
        drawClockLines(canvas, radius, 3, 30, 45);

        // 画圆点
        drawClockPoint(canvas);
    }

    protected void drawClockBackGround(Canvas canvas, int radius) {
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(getCircleRadiusX(), getCircleRadiusY(), radius, mPaint);
    }

    protected void drawClockPoint(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getCircleRadiusX(), getCircleRadiusY(), mCirclePointWidth, mPaint);
    }

    protected void drawClockScale(Canvas canvas, int radius) {
        int lineLength = (int) Math.floor(radius/16);
        mPaint.setColor(Color.WHITE);
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

    protected void drawClockLines(Canvas canvas, int radius, int hour, int minus, int second) {

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2*strokeWidth);
        mPaint.setColor(mHourColor);

        hour = (hour > 12 ? (hour-12) :hour );
        Info info = getRadianInfo(hour, 12, 0.4F);
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY(), (getCircleRadiusX() + info.x), (getCircleRadiusY()+info.y), mPaint);

        mPaint.setStrokeWidth(2*strokeWidth);
        mPaint.setColor(mMinusColor);
        info = getRadianInfo(minus, 60, 0.6F);
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY(), (getCircleRadiusX()+info.x), (getCircleRadiusY()+info.y), mPaint);

        mPaint.setStrokeWidth(2*strokeWidth);
        mPaint.setColor(Color.BLUE);
        info = getRadianInfo(second, 60, 0.8F);
        canvas.drawLine(getCircleRadiusX(), getCircleRadiusY(), (getCircleRadiusX()+info.x), (getCircleRadiusY()+info.y), mPaint);
    }

    protected Info getRadianInfo(int number, int max, float percent) {
        double radian = number*2*Math.PI/max;
        final double radius = Math.min(getContentWidth(), getContentHeight())/2;
        Info i = new Info();
        i.x = (float) (Math.sin(radian) * radius * percent);
        i.y = (float) (Math.cos(radian) * radius * percent);

        return i;
    }

    private class Info {
        public float x = 0;
        public float y = 0;
    }
}
