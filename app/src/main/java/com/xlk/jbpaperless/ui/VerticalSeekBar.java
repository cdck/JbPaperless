package com.xlk.jbpaperless.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.SeekBar;

import com.xlk.jbpaperless.R;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * @author Created by xlk on 2021/3/27.
 * @desc
 */
public class VerticalSeekBar extends AppCompatSeekBar {
    // 画笔
    private Paint mPaint;
    // 进度文字位置信息
    private Rect mProgressTextRect = new Rect();
    // 滑块按钮宽度
    private int mThumbWidth = dp2px(20);
    // 进度指示器宽度
    private int mIndicatorWidth = dp2px(20);
    // 进度监听
    private OnIndicatorSeekBarChangeListener mIndicatorSeekBarChangeListener;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMax(6);
        init();
    }

    private void init() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#00574B"));
        mPaint.setTextSize(sp2px(16));

        // 如果不设置padding，当滑动到最左边或最右边时，滑块会显示不全
//        setPadding(mThumbWidth / 2, 0, mThumbWidth / 2, 0);
        setPadding(0, mThumbWidth / 2, 0, mThumbWidth / 2);

        // 设置滑动监听
        this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // NO OP
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mIndicatorSeekBarChangeListener != null) {
                    mIndicatorSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mIndicatorSeekBarChangeListener != null) {
                    mIndicatorSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
        String progressText = getProgress()+"";//+ "%";
        mPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);

        // 进度百分比
        float progressRatio = (float) getProgress() / getMax();
        // thumb偏移量
        float thumbOffset = (mThumbWidth - mProgressTextRect.width()) / 2 - mThumbWidth * progressRatio;
        float thumbX = getWidth() * progressRatio + thumbOffset;
        float thumbY = getHeight() / 2f + mProgressTextRect.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mPaint);

        if (mIndicatorSeekBarChangeListener != null) {
            float indicatorOffset = getWidth() * progressRatio - (mIndicatorWidth - mThumbWidth) / 2 - mThumbWidth * progressRatio;
            mIndicatorSeekBarChangeListener.onProgressChanged(this, getProgress(), indicatorOffset);
        }
    }

    /**
     * 设置进度监听
     *
     * @param listener OnIndicatorSeekBarChangeListener
     */
    public void setOnSeekBarChangeListener(OnIndicatorSeekBarChangeListener listener) {
        this.mIndicatorSeekBarChangeListener = listener;
    }

    /**
     * 进度监听
     */
    public interface OnIndicatorSeekBarChangeListener {
        /**
         * 进度监听回调
         *
         * @param seekBar         SeekBar
         * @param progress        进度
         * @param indicatorOffset 指示器偏移量
         */
        public void onProgressChanged(SeekBar seekBar, int progress, float indicatorOffset);

        /**
         * 开始拖动
         *
         * @param seekBar SeekBar
         */
        public void onStartTrackingTouch(SeekBar seekBar);

        /**
         * 停止拖动
         *
         * @param seekBar SeekBar
         */
        public void onStopTrackingTouch(SeekBar seekBar);
    }

    /**
     * dp转px
     *
     * @param dp dp值
     * @return px值
     */
    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param sp sp值
     * @return px值
     */
    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
