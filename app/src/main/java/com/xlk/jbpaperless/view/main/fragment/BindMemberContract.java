package com.xlk.jbpaperless.view.main.fragment;

import com.xlk.jbpaperless.base.BaseContract;

/**
 * @author Created by xlk on 2021/3/17.
 * @desc
 */
interface BindMemberContract {
    interface View extends BaseContract.View {

        /**
         * 更新会议名称
         *
         * @param meetName 会议名称
         */
        void updateMeetingName(String meetName);
    }

    interface Presenter extends BaseContract.Presenter {
    }
}
