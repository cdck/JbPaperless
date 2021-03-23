package com.xlk.jbpaperless.view.main;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.jbpaperless.base.BaseContract;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
interface MainContract {
    interface View extends BaseContract.View{

        /**
         * 更新设备ID文本
         */
        void updateDeviceId();

        /**
         * 更新界面内容
         * @param devMeetInfo 设备会议信息
         */
        void updateInterfaceContent(InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo);

        /**
         * 更新时间
         * @param date
         */
        void updateTime(String[] date);

        void readySignIn();

        void jump2meet();
    }
    interface Presenter extends BaseContract.Presenter{
        /**
         * 平台初始化
         * @param uniqueDeviceId 设备的唯一ID
         */
        void initialization(String uniqueDeviceId);
    }
}
