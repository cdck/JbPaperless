package com.xlk.jbpaperless.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Created by xlk on 2021/3/27.
 * @desc
 */
public class VerticalSB extends View {

    private Paint mPaint;

    public VerticalSB(Context context) {
        this(context, null);
    }

    public VerticalSB(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔线帽
        mPaint.setAntiAlias(true);//开启抗锯齿
        mPaint.setDither(true);//设置防抖动
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        canvas.drawRect(0, 0, 10, 100, mPaint);
    }
}
