package com.xlk.jbpaperless.view.meet;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.jbpaperless.base.BasePresenter;
import com.xlk.jbpaperless.model.EventMessage;
import com.xlk.jbpaperless.model.EventType;
import com.xlk.jbpaperless.util.DateUtil;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/18.
 * @desc
 */
public class MeetingPresenter extends BasePresenter<MeetingContract.View> implements MeetingContract.Presenter {
    public MeetingPresenter(MeetingContract.View view) {
        super(view);
        cacheData();
    }

    private void cacheData() {
        //缓存会议目录
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber());
        //会议目录文件
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber());
        //缓存会议评分
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN.getNumber());
        // 缓存会场设备
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber());
        //缓存会场设备
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber());
        // 缓存会议排位
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber());
        // 缓存参会人信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber());
        //缓存参会人权限
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION.getNumber());
        //缓存投票信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber());
        //人员签到
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber());
        //公告信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber());
        //会议视频
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber());
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //设备寄存器变更通知
            /*case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备寄存器变更通知");
                queryDevice();
                queryOnlineStatus();
                break;
            }*/
            //设备会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备会议信息变更通知");
                queryDeviceMeetInfo();
                break;
            }
            case EventType.BUS_MEETING_MENU: {
                boolean isNext = (boolean) msg.getObjects()[0];
                mView.onSelectedPage(isNext);
                break;
            }
            //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                byte[] datas = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg inform = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(datas);
                LogUtils.d(TAG, "会议排位变更通知 id=" + inform.getId() + ",operMethod=" + inform.getOpermethod());
                queryHostName();
                break;
            }
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                LogUtils.d(TAG, "会场信息变更通知");
                queryMeetingAddr();
                break;
            }
            //会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE: {
                LogUtils.d(TAG, "会议信息变更通知");
                queryMeetingTime();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void queryDeviceMeetInfo() {
        InterfaceDevice.pbui_Type_DeviceFaceShowDetail object = jni.queryDeviceMeetInfo();
        if (object != null) {
            mView.updateDeviceMeetingInfo(object);
        }
    }

    @Override
    public void updateMeetingMessage() {
        queryMeetingTime();
        queryMeetingAddr();
        queryHostName();
        queryMember();
    }

    private void queryMeetingTime() {
        LogUtils.i(TAG, "queryMeetingTime");
        InterfaceMeet.pbui_Type_MeetMeetInfo pbui_type_meetMeetInfo = jni.queryMeetingById(queryCurrentMeetId());
        if (pbui_type_meetMeetInfo != null) {
            InterfaceMeet.pbui_Item_MeetMeetInfo info = pbui_type_meetMeetInfo.getItemList().get(0);
            String meetingName = info.getName().toStringUtf8();
            String sT = DateUtil.secondFormatDateTime(info.getStartTime());
            String eT = DateUtil.secondFormatDateTime(info.getEndTime());
            String time = sT + " 至 " + eT;
            mView.updateMeetingUseTime(time);
        }
    }

    private void queryMeetingAddr() {
        LogUtils.i(TAG, "queryMeetingAddr");
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo room = jni.queryRoomById(queryCurrentRoomId());
        if (room != null) {
            InterfaceRoom.pbui_Item_MeetRoomDetailInfo item = room.getItem(0);
            String addr = item.getAddr().toStringUtf8();
            mView.updateMeetingAddr(addr);
        }
    }

    private void queryHostName() {
        LogUtils.i(TAG, "queryHostName");
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo pbui_type_meetSeatDetailInfo = jni.queryMeetRanking();
        if (pbui_type_meetSeatDetailInfo != null) {
            List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList = pbui_type_meetSeatDetailInfo.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceRoom.pbui_Item_MeetSeatDetailInfo info = itemList.get(i);
                int role = info.getRole();
                int nameId = info.getNameId();
                if (role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE) {
                    for (int j = 0; j < memberInfos.size(); j++) {
                        if (memberInfos.get(j).getPersonid() == nameId) {
                            mView.updateMeetingHostName(memberInfos.get(j).getName().toStringUtf8());
                            return;
                        }
                    }
                }
            }
        }
    }
}
