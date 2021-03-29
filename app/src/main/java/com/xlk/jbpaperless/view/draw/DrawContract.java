package com.xlk.jbpaperless.view.draw;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;

import com.google.protobuf.ByteString;
import com.xlk.jbpaperless.base.BaseContract;
import com.xlk.jbpaperless.ui.ArtBoard;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/27.
 * @desc
 */
interface DrawContract {
    interface View extends BaseContract.View {
        void updateShareStatus();

        void drawZoomBmp(Bitmap bs2bmp);

        void setCanvasSize(int maxX, int maxY);

        void drawPath(Path newPath, Paint newPaint);

        void invalidate();

        void funDraw(Paint newPaint, int height, int i, int x, int y, String ptext);

        void drawText(String ptext, float lx, float ly, Paint newPaint);

        void initCanvas();

        void drawAgain(List<ArtBoard.DrawPath> pathList);
    }

    interface Presenter extends BaseContract.Presenter {
        void stopShare();

        void savePicture(String trim, boolean server, Bitmap bitmap);
    }
}
