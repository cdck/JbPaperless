package com.xlk.jbpaperless.base;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.jbpaperless.jni.JniHelper;
import com.xlk.jbpaperless.model.EventMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public abstract class BasePresenter<T extends BaseContract.View> {
    protected String TAG = this.getClass().getSimpleName() + "-->";
    protected JniHelper jni = JniHelper.getInstance();
    public T mView;
    protected List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos = new ArrayList<>();
    protected List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceInfos = new ArrayList<>();
    protected List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> memberDetailInfos = new ArrayList<>();

    public BasePresenter(T view) {
        mView = view;
        register();
    }

    public void initialData() {
        queryMember();
        queryDevice();
        queryMemberDetailed();
        queryOnlineStatus();
    }

    public void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregister() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(EventMessage msg) throws InvalidProtocolBufferException {
        busEvent(msg);
        switch (msg.getType()) {
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
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备寄存器变更通知");
                queryDevice();
                queryOnlineStatus();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 查询所有的参会人
     * InterfaceMember.pbui_Item_MemberDetailInfo
     */
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo object = jni.queryMember();
        memberInfos.clear();
        if (object != null) {
            memberInfos.addAll(object.getItemList());
        }
        mView.updateMemberList(memberInfos);
    }

    /**
     * 查询所有的设备
     * InterfaceDevice.pbui_Item_DeviceDetailInfo
     */
    public void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo object = jni.queryDeviceInfo();
        deviceInfos.clear();
        if (object != null) {
            deviceInfos.addAll(object.getPdevList());
        }
        mView.updateDeviceList(deviceInfos);
    }

    /**
     * 查询参会人详细信息
     * InterfaceMember.pbui_Item_MeetMemberDetailInfo
     */
    public void queryMemberDetailed() {
        InterfaceMember.pbui_Type_MeetMemberDetailInfo memberDetailInfo = jni.queryMemberDetailed();
        memberDetailInfos.clear();
        if (memberDetailInfo != null) {
            memberDetailInfos.addAll(memberDetailInfo.getItemList());
            StringBuilder sb = new StringBuilder();
            sb.append("所有参会人信息：\n");
            for (int i = 0; i < memberDetailInfos.size(); i++) {
                InterfaceMember.pbui_Item_MeetMemberDetailInfo info = memberDetailInfos.get(i);
                sb.append((i + 1) + "、" + info.getMembername().toStringUtf8() + ",人员id=" + info.getMemberid()
                        + ",设备名称=" + info.getDevname().toStringUtf8() + "设备id=" + info.getDevid() + ",是否在线=" + info.getMemberdetailflag() + "\n");
            }
            LogUtils.d(sb.toString());
        }
        mView.updateMemberDetailList(memberDetailInfos);
    }

    /**
     * 获取当前会议的会议id
     *
     * @return 会议id
     */
    protected int queryCurrentMeetId() {
        InterfaceContext.pbui_MeetContextInfo info = jni.queryContextProperty(
                InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURMEETINGID_VALUE);
        int propertyval = info.getPropertyval();
        LogUtils.i(TAG, "queryCurrentMeetId 当前会议的会议id=" + propertyval);
        return propertyval;
    }


    /**
     * 获取当前会议的会场id
     *
     * @return 会场id
     */
    protected int queryCurrentRoomId() {
        InterfaceContext.pbui_MeetContextInfo info = jni.queryContextProperty(
                InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURROOMID_VALUE);
        int propertyval = info.getPropertyval();
        LogUtils.i(TAG, "queryCurrentRoomId 当前会议的会场id=" + propertyval);
        return propertyval;
    }

    /**
     * 查询设备在线状态
     */
    public void queryOnlineStatus() {
        mView.updateOnlineStatus(jni.isOnline());
    }

    /**
     * EventBus发送的消息交给子类去处理
     *
     * @param msg 消息数据
     * @throws InvalidProtocolBufferException byte数组转指定结构体时的异常，避免子类中一直try catch
     */
    protected abstract void busEvent(EventMessage msg) throws InvalidProtocolBufferException;

    public void onDestroy() {
        unregister();
    }
}
