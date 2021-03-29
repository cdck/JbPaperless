package com.xlk.jbpaperless.view.main.fragment;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.jbpaperless.base.BasePresenter;
import com.xlk.jbpaperless.model.EventMessage;

/**
 * @author Created by xlk on 2021/3/17.
 * @desc
 */
class BindMemberPresenter extends BasePresenter<BindMemberContract.View> implements BindMemberContract.Presenter {

    public BindMemberPresenter(BindMemberContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //设备会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备会议信息变更通知");
                queryDeviceMeetInfo();
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                byte[] o = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(o);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtils.i(TAG, "BusEvent -->" + "参会人员变更通知 id= " + id + ", opermethod= " + opermethod);
                queryMember();
                queryMemberDetailed();
                break;
            }
            default:
                break;
        }
    }

    public void queryDeviceMeetInfo() {
        InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo = jni.queryDeviceMeetInfo();
        if (devMeetInfo != null) {
            mView.updateMeetingName(devMeetInfo.getMeetingname().toStringUtf8());
        }
    }
}
