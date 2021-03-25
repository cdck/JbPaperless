package com.xlk.jbpaperless.view.config;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xlk.jbpaperless.base.BasePresenter;
import com.xlk.jbpaperless.model.EventMessage;

/**
 * @author Created by xlk on 2021/3/24.
 * @desc
 */
class ConfigPresenter extends BasePresenter<ConfigContract.View> implements ConfigContract.Presenter {
    public ConfigPresenter(ConfigContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {

    }
}
