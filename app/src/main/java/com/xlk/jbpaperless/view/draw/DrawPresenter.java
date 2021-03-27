package com.xlk.jbpaperless.view.draw;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xlk.jbpaperless.base.BasePresenter;
import com.xlk.jbpaperless.model.EventMessage;

/**
 * @author Created by xlk on 2021/3/27.
 * @desc
 */
public class DrawPresenter extends BasePresenter<DrawContract.View> implements DrawContract.Presenter {
    public DrawPresenter(DrawContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {

    }
}
