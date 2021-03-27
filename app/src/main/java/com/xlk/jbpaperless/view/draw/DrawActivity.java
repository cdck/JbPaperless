package com.xlk.jbpaperless.view.draw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.base.BaseActivity;

public class DrawActivity extends BaseActivity<DrawPresenter> implements DrawContract.View {


    private android.widget.FrameLayout flDraw;
    private android.widget.LinearLayout llStartTools;
    private android.widget.ImageView ivDrawPen;
    private android.widget.ImageView ivDrawEraser;
    private android.widget.ImageView ivDrawArrow;
    private android.widget.ImageView ivDrawText;
    private android.widget.ImageView ivDrawPicture;
    private android.widget.ImageView ivDrawCircle;
    private android.widget.ImageView ivDrawRect;
    private android.widget.LinearLayout llEndTools;
    private android.widget.ImageView ivDrawExit;
    private android.widget.ImageView ivDrawSave;
    private android.widget.ImageView ivDrawRevoke;
    private android.widget.ImageView ivDrawClear;
    private android.widget.ImageView ivDrawColor;
    private com.jaygoo.widget.VerticalRangeSeekBar verticalSeb;
    private TextView tvProgress;
    private VerticalRangeSeekBar sbTextSize;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draw;
    }

    @Override
    protected void initView() {
        flDraw = (FrameLayout) findViewById(R.id.fl_draw);
        llStartTools = (LinearLayout) findViewById(R.id.ll_start_tools);
        ivDrawPen = (ImageView) findViewById(R.id.iv_draw_pen);
        ivDrawEraser = (ImageView) findViewById(R.id.iv_draw_eraser);
        ivDrawArrow = (ImageView) findViewById(R.id.iv_draw_arrow);
        ivDrawText = (ImageView) findViewById(R.id.iv_draw_text);
        ivDrawPicture = (ImageView) findViewById(R.id.iv_draw_picture);
        ivDrawCircle = (ImageView) findViewById(R.id.iv_draw_circle);
        ivDrawRect = (ImageView) findViewById(R.id.iv_draw_rect);
        llEndTools = (LinearLayout) findViewById(R.id.ll_end_tools);
        ivDrawExit = (ImageView) findViewById(R.id.iv_draw_exit);
        ivDrawSave = (ImageView) findViewById(R.id.iv_draw_save);
        ivDrawRevoke = (ImageView) findViewById(R.id.iv_draw_revoke);
        ivDrawClear = (ImageView) findViewById(R.id.iv_draw_clear);
        ivDrawColor = (ImageView) findViewById(R.id.iv_draw_color);
        verticalSeb = (VerticalRangeSeekBar) findViewById(R.id.vertical_seb);
        sbTextSize = (VerticalRangeSeekBar) findViewById(R.id.sb_text_size);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
    }

    @Override
    protected DrawPresenter initPresenter() {
        return new DrawPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        sbTextSize.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                LogUtils.i("seekbar", "onRangeChanged leftValue=" + leftValue + ",rightValue=" + rightValue + ",isFromUser=" + isFromUser);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                LogUtils.i("seekbar", "onStartTrackingTouch isLeft=" + isLeft);
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                LogUtils.i("seekbar", "onStopTrackingTouch isLeft=" + isLeft);
            }
        });
    }
}