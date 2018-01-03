package com.ilifesmart.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Px;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.ilifesmart.custom.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * TODO: document your custom view class.
 */
public class CircleTextView extends View {
    private String mTitle;
    private int mFontColor = Color.RED;
    private int mBackgroundColor = Color.LTGRAY;
    private int mFontSize = 24;
    private TextPaint mTextPaint;
    private Paint mPaint;
    private float mTextWidth;
    private float mTextHeight;
    private int density;

    public CircleTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes

        density = (int)getContext().getResources().getDisplayMetrics().density;
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircleTextView, defStyle, 0);

        mTitle = a.getString(
                R.styleable.CircleTextView_title);
        mFontColor = a.getColor(
                R.styleable.CircleTextView_fontColor,
                mFontColor);
        mBackgroundColor = a.getColor(
                R.styleable.CircleTextView_bgColor,
                mBackgroundColor);

        mFontSize = a.getDimensionPixelSize(R.styleable.CircleTextView_fontSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, getResources().getDisplayMetrics()));

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mFontSize*density);
        mTextPaint.setColor(mFontColor);
        mTextWidth = mTextPaint.measureText(mTitle);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent + fontMetrics.ascent; // ascent为负， decent为正， 相加为向上的偏移量.
    }

    private String getCutString(int maxWidth) {
        String ret = mTitle;

        int width = (int) mTextPaint.measureText(ret);
        while (width > maxWidth) {
            ret = ret.substring(0, ret.length()-1);
            width = (int) mTextPaint.measureText(ret);
        }

        return ret;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        int radius = Math.min(contentWidth, contentHeight)/2;

        String retStr = getCutString(contentWidth);
        Log.d("CircleTextView", "onDraw: retStr " + retStr);
        // Draw the Circle.
        mPaint.setColor(mBackgroundColor);
        canvas.drawCircle(paddingLeft+contentWidth/2, paddingTop+contentHeight/2, radius, mPaint);

        // Draw the text.
        canvas.drawText(retStr,
                paddingLeft + contentWidth / 2,
                paddingTop + contentHeight / 2 - mTextHeight/2,
                mTextPaint);

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        canvas.drawLine(paddingLeft, paddingTop+contentHeight/2, radius*2, paddingTop+contentHeight/2, mPaint);
    }

    public void setTitle(String title) {
        mTitle = title;
        postInvalidate();
    }
}
