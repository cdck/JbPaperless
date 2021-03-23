package com.xlk.jbpaperless.base;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMember;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public interface BaseContract {
    interface View {
        /**
         * 更新参会人详情列表
         * @param memberDetailInfos 包含设备id
         */
        void updateMemberDetailList(List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> memberDetailInfos);

        /**
         * 更新设备列表
         * @param deviceInfos 设备信息
         */
        void updateDeviceList(List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceInfos);

        /**
         * 更新参会人列表
         * @param memberInfos
         */
        void updateMemberList(List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos);

        /**
         * 更新在线状态
         * @param isOnline
         */
        void updateOnlineStatus(boolean isOnline);
    }

    interface Presenter {

    }
}
