package com.xlk.jbpaperless.view.draw;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.base.BaseActivity;
import com.xlk.jbpaperless.model.GlobalValue;
import com.xlk.jbpaperless.ui.ArtBoard;
import com.xlk.jbpaperless.ui.ColorPickerDialog;

import java.io.File;
import java.util.List;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static com.xlk.jbpaperless.util.ConvertUtil.bmp2bs;

public class DrawActivity extends BaseActivity<DrawPresenter> implements DrawContract.View, View.OnClickListener {


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
    public static boolean isDrawing;
    private ArtBoard artBoard;
    private boolean isAddScreenShot;//是否在发起共享时,添加截图图片
    private final int PICTURE_REQUEST_CODE = 1;
    private ImageView ivDrawLaunch;
    private int mProgress;
    private LinearLayout llSb;

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
        ivDrawLaunch = (ImageView) findViewById(R.id.iv_draw_launch);
        llSb = (LinearLayout) findViewById(R.id.ll_sb);
        verticalSeb = (VerticalRangeSeekBar) findViewById(R.id.vertical_seb);
        sbTextSize = (VerticalRangeSeekBar) findViewById(R.id.sb_text_size);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        ivDrawPen.setOnClickListener(this);
        ivDrawEraser.setOnClickListener(this);
        ivDrawArrow.setOnClickListener(this);
        ivDrawText.setOnClickListener(this);
        ivDrawPicture.setOnClickListener(this);
        ivDrawCircle.setOnClickListener(this);
        ivDrawRect.setOnClickListener(this);
        ivDrawExit.setOnClickListener(this);
        ivDrawSave.setOnClickListener(this);
        ivDrawRevoke.setOnClickListener(this);
        ivDrawClear.setOnClickListener(this);
        ivDrawColor.setOnClickListener(this);
        ivDrawLaunch.setOnClickListener(this);
    }

    @Override
    protected DrawPresenter initPresenter() {
        return new DrawPresenter(this, this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        isDrawing = true;
        flDraw.post(this::initial);
        presenter.queryMember();
        presenter.queryDevice();
        sbTextSize.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mProgress = (int) leftValue;
                LogUtils.e(TAG, "onRangeChanged leftValue=" + leftValue + ",rightValue=" + rightValue
                        + ",isFromUser=" + isFromUser + ",mProgress=" + mProgress);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                LogUtils.e(TAG, "onStartTrackingTouch isLeft=" + isLeft);
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                LogUtils.e(TAG, "onStopTrackingTouch isLeft=" + isLeft);
                tvProgress.setText(String.valueOf(mProgress));
                sbTextSize.setProgress(mProgress);
                artBoard.setPaintWidth(mProgress);
            }
        });
    }

    private void initial() {
        int width = flDraw.getWidth();
        int height = flDraw.getHeight();
        LogUtils.d(TAG, "initial -->" + "width: " + width + ", height: " + height);
        artBoard = new ArtBoard(getApplicationContext(), width, height);
        flDraw.addView(artBoard);
        if (GlobalValue.screenShotBitmap != null) {
            isAddScreenShot = true;//设置发起同屏时是否发送图片
            artBoard.drawZoomBmp(GlobalValue.screenShotBitmap);
        }
        artBoard.setDrawTextListener((x, y) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            EditText editText = new EditText(this);
            builder.setView(editText);
            builder.setOnDismissListener(dialog -> {
                String text = editText.getText().toString().trim();
                if (!text.isEmpty()) {
                    artBoard.drawText(x, y, text);
                }
            });
            builder.create().show();
        });
        artBoard.setDrawStatusListener(isDrawing -> {
            LogUtils.e(TAG, "DrawStatusListener isDrawing=" + isDrawing);
            llStartTools.setVisibility(isDrawing ? View.GONE : View.VISIBLE);
            llEndTools.setVisibility(isDrawing ? View.GONE : View.VISIBLE);
            llSb.setVisibility(isDrawing ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_draw_pen: {
                artBoard.setDrawType(1);
                break;
            }
            case R.id.iv_draw_eraser: {
                artBoard.setDrawType(6);
                break;
            }
            case R.id.iv_draw_arrow: {
                break;
            }
            case R.id.iv_draw_text: {
                artBoard.setDrawType(5);
                break;
            }
            case R.id.iv_draw_picture: {
                Intent i = new Intent(ACTION_OPEN_DOCUMENT);//打开图片
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, PICTURE_REQUEST_CODE);
                break;
            }
            case R.id.iv_draw_circle: {
                artBoard.setDrawType(2);
                break;
            }
            case R.id.iv_draw_rect: {
                artBoard.setDrawType(3);
                break;
            }
            case R.id.iv_draw_exit: {
                finish();
                break;
            }
            case R.id.iv_draw_save: {
                showSaveDialog();
                break;
            }
            case R.id.iv_draw_revoke: {
                artBoard.revoke();
                break;
            }
            case R.id.iv_draw_clear: {
                artBoard.clear();
                //已经清空了就不必发送了
                isAddScreenShot = false;
                break;
            }
            case R.id.iv_draw_color: {
                new ColorPickerDialog(this, color -> artBoard.setPaintColor(color), Color.BLACK).show();
                break;
            }
            case R.id.iv_draw_launch: {

                break;
            }
            default:
                break;
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.please_enter_file_name));
        EditText edt = new EditText(this);
        edt.setHint(R.string.please_enter_file_name);
        edt.setText(String.valueOf(System.currentTimeMillis()));
        //编辑光标移动到最后
        edt.setSelection(edt.getText().toString().length());
        builder.setView(edt);
        builder.setNeutralButton(R.string.save_server, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String trim = edt.getText().toString().trim();
                if (trim.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_file_name);
                    return;
                }
                dialog.dismiss();
                presenter.savePicture(trim, true, artBoard.getCanvasBmp());
            }
        });
        builder.setPositiveButton(R.string.save_local, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String trim = edt.getText().toString().trim();
                if (trim.isEmpty()) {
                    ToastUtils.showShort(R.string.please_enter_file_name);
                    return;
                }
                dialog.dismiss();
                presenter.savePicture(trim, false, artBoard.getCanvasBmp());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void updateShareStatus() {
//        DrawPresenter.isSharing
//        iv_draw_launch
    }

    @Override
    public void drawZoomBmp(Bitmap bitmap) {
        artBoard.drawZoomBmp(bitmap);
    }

    @Override
    public void setCanvasSize(int maxX, int maxY) {
        artBoard.setCanvasSize(maxX, maxY);
    }

    @Override
    public void drawPath(Path path, Paint paint) {
        artBoard.drawPath(path, paint);
    }

    @Override
    public void invalidate() {
        artBoard.invalidate();
    }

    @Override
    public void funDraw(Paint paint, int height, int canSee, int fx, int fy, String text) {
        artBoard.funDraw(paint, height, canSee, fx, fy, text);
    }

    @Override
    public void drawText(String ptext, float lx, float ly, Paint paint) {
        artBoard.drawText(ptext, lx, ly, paint);
    }

    @Override
    public void initCanvas() {
        artBoard.initCanvas();
    }

    @Override
    public void drawAgain(List<ArtBoard.DrawPath> pathList) {
        artBoard.drawAgain(pathList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 获取选中文件的uri
            LogUtils.d(TAG, "onActivityResult: data.toString : " + data.toString());
            Uri uri = data.getData();
            String realPath = null;
            File file = UriUtils.uri2File(uri);
            if (file != null) {
                realPath = file.getAbsolutePath();
            }
            LogUtils.e(TAG, "DrawBoardActivity.onActivityResult :  选中的文件路径 --->>> " + realPath);
            if (realPath == null) {
                LogUtils.e(TAG, "onActivityResult: 获取该文件的路径失败....");
                ToastUtils.showShort(R.string.get_file_path_fail);
            } else {
                // 执行操作
                Bitmap dstbmp = BitmapFactory.decodeFile(realPath);
                //将图片绘制到画板中
                Bitmap bitmap = artBoard.drawZoomBmp(dstbmp);
                //保存图片信息
                ArtBoard.DrawPath drawPath = new ArtBoard.DrawPath();
                drawPath.picdata = bmp2bs(bitmap);
                ArtBoard.LocalPathList.add(drawPath);
                if (DrawPresenter.isSharing) {
                    long time = System.currentTimeMillis();
                    int operid = (int) (time / 10);
                    DrawPresenter.localOperids.add(operid);
                    DrawPresenter.LocalSharingPathList.add(drawPath);
                    jni.addPicture(operid, GlobalValue.localMemberId, DrawPresenter.mSrcmemid, DrawPresenter.mSrcwbid,
                            time, InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_PICTURE.getNumber(), 0, 0, bmp2bs(bitmap));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDrawing = false;
        if (GlobalValue.screenShotBitmap != null) {
            GlobalValue.screenShotBitmap.recycle();
            GlobalValue.screenShotBitmap = null;
        }
        presenter.stopShare();
        presenter.unregister();
        ArtBoard.LocalPathList.clear();
        DrawPresenter.LocalSharingPathList.clear();
        DrawPresenter.localOperids.clear();
        DrawPresenter.togetherIDs.clear();
        DrawPresenter.pathList.clear();
        DrawPresenter.tempPicData = null;
        DrawPresenter.savePicData = null;
        DrawPresenter.mSrcmemid = 0;
        DrawPresenter.mSrcwbid = 0;
        DrawPresenter.disposePicOpermemberid = 0;
        DrawPresenter.disposePicSrcmemid = 0;
        DrawPresenter.disposePicSrcwbidd = 0;
        ArtBoard.artBoardWidth = 0;
        ArtBoard.artBoardHeight = 0;
        artBoard.destroyDrawingCache();
        artBoard = null;
        presenter.onDestroy();
    }
}