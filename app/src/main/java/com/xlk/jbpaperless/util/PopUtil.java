package com.xlk.jbpaperless.util;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.model.GlobalValue;

/**
 * @author Created by xlk on 2020/11/28.
 * @desc
 */
public class PopUtil {
    private static final String TAG = "PopUtil-->";

    /**
     * @param contentView 弹框布局
     * @param w           宽
     * @param h           高
     * @param parent      父控件
     * @return PopupWindow
     */
    public static PopupWindow createCenter(View contentView, int w, int h, View parent,boolean outside) {
        PopupWindow popupWindow = new PopupWindow(contentView, w, h);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(outside);
        popupWindow.setFocusable(outside);
        popupWindow.setAnimationStyle(R.style.pop_animation_t_b);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }
    /**
     * @param contentView 弹框布局
     * @param w           宽
     * @param h           高
     * @param parent      父控件
     * @return PopupWindow
     */
    public static PopupWindow createCenter(View contentView, int w, int h, View parent) {
        PopupWindow popupWindow = new PopupWindow(contentView, w, h);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation_t_b);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    public static PopupWindow createBigPop(View contentView, View parent) {
        PopupWindow popupWindow = new PopupWindow(contentView, GlobalValue.screen_width * 2 / 3, GlobalValue.screen_height * 2 / 3);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation_t_b);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    public static PopupWindow createHalfPop(View contentView, View parent) {
        PopupWindow popupWindow = new PopupWindow(contentView, GlobalValue.screen_width/2, GlobalValue.screen_width/2);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation_t_b);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    public static PopupWindow createSmallPop(View contentView, View parent) {
        PopupWindow popupWindow = new PopupWindow(contentView, GlobalValue.screen_width / 3, GlobalValue.screen_height / 3);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation_t_b);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    /**
     * @param contentView 弹框布局
     * @param w           宽
     * @param h           高
     * @param parent      父控件
     * @return PopupWindow
     */
    public static PopupWindow createAt(View contentView, int w, int h, View parent, int xoff, int yoff) {
        PopupWindow popupWindow = new PopupWindow(contentView, w, h);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation_l_r);
        LogUtils.i(TAG, "createAt xoff=" + xoff + ",yoff=" + yoff);
        popupWindow.showAtLocation(parent, Gravity.START | Gravity.TOP, xoff, yoff);
        return popupWindow;
    }


    public static PopupWindow createAs(View contentView, int width, int height, View parent, int xoff, int yoff) {
        PopupWindow popupWindow = new PopupWindow(contentView, width, height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setTouchable(true);
        // true:设置触摸外面时消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
//        popupWindow.setAnimationStyle(R.style.pop_animation_l_r);
        popupWindow.showAsDropDown(parent, xoff, yoff);
        LogUtils.i(TAG, "createAs xoff=" + xoff + ",yoff=" + yoff);
        return popupWindow;
    }
}
