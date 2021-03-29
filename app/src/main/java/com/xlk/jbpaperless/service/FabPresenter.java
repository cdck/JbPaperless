package com.xlk.jbpaperless.service;

import android.content.Context;

import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.jbpaperless.model.EventMessage;
import com.xlk.jbpaperless.view.draw.DrawActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author Created by xlk on 2021/3/29.
 * @desc
 */
public class FabPresenter {
    private final IFab mView;
    private final Context cxt;

    public FabPresenter(IFab view, Context context) {
        mView = view;
        cxt = context;
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(EventMessage msg) {
        switch (msg.getType()) {
            //收到打开白板通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ASK_VALUE) {
                    if (!DrawActivity.isDrawing) {
                        mView.openArtBoardInform(msg);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }
}
