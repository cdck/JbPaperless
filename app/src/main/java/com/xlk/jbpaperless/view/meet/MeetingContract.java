package com.xlk.jbpaperless.view.meet;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.jbpaperless.base.BaseContract;

/**
 * @author Created by xlk on 2021/3/18.
 * @desc
 */
interface MeetingContract {
    interface View extends BaseContract.View {

        /**
         * 更新参会人名称和会议名称
         * @param devMeetInfo
         */
        void updateDeviceMeetingInfo(InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo);

        void onSelectedPage(boolean isNext);

        /**
         * 更新会议地点
         *
         * @param addr
         */
        void updateMeetingAddr(String addr);

        /**
         * 更新会议时间
         *
         * @param time 开始时间到结束时间
         */
        void updateMeetingUseTime(String time);

        /**
         * 更新会议主持人名称
         * @param hostName 主持人名称
         */
        void updateMeetingHostName(String hostName);
    }

    interface Presenter extends BaseContract.Presenter {

        void queryDeviceMeetInfo();

        void updateMeetingMessage();
    }
}
