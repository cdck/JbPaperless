package com.xlk.jbpaperless.jni;

import android.graphics.PointF;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.mogujie.tt.protobuf.InterfaceUpload;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.xlk.jbpaperless.model.Constant;
import com.xlk.jbpaperless.model.GlobalValue;

import java.util.List;

import static com.xlk.jbpaperless.model.Constant.CAMERA_SUB_ID;
import static com.xlk.jbpaperless.model.Constant.RESOURCE_ID_0;
import static com.xlk.jbpaperless.model.Constant.SCREEN_SUB_ID;
import static com.xlk.jbpaperless.util.ConvertUtil.s2b;


/**
 * @author Created by xlk on 2020/11/26.
 * @desc
 */
public class JniHelper {
    private final String TAG = "JniHelper-->";
    private Call jni;
    private static JniHelper instance;

    public static JniHelper getInstance() {
        if (instance == null) {
            synchronized (JniHelper.class) {
                if (instance == null) {
                    instance = new JniHelper();
                }
            }
        }
        return instance;
    }

    private JniHelper() {
        jni = Call.getInstance();
    }


    /**
     * ??????????????????????????????
     *
     * @param uniqueId ?????????
     */
    public boolean javaInitSys(String uniqueId) {
        LogUtils.i("javaInitSys=" + uniqueId);
        InterfaceBase.pbui_MeetCore_InitParam pb = InterfaceBase.pbui_MeetCore_InitParam.newBuilder()
                .setPconfigpathname(s2b(Constant.root_dir + "client.ini"))
                .setProgramtype(InterfaceMacro.Pb_ProgramType.Pb_MEET_PROGRAM_TYPE_MEETCLIENT.getNumber())
                .setStreamnum(4)
                .setLogtofile(0)
                .setKeystr(s2b(uniqueId))
                .build();
        boolean bret = true;
        if (-1 == jni.Init_walletSys(pb.toByteArray())) {
            bret = false;
        }
        return bret;
    }

    /**
     * ?????????????????????
     * Pb_MemState_MainFace=0; //???????????????
     * Pb_MemState_MemFace=1;//??????????????????
     * Pb_MemState_AdminFace=2;//??????????????????
     *
     * @param propertyid InterfaceMacro.Pb_ContextPropertyID
     * @param value      InterfaceMacro.Pb_MeetFaceStatus
     * @see InterfaceMacro.Pb_ContextPropertyID
     */
    public void modifyContextProperties(int propertyid, int value) {
        InterfaceContext.pbui_MeetContextInfo build = InterfaceContext.pbui_MeetContextInfo.newBuilder()
                .setPropertyid(propertyid)
                .setPropertyval(value)
                .build();
        byte[] bytes = build.toByteArray();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCONTEXT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SETPROPERTY_VALUE, bytes);
        LogUtils.e(TAG, "setInterfaceState:  ?????????????????????  --->>> propertyid= " + propertyid + ", value = " + value);
    }

    public void InitAndCapture(int type, int channelindex) {
        jni.InitAndCapture(type, channelindex);
    }

    /**
     * ????????????ID???????????????
     *
     * @param devid ??????id
     */
    public InterfaceDevice.pbui_Item_DeviceDetailInfo queryDevInfoById(int devid) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(devid).build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (array != null) {
            try {
                InterfaceDevice.pbui_Type_DeviceDetailInfo pbui_type_deviceDetailInfo = InterfaceDevice.pbui_Type_DeviceDetailInfo.parseFrom(array);
                if (pbui_type_deviceDetailInfo != null) {
                    if (pbui_type_deviceDetailInfo.getPdevCount() > 0) {
                        LogUtils.e(TAG, "queryDevInfoById :  ????????????ID????????????????????? --> devid=" + devid);
                        InterfaceDevice.pbui_Item_DeviceDetailInfo pdev = pbui_type_deviceDetailInfo.getPdev(0);
                        return pdev;
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryDevInfoById :  ????????????ID????????????????????? --> devid=" + devid);
        return null;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public InterfaceFaceconfig.pbui_Type_FaceConfigInfo queryInterFaceConfiguration() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                InterfaceFaceconfig.pbui_Type_FaceConfigInfo pbui_type_faceConfigInfo = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.parseFrom(array);
                LogUtils.i(TAG, "queryInterFaceConfiguration :  ?????????????????? -->?????? ");
                return pbui_type_faceConfigInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryInterFaceConfiguration :  ?????????????????? -->?????? ");
        return null;
    }


    /**
     * ????????????????????????
     *
     * @param pathName   ???????????????????????????
     * @param mediaId    ??????ID
     * @param isNewFile  =0 ?????????????????????,=1 ????????????
     * @param onlyFinish =1 ??????????????????????????????
     * @param userStr    ????????????????????????????????????
     */
    public void creationFileDownload(String pathName, int mediaId, int isNewFile, int onlyFinish, String userStr) {
//        if (downloadingFiles.contains(mediaId)) {
//            ToastUtils.showShort(R.string.file_downloading);
//            return;
//        }
        InterfaceDownload.pbui_Type_DownloadStart build = InterfaceDownload.pbui_Type_DownloadStart.newBuilder()
                .setMediaid(mediaId)
                .setNewfile(isNewFile)
                .setOnlyfinish(onlyFinish)
                .setPathname(s2b(pathName))
                .setUserstr(s2b(userStr)).build();
        LogUtils.e(TAG, "creationFileDownload:   --->>> mediaId=" + mediaId + ", ??????=" + pathName + ", userStr=" + userStr);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD_VALUE, build.toByteArray());
        LogUtils.e(TAG, "creationFileDownload:  ???????????????????????? --->>>pathName=  " + pathName);
//        downloadingFiles.add(mediaId);
    }

    /**
     * ???????????????????????????14???
     */
    public InterfaceDevice.pbui_Type_DeviceFaceShowDetail queryDeviceMeetInfo() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            LogUtils.i(TAG, "queryDeviceMeetInfo:  ????????????????????????--->>> ??????");
            try {
                return InterfaceDevice.pbui_Type_DeviceFaceShowDetail.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryDeviceMeetInfo:  ???????????????????????? --->>> ??????");
        return null;

    }

    /**
     * ?????????ID???????????????????????????
     *
     * @param propertyid InterfaceMacro.Pb_ContextPropertyID
     */
    public InterfaceContext.pbui_MeetContextInfo queryContextProperty(int propertyid) {
        InterfaceContext.pbui_QueryMeetContextInfo build = InterfaceContext.pbui_QueryMeetContextInfo.newBuilder()
                .setPropertyid(propertyid)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCONTEXT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY_VALUE, build.toByteArray());
        if (array != null) {
            try {
                InterfaceContext.pbui_MeetContextInfo pbui_meetContextInfo = InterfaceContext.pbui_MeetContextInfo.parseFrom(array);
                LogUtils.i(TAG, "queryContextProperty ?????????ID??????????????????????????? ?????? --->>> propertyid=" + propertyid);
                return pbui_meetContextInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryContextProperty ?????????ID??????????????????????????? ?????? --->>> propertyid+" + propertyid);
        return null;
    }

    /**
     * 207.????????????
     *
     * @param memberid ???????????????ID,???0???????????????????????????
     * @param signType ????????????
     * @param pwd      ??????
     * @param picdata  ??????????????????
     */
    public void sendSign(int memberid, int signType, String pwd, ByteString picdata) {
        InterfaceSignin.pbui_Type_DoMeetSignIno.Builder builder = InterfaceSignin.pbui_Type_DoMeetSignIno.newBuilder();
        builder.setMemberid(memberid);
        builder.setSigninType(signType);
        builder.setPassword(s2b(pwd));
        builder.setPsigndata(picdata);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), builder.build().toByteArray());
        LogUtils.e(TAG, "sendSign:  ???????????? --->>> signType: " + signType);
    }


    /**
     * 223.??????????????????
     *
     * @return
     */
    public void whiteBoardDeleteRecord(int memberid, int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype) {
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.Builder builder = InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.newBuilder();
        builder.setMemberid(memberid);
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "whiteBoardDeleteRecord:  ?????????????????? --->>> ");
    }

    /**
     * ????????????
     *
     * @return
     */
    public void addInk(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp,
                       int figuretype, int linesize, int argb, List<PointF> allpinklist) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        builder.setLinesize(linesize);
        builder.setArgb(argb);
        for (int i = 0; i < allpinklist.size(); i++) {
            builder.addPinklist(allpinklist.get(i).x);
            builder.addPinklist(allpinklist.get(i).y);
        }
        LogUtils.e(TAG, "addInk   ?????????xy??????--->>> " + builder.getPinklistCount());
//        builder.addAllPinklist(allpinklist);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDINK.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addInk:  ???????????? --->>> ");
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public void addDrawFigure(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp,
                              int type, int size, int color, List<Float> allpt) {
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(type);
        builder.setLinesize(size);
        builder.setArgb(color);
        builder.addAllPt(allpt);
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDRECT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addDrawFigure:  ?????????????????????????????? --->>> ");
    }


    /**
     * ????????????
     *
     * @param operid       ??????ID ?????????????????? ???0????????????????????????opermemberid???????????????
     * @param opermemberid ????????????????????????ID ???0????????????????????????opermemberid???????????????
     * @param srcmemid     ??????????????????ID ??????????????????
     * @param srcwbid      ???????????????????????? ?????????????????????????????? ??????????????????
     * @param utcstamp     ?????????  ??????
     * @param figuretype   ???????????? ???0????????????????????????figuretype???????????????
     * @param fontsize     ????????????
     * @param fontflag     ??????????????????
     * @param argb         ????????????
     * @param fontname     ????????????
     * @param lx           (lx,ly,???????????????)
     * @param ly           (lx,ly,???????????????)
     * @param ptext        ??????
     */
    public void addText(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype, int fontsize, int fontflag,
                        int argb, String fontname, float lx, float ly, String ptext) {
        InterfaceWhiteboard.pbui_Item_MeetWBTextDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBTextDetail.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        builder.setFontsize(fontsize);
        builder.setFontflag(fontflag);
        builder.setArgb(argb);
        builder.setFontname(s2b(fontname));
        builder.setLx(lx);
        builder.setLy(ly);
        builder.setPtext(s2b(ptext));
        InterfaceWhiteboard.pbui_Item_MeetWBTextDetail build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDTEXT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addText:  ???????????? --->>> " + ptext);
    }

    /**
     * ????????????
     *
     * @return
     */
    public void agreeJoin(int opermemberid, int srcmemid, long srcwbid) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.newBuilder();
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setOpermemberid(opermemberid);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "agreeJoin :  ???????????? --> ");
    }

    /**
     * ????????????
     *
     * @return
     */
    public void rejectJoin(int opermemberid, int srcmemid, long srcwbid) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.newBuilder();
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REJECT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "rejectJoin:  ???????????? --->>> ");
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public void broadcastStopWhiteBoard(int operflag, String medianame, int opermemberid, int srcmemid, long srcwbid, List<Integer> alluserid) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        builder.setOperflag(operflag);
        builder.setMedianame(s2b(medianame));
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.addAllUserid(alluserid);
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "inquiryStartWhiteBoard:  ???????????????????????? --->>> ");
    }

    /**
     * ????????????
     *
     * @return
     */
    public void coerceStartWhiteBoard(int operFlag, String mediaName, int operMemberid, int srcmemId, long srcwbId, List<Integer> allUserId) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder tmp3 = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        tmp3.setOperflag(operFlag);
        tmp3.setMedianame(s2b(mediaName));
        tmp3.setOpermemberid(operMemberid);
        tmp3.setSrcmemid(srcmemId);
        tmp3.setSrcwbid(srcwbId);
        tmp3.addAllUserid(allUserId);
        tmp3.setOperflag(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber());
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = tmp3.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "coerceStartBoard:  ???????????? --->>> ");
    }

    /**
     * ????????????
     *
     * @return
     */
    public void addPicture(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype, float lx, float ly, ByteString picdata) {
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        builder.setLx(lx);
        builder.setLy(ly);
        builder.setPicdata(picdata);
        LogUtils.e(TAG, "addPicture :   --->>> " + figuretype);
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDPICTURE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addPicture:  ?????????????????? --->>> ");
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public void whiteBoardClearRecord(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp, int figuretype) {
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.Builder builder = InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(figuretype);
        InterfaceWhiteboard.pbui_Type_MeetDoClearWhiteBoard build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DELALL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "whiteBoardClearInform:  222.?????????????????? --->>> ");
    }

    /**
     * ??????????????????
     */
    public InterfaceMember.pbui_Type_MemberDetailInfo queryMember() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                InterfaceMember.pbui_Type_MemberDetailInfo pbui_type_memberDetailInfo = InterfaceMember.pbui_Type_MemberDetailInfo.parseFrom(array);
                LogUtils.i(TAG, "queryAttendPeopleFromId:  ???????????????????????? --->>> ");
                return pbui_type_memberDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAttendPeople:  ???????????????????????? --->>> ");
        return null;
    }

    /**
     * ??????????????????????????????
     */
    public InterfaceMember.pbui_Type_MeetMemberDetailInfo queryMemberDetailed() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DETAILINFO.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMemberDetailed :  ???????????????????????????????????? --> ");
                return InterfaceMember.pbui_Type_MeetMemberDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMemberDetailed :  ???????????????????????????????????? --> ");
        return null;
    }

    /**
     * ??????????????????
     */
    public InterfaceRoom.pbui_Type_MeetSeatDetailInfo queryMeetRanking() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetRanking :  ???????????????????????? --> ");
                return InterfaceRoom.pbui_Type_MeetSeatDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetRanking :  ???????????????????????? --> ");
        return null;
    }

    /**
     * ??????????????????
     */
    public void modifyMeetRanking(int nameid, int role, int seatid) {
        InterfaceRoom.pbui_Item_MeetSeatDetailInfo.Builder builder1 = InterfaceRoom.pbui_Item_MeetSeatDetailInfo.newBuilder();
        builder1.setNameId(nameid);
        builder1.setSeatid(seatid);
        builder1.setRole(role);
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo.Builder builder = InterfaceRoom.pbui_Type_MeetSeatDetailInfo.newBuilder();
        builder.addItem(builder1);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY_VALUE, builder.build().toByteArray());
        LogUtils.e(TAG, "modifMeetRanking:  ?????????????????? --->>>nameid: " + nameid + ", role:  " + role + " , devid: " + seatid);
    }


    /**
     * ??????????????????
     */
    public void addAttendPeople(InterfaceMember.pbui_Item_MemberDetailInfo info) {
        InterfaceMember.pbui_Type_MemberDetailInfo build = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder()
                .addItem(info).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addAttendPeople:  ?????????????????? --->>> ");
    }


    /**
     * ????????????????????????
     *
     * @param type ??????????????????
     */
    public void cacheData(int type) {
        InterfaceBase.pbui_MeetCacheOper build = InterfaceBase.pbui_MeetCacheOper.newBuilder()
                .setCacheflag(InterfaceMacro.Pb_CacheFlag.Pb_MEET_CACEH_FLAG_ZERO.getNumber())
                .setId(1)
                .build();
        jni.call_method(type, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CACHE_VALUE, build.toByteArray());
    }


    /**
     * ?????????????????????
     *
     * @param resid
     * @param w
     * @param h
     * @return
     */
    public void initVideoRes(int resid, int w, int h) {
        InterfacePlaymedia.pbui_Type_MeetInitPlayRes.Builder builder = InterfacePlaymedia.pbui_Type_MeetInitPlayRes.newBuilder();
        builder.setRes(resid);
        builder.setY(0);
        builder.setX(0);
        builder.setW(w);
        builder.setH(h);
        InterfacePlaymedia.pbui_Type_MeetInitPlayRes build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_INIT.getNumber(), build.toByteArray());
        LogUtils.d(TAG, "initVideoRes:  ????????????????????? --->>> " + w + "," + h);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public void releaseVideoRes(int resValue) {
        InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes.Builder builder = InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes.newBuilder();
        builder.setRes(resValue);
        InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DESTORY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "releaseVideoRes :  ?????????????????? --->>> resValue= " + resValue);
    }


    /**
     * ?????????ID????????????????????????
     *
     * @param propetyid InterfaceMacro.Pb_MeetDevicePropertyID
     * @param devId     =0?????????
     * @return pbui_DeviceInt32uProperty??????????????????????????????????????????=0?????????=1??????
     * pbui_DeviceStringProperty???????????????
     */
    public byte[] queryDevicePropertiesById(int propetyid, int devId) {
        InterfaceDevice.pbui_MeetDeviceQueryProperty.Builder builder = InterfaceDevice.pbui_MeetDeviceQueryProperty.newBuilder();
        builder.setPropertyid(propetyid);
        builder.setDeviceid(devId);
        builder.setParamterval(0);
        InterfaceDevice.pbui_MeetDeviceQueryProperty build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), bytes);
        if (array == null) {
            LogUtils.e(TAG, "queryDevicePropertiesById :  ?????????ID?????????????????????????????? --->>> ");
            return null;
        }
        LogUtils.e(TAG, "queryDevicePropertiesById:  ?????????ID?????????????????????????????? --->>> ");
        return array;
    }

    /**
     * ????????????????????????(???????????????????????????)
     *
     * @param propertyid ??????ID
     */
    public InterfaceBase.pbui_CommonInt32uProperty queryMeetRankingProperty(int propertyid) {
        InterfaceBase.pbui_CommonQueryProperty build = InterfaceBase.pbui_CommonQueryProperty.newBuilder()
                .setPropertyid(propertyid)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY_VALUE, build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetRankingProperty :  ???????????????????????? ?????? --> ");
                return InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetRankingProperty :  ???????????????????????? ?????? --> ");
        return null;
    }

    /**
     * ??????????????????
     */
    public InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo queryMeetFunction() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FUNCONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetFunction :  ???????????????????????? --> ");
                return InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetFunction :  ???????????????????????? --> ");
        return null;
    }

    /**
     * ????????????
     */
    public InterfaceAgenda.pbui_meetAgenda queryAgenda() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryAgenda :  ?????????????????? --> ");
                return InterfaceAgenda.pbui_meetAgenda.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAgenda :  ?????????????????? --> ");
        return null;
    }

    /**
     * 149.??????????????????
     *
     * @return
     */
    public byte[] queryFileProperty(int propertyid, int parmeterval) {
        InterfaceBase.pbui_CommonQueryProperty.Builder builder = InterfaceBase.pbui_CommonQueryProperty.newBuilder();
        builder.setPropertyid(propertyid);
        builder.setParameterval(parmeterval);
        InterfaceBase.pbui_CommonQueryProperty build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            LogUtils.e(TAG, "queryFileProperty ?????????????????? ??????");
            return new byte[0];
        }
        LogUtils.i(TAG, "queryFileProperty ?????????????????? ??????");
        return array;
    }


    /**
     * ??????????????????
     */
    public InterfaceFile.pbui_Type_MeetDirDetailInfo queryMeetDir() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryMeetDir :  ???????????????????????? --> ");
                return InterfaceFile.pbui_Type_MeetDirDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetDir :  ???????????????????????? --> ");
        return null;
    }

    /**
     * 143.????????????????????????
     */
    public InterfaceFile.pbui_Type_MeetDirFileDetailInfo queryMeetDirFile(int dirId) {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(dirId);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array != null) {
            LogUtils.e(TAG, "queryMeetDirFile :  ?????????????????????????????? --> " + dirId);
            try {
                return InterfaceFile.pbui_Type_MeetDirFileDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetDirFile :  ?????????????????????????????? --> " + dirId);
        return null;
    }

    /**
     * ????????????
     *
     * @param uploadflag ???????????? ?????? Pb_Upload_Flag =1????????????????????????????????????
     * @param dirid      ???????????????ID ?????? Pb_Upload_DefaultDirId
     * @param attrib     ???????????? ?????? Pb_MeetFileAttrib
     * @param newname    ?????????????????????
     * @param pathname   ????????????
     * @param userval    ?????????????????????
     * @param userStr    ??????????????????????????????
     */
    public void uploadFile(int uploadflag, int dirid, int attrib, String newname, String pathname, int userval, String userStr) {
        InterfaceUpload.pbui_Type_AddUploadFile.Builder builder = InterfaceUpload.pbui_Type_AddUploadFile.newBuilder();
        builder.setUploadflag(uploadflag);
        builder.setDirid(dirid);
        builder.setAttrib(attrib);
        builder.setNewname(s2b(newname));
        builder.setPathname(s2b(pathname));
        builder.setUserval(userval);
        builder.setUserstr(s2b(userStr));
        InterfaceUpload.pbui_Type_AddUploadFile build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "uploadFile :   --> ???????????? " + newname + ", dirid= " + dirid + ", pathname= " + pathname);
    }


    /**
     * ??????????????????
     *
     * @param mediaid
     * @param devIds
     * @param pos
     * @param res
     * @param triggeruserval Pb_TriggerUsedef
     * @param flag           Pb_MeetPlayFlag
     */
    public void mediaPlayOperate(int mediaid, List<Integer> devIds, int pos, int res, int triggeruserval, int flag) {
        InterfacePlaymedia.pbui_Type_MeetDoMediaPlay build = InterfacePlaymedia.pbui_Type_MeetDoMediaPlay.newBuilder()
                .setPlayflag(flag)
                .setPos(pos)
                .setMediaid(mediaid)
                .setTriggeruserval(triggeruserval)
                .addRes(res)
                .addAllDeviceid(devIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START_VALUE, build.toByteArray());
        LogUtils.e(TAG, "mediaPlayOperate  ??????????????????");
    }

    /**
     * ????????????????????????
     */
    public InterfaceMember.pbui_Type_MemberPermission queryAttendPeoplePermissions() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryAttendPeoplePermissions :  ?????????????????????????????? --> ");
                return InterfaceMember.pbui_Type_MemberPermission.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAttendPeoplePermissions :  ?????????????????????????????? --> ");
        return null;
    }

    /**
     * ???????????????????????????
     *
     * @param devid     ??????id
     * @param privilege ????????? InterfaceMacro.Pb_MemberPermissionPropertyID
     */
    public void applyPermission(int devid, int privilege) {
        InterfaceDevice.pbui_Type_MeetRequestPrivilege.Builder builder = InterfaceDevice.pbui_Type_MeetRequestPrivilege.newBuilder();
        builder.addDevid(devid);
        builder.setPrivilege(privilege);
        InterfaceDevice.pbui_Type_MeetRequestPrivilege build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTPRIVELIGE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "applyPermission:  ???????????????????????????????????? --->>> ?????????????????? " + privilege);
    }

    /**
     * ????????????
     */
    public InterfaceBase.pbui_meetUrl queryWebUrl() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryWebUrl  ???????????? --->>> ??????");
                return InterfaceBase.pbui_meetUrl.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryWebUrl  ???????????? --->>> ??????");
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @param id ?????????ID
     */
    public InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo placeDeviceRankingInfo(int id) {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(id);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DETAILINFO_VALUE, build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "placeDeviceRankingInfo :  ?????????????????????????????????????????? --> id=" + id);
                return InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "placeDeviceRankingInfo :  ?????????????????????????????????????????? --> id=" + id);
        return null;
    }

    /**
     * ????????????
     */
    public InterfaceSignin.pbui_Type_MeetSignInDetailInfo querySignin() throws InvalidProtocolBufferException {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            LogUtils.e(TAG, "querySignin :  ?????????????????? --> ");
            return null;
        }
        LogUtils.e(TAG, "querySignin :  ?????????????????? --> ");
        return InterfaceSignin.pbui_Type_MeetSignInDetailInfo.parseFrom(array);
    }

    /**
     * ??????????????????ID?????????ID
     *
     * @param roomid ??????ID
     * @return
     * @throws InvalidProtocolBufferException
     */
    public int queryMeetRoomProperty(int roomid) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_CommonQueryProperty build = InterfaceBase.pbui_CommonQueryProperty.newBuilder()
                .setParameterval(roomid)
                .setParameterval2(roomid)
                .setPropertyid(InterfaceMacro.Pb_MeetRoomPropertyID.Pb_MEETROOM_PROPERTY_BGPHOTOID.getNumber())
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        int propertyval = 0;
        if (bytes != null) {
            InterfaceBase.pbui_CommonInt32uProperty pbui_commonInt32uProperty = InterfaceBase.pbui_CommonInt32uProperty.parseFrom(bytes);
            propertyval = pbui_commonInt32uProperty.getPropertyval();
        }
        LogUtils.e(TAG, "queryMeetRoomProperty :  ??????????????????ID?????????ID --> " + roomid + ",  propertyval:" + propertyval);
        return propertyval;
    }


    /**
     * ??????????????????
     */
    public InterfaceDevice.pbui_Type_DeviceDetailInfo queryDeviceInfo() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryDeviceInfo :  ???????????????????????? --> ");
                return InterfaceDevice.pbui_Type_DeviceDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryDeviceInfo :  ???????????????????????? --> ");
        return null;
    }

    /**
     * ??????????????????????????????
     */
    public InterfaceIM.pbui_Type_MeetIMDetailInfo queryAllChatMessage() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                LogUtils.i(TAG, "queryAllChatMessage ????????????????????????????????????");
                return InterfaceIM.pbui_Type_MeetIMDetailInfo.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryAllChatMessage ????????????????????????????????????");
        return null;
    }

    /**
     * ???????????????????????????
     *
     * @param memberId ??????id
     */
    public InterfaceIM.pbui_TypePageResQueryrMsgInfo queryMessageByMemberId(int memberId) {
        InterfaceIM.pbui_Type_MeetComplexQueryIM build = InterfaceIM.pbui_Type_MeetComplexQueryIM.newBuilder()
                .setQueryflag(InterfaceMacro.Pb_MeetIMMSG_QueryFlag.Pb_COMPLEXQUERY_MEMBERID_VALUE |
                        InterfaceMacro.Pb_MeetIMMSG_QueryFlag.Pb_COMPLEXQUERY_MSGTYPE_VALUE)
                .setMsgtype(0)
                .setMemberid(memberId)
                .setPageindex(0)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXQUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.i(TAG, "queryMessageByMemberId ????????????????????????????????? " + memberId);
                return InterfaceIM.pbui_TypePageResQueryrMsgInfo.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMessageByMemberId ????????????????????????????????? " + memberId);
        return null;
    }

    /**
     * 185.????????????????????????
     *
     * @return
     */
    public void sendChatMessage(String msg, int msgType, List<Integer> ids) {
        InterfaceIM.pbui_Type_SendMeetIM.Builder builder = InterfaceIM.pbui_Type_SendMeetIM.newBuilder();
        builder.setMsg(s2b(msg));
        builder.setMsgtype(msgType);
        builder.addAllUserids(ids);
        InterfaceIM.pbui_Type_SendMeetIM build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SEND.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "sendMeetInfo:  ???????????????????????? --->>> ");
    }

    /**
     * 62.??????????????????
     *
     * @param oper enum Pb_DeviceControlFlag
     * @return
     */
    public void executeTerminalControl(int oper, int operval1, int operval2, List<Integer> devids) {
        InterfaceDevice.pbui_Type_DeviceOperControl.Builder builder = InterfaceDevice.pbui_Type_DeviceOperControl.newBuilder();
        builder.setOper(oper);
        builder.setOperval1(operval1);
        builder.setOperval2(operval2);
        builder.addAllDevid(devids);
        InterfaceDevice.pbui_Type_DeviceOperControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICECONTROL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "executeTerminalControl:  ?????????????????? --->>> oper= " + oper);
    }

    /**
     * ??????????????????
     *
     * @param ids ??????id
     */
    public void wakeOnLan(List<Integer> ids) {
        InterfaceDevice.pbui_Type_MeetDoNetReboot build = InterfaceDevice.pbui_Type_MeetDoNetReboot.newBuilder()
                .addAllDevid(ids)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REBOOT_VALUE, build.toByteArray());
    }

    /**
     * ??????????????????
     *
     * @param devids
     */
    public void assistedSignIn(List<Integer> devids) {
        InterfaceDevice.pbui_MeetDoEnterMeet.Builder builder = InterfaceDevice.pbui_MeetDoEnterMeet.newBuilder();
        builder.addAllDevid(devids);
        InterfaceDevice.pbui_MeetDoEnterMeet build = builder.build();
        byte[] bytes = build.toByteArray();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber(), bytes);
        LogUtils.e(TAG, "signAlterationOperate:  ?????????????????? --->>> " + devids);
    }


    /**
     * 200.????????????
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public InterfaceVote.pbui_Type_MeetVoteDetailInfo queryVote() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryVote :  ?????????????????? --> ");
                return InterfaceVote.pbui_Type_MeetVoteDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryVote :  ?????????????????? --> ");
        return null;
    }

    public InterfaceVote.pbui_Type_MeetVoteDetailInfo queryVoteByType(int voteType) {
        InterfaceVote.pbui_Type_MeetVoteComplexQuery build = InterfaceVote.pbui_Type_MeetVoteComplexQuery.newBuilder()
                .setMaintype(voteType).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXQUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.d(TAG, "queryVoteByType -->" + "?????????????????????????????????" + voteType);
                return InterfaceVote.pbui_Type_MeetVoteDetailInfo.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }
        LogUtils.d(TAG, "queryVoteByType -->" + "?????????????????????????????????" + voteType);
        return null;
    }


    /**
     * ????????????
     *
     * @param memberIds ?????????ID
     * @param voteid    ??????ID
     * @param seconds   ?????????
     */
    public void launchVote(List<Integer> memberIds, int voteid, int seconds) {
        InterfaceVote.pbui_ItemVoteStart.Builder b = InterfaceVote.pbui_ItemVoteStart.newBuilder();
        b.setVoteid(voteid);
        b.setVoteflag(InterfaceMacro.Pb_VoteStartFlag.Pb_MEET_VOTING_FLAG_AUTOEXIT_VALUE);
        b.setTimeouts(seconds);
        b.addAllMemberid(memberIds);
        InterfaceVote.pbui_Type_MeetStartVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStartVoteInfo.newBuilder();
        builder.addItem(b);
        InterfaceVote.pbui_Type_MeetStartVoteInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "launchVote:  ???????????? --->>> ");
    }

    /**
     * ????????????
     */
    public void deleteVote(Integer voteid) {
        InterfaceVote.pbui_Type_MeetStopVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStopVoteInfo.newBuilder();
        builder.addVoteid(voteid);
        InterfaceVote.pbui_Type_MeetStopVoteInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "deleteVote:  ???????????? --->>> ");
    }

    /**
     * ????????????
     */
    public void stopVote(int voteid) {
        InterfaceVote.pbui_Type_MeetStopVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStopVoteInfo.newBuilder();
        builder.addVoteid(voteid);
        InterfaceVote.pbui_Type_MeetStopVoteInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "stopVote:  ???????????? --->>> ");
    }

    /**
     * ??????????????????????????????
     */
    public InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo querySubmittedVoters(int voteId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(voteId).build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTESIGNED.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "querySubmittedVoters :  ???????????????????????????????????? --> ");
                return InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "querySubmittedVoters :  ???????????????????????????????????? --> ");
        return null;
    }

    /**
     * ?????????(????????????)
     *
     * @param sourceDevId    ????????????????????????ID
     * @param subid          2??????????????? 3????????????
     * @param triggeruserval ????????? 0  Pb_TriggerUsedef
     * @param allres         ?????????????????????
     * @param alldeviceid    ????????????????????????????????????????????????
     */
    public void streamPlay(int sourceDevId, int subid, int triggeruserval, List<Integer> allres, List<Integer> alldeviceid) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder()
                .setSrcdeviceid(sourceDevId)
                .setSubid(subid)
                .setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE)
                .setTriggeruserval(triggeruserval)
                .addAllRes(allres)
                .addAllDeviceid(alldeviceid)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "streamPlay:  ????????? --->>> ");
    }

    /**
     * ???????????????????????????
     *
     * @param sourceId ??????id
     */
    public void playTargetScreen(int sourceId) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder()
                .setSrcdeviceid(sourceId)
                .setSubid(SCREEN_SUB_ID)
                .setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE)
                .setTriggeruserval(InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_ZERO_VALUE)
                .addRes(RESOURCE_ID_0)
                .addDeviceid(GlobalValue.localDeviceId)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START_VALUE,
                build.toByteArray());
        LogUtils.i(TAG, "playTargetScreen ??????????????????????????? sourceId=" + sourceId
                + ",GlobalValue.localDeviceId=" + GlobalValue.localDeviceId);
    }

    /**
     * ??????????????????????????????
     *
     * @param sourceId ??????id
     */
    public void playTargetCamera(int sourceId) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder()
                .setSrcdeviceid(sourceId)
                .setSubid(CAMERA_SUB_ID)
                .setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO_VALUE)
                .setTriggeruserval(InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_ZERO_VALUE)
                .addRes(RESOURCE_ID_0)
                .addDeviceid(GlobalValue.localDeviceId)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        LogUtils.i(TAG, "playTargetCamera ?????????????????????????????? sourceId=" + sourceId);
    }

    /**
     * ??????????????????
     */
    public void stopResourceOperate(int res, List<Integer> devIds) {
        InterfaceStop.pbui_Type_MeetDoStopResWork build = InterfaceStop.pbui_Type_MeetDoStopResWork.newBuilder()
                .addRes(res)
                .addAllDeviceid(devIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE.getNumber(), build.toByteArray());
        LogUtils.i(TAG, "stopResourceOperate:  ??????????????????  ---> ");
    }

    /**
     * ??????????????????
     */
    public void stopResourceOperate(List<Integer> res, List<Integer> devIds) {
        InterfaceStop.pbui_Type_MeetDoStopResWork build = InterfaceStop.pbui_Type_MeetDoStopResWork.newBuilder()
                .addAllRes(res)
                .addAllDeviceid(devIds)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE.getNumber(), build.toByteArray());
        LogUtils.i(TAG, "stopResourceOperate:  ??????????????????  ---> ");
    }


    /**
     * ??????????????????
     */
    public void setPlayPause(int resIndex, List<Integer> devIds) {
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoPlayControl.newBuilder();
        builder.setResindex(resIndex);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PAUSE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "setPlayStop:  ?????????????????? --->>> ");
    }

    /**
     * ??????????????????
     */
    public void setPlayRecover(int resIndex, List<Integer> devIds) {
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoPlayControl.newBuilder();
        builder.setResindex(resIndex);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PLAY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "setPlayRecover:  ?????????????????? --->>> ");
    }

    /**
     * ??????????????????
     */
    public void setPlayPlace(int resIndex, int pos, List<Integer> devIds, int triggeruserval, int playflag) {
        InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos.newBuilder();
        builder.setResindex(resIndex);
        builder.setPos(pos);
        builder.addAllDeviceid(devIds);
        builder.setTriggeruserval(triggeruserval);
        builder.setPlayflag(playflag);
        InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MOVE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "setPlayPlace:  ?????????????????? --->>> ");
    }


    /**
     * ????????????
     *
     * @param item
     */
    public void addNotice(InterfaceBullet.pbui_Item_BulletDetailInfo item) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "addNotice:  ???????????? --->>> ");
    }

    /**
     * ????????????
     */
    public InterfaceBullet.pbui_BulletDetailInfo queryBullet() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryBullet:  ?????????????????? --->>> ");
                return InterfaceBullet.pbui_BulletDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryBullet:  ?????????????????? --->>> ");
        return null;
    }

    /**
     * ????????????
     *
     * @param bullet ??????
     */
    public void launchBullet(InterfaceBullet.pbui_Item_BulletDetailInfo bullet) {
        InterfaceBullet.pbui_Type_MeetPublishBulletInfo build = InterfaceBullet.pbui_Type_MeetPublishBulletInfo.newBuilder()
                .setItem(bullet)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PUBLIST_VALUE, build.toByteArray());
        LogUtils.e(TAG, "launchBullet:  ???????????? --->>> ");
    }

    /**
     * ????????????
     *
     * @param item
     */
    public void modifyBullet(InterfaceBullet.pbui_Item_BulletDetailInfo item) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "modifyBullet:  ???????????? --->>> ");
    }

    /**
     * ????????????
     *
     * @param item
     */
    public void deleteBullet(InterfaceBullet.pbui_Item_BulletDetailInfo item) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "deleteBullet:  ???????????? --->>> ");
    }


    /**
     * ??????????????????????????????
     */
    public InterfaceDevice.pbui_Type_DeviceResPlay queryCanJoin() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESINFO.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryCanJoin  ???????????????????????????????????? --->>> ");
                return InterfaceDevice.pbui_Type_DeviceResPlay.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryCanJoin  ???????????????????????????????????? --->>> ");
        return null;
    }


    /**
     * 189.?????????????????????
     */
    public InterfaceVote.pbui_Type_MeetOnVotingDetailInfo queryInitiateVote() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryInitiateVote:  ??????????????????????????? --->>> ");
                return InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryInitiateVote:  ??????????????????????????? --->>> ");
        return null;
    }

    /**
     * ????????????id?????????
     */
    public InterfaceVote.pbui_Type_MeetOnVotingDetailInfo queryVoteById(int voteId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(voteId)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array != null) {
            try {
                LogUtils.e(TAG, "queryVoteById:  ????????????id???????????????");
                return InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryVoteById:  ????????????id???????????????");
        return null;
    }


    /**
     * ??????????????????
     *
     * @param devid ???????????????ID
     */
    public void stopDeviceIntercom(int devid) {
        InterfaceDevice.pbui_Type_DoExitDeviceChat build = InterfaceDevice.pbui_Type_DoExitDeviceChat.newBuilder()
                .setOperdeviceid(devid)
                .build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_EXITCHAT_VALUE, build.toByteArray());
    }

    /**
     * ????????????
     *
     * @param flag Interface_device.Pb_DeviceInviteFlag
     */
    public void deviceIntercom(List<Integer> devids, int flag) {
        InterfaceDevice.pbui_Type_DoDeviceChat build = InterfaceDevice.pbui_Type_DoDeviceChat.newBuilder()
                .addAllDevid(devids)
                .setInviteflag(flag)
                .build();
        LogUtils.d(TAG, "deviceIntercom -->" + "???????????? flag = " + flag);
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTINVITE_VALUE, build.toByteArray());
    }

    /**
     * ??????????????????
     *
     * @param devid ???????????????
     * @param flag  Interface_device.Pb_DeviceInviteFlag  =1?????????=0??????
     *              Pb_DEVICE_INVITECHAT_FLAG_DEAL
     */
    public void replyDeviceIntercom(int devid, int flag) {
        InterfaceDevice.pbui_Type_DeviceChat build = InterfaceDevice.pbui_Type_DeviceChat.newBuilder()
                .setOperdeviceid(devid)
                .setInviteflag(flag)
                .build();
        LogUtils.d(TAG, "deviceIntercom -->" + "??????????????????");
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEINVITE_VALUE, build.toByteArray());
    }


    /**
     * ??????????????????????????????
     *
     * @param devid      ??????????????????
     * @param returncode 1=??????,0=?????????
     */
    public void revertAttendPermissionsRequest(int devid, int returncode) {
        InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege.Builder builder = InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege.newBuilder();
        builder.addDevid(devid);
        builder.setReturncode(returncode);
        InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "revertPermissionsRequest:    ?????????????????????????????? --->>> ");
    }


    /**
     * ??????????????????
     *
     * @param selcnt  ???????????????
     * @param voteid  ??????ID
     * @param selitem ???????????????????????????????????????
     */
    public void submitVoteResult(int selcnt, int voteid, int selitem) {
        InterfaceVote.pbui_Item_MeetSubmitVote.Builder builder1 = InterfaceVote.pbui_Item_MeetSubmitVote.newBuilder();
        builder1.setSelcnt(selcnt);
        builder1.setVoteid(voteid);
        builder1.setSelitem(selitem);
        InterfaceVote.pbui_Type_MeetSubmitVote.Builder builder = InterfaceVote.pbui_Type_MeetSubmitVote.newBuilder();
        builder.addItem(builder1);
        InterfaceVote.pbui_Type_MeetSubmitVote build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SUBMIT.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "submitVoteResult:  ?????????????????? --->>> ");
    }


    /**
     * 172.??????????????????
     */
    public InterfaceVideo.pbui_Type_MeetVideoDetailInfo queryMeetVideo() {
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array != null) {
            try {
                InterfaceVideo.pbui_Type_MeetVideoDetailInfo info = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.parseFrom(array);
                LogUtils.e(TAG, "queryMeetVedio :  ???????????????????????? --> ");
                return info;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMeetVedio :  ???????????????????????? --> ");
        return null;
    }

    /**
     * 191.??????????????????
     *
     * @return
     */
    public void createVote(InterfaceVote.pbui_Item_MeetOnVotingDetailInfo vote) {
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.Builder builder1 = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.newBuilder();
        builder1.addItem(vote);
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo build = builder1.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "createVote:  ?????????????????? --->>> ");
    }

    /**
     * ????????????id?????????
     *
     * @param bulletid ??????id
     */
    public InterfaceBullet.pbui_BulletDetailInfo queryBulletById(int bulletid) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(bulletid)
                .build();
        byte[] array = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            try {
                LogUtils.e(TAG, "queryBulletById :  ??????????????????????????? --> ");
                return InterfaceBullet.pbui_BulletDetailInfo.parseFrom(array);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryBulletById :  ??????????????????????????? --> ");
        return null;
    }

    /**
     * 192.??????????????????
     *
     * @return
     */
    public void modifyVote(InterfaceVote.pbui_Item_MeetOnVotingDetailInfo item) {
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.Builder builder = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.newBuilder();
        builder.addItem(item);
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo build = builder.build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        LogUtils.e(TAG, "modifyVote:  ?????????????????? --->>> ");
    }



    /**
     * ????????????????????????
     *
     * @param propertyid   ??????ID ?????? Pb_MemberPropertyID
     * @param parameterval ???????????? ???0??????????????????????????????id
     * @return
     */
    public InterfaceMember.pbui_Type_MeetMembeProperty queryMemberProperty(int propertyid, int parameterval) {
        InterfaceMember.pbui_Type_MeetMemberQueryProperty build = InterfaceMember.pbui_Type_MeetMemberQueryProperty.newBuilder()
                .setPropertyid(propertyid)
                .setParameterval(parameterval)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(),
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.d(TAG, "queryMemberProperty -->" + "??????????????????????????????");
                return InterfaceMember.pbui_Type_MeetMembeProperty.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryMemberProperty -->" + "??????????????????????????????");
        return null;
    }


    /**
     * ????????????ID???????????????
     *
     * @param mediaId ??????id
     */
    public String queryFileNameByMediaId(int mediaId) {
        String fileName = "";
        byte[] bytes = queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaId);
        try {
            InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
            fileName = pbui_commonTextProperty.getPropertyval().toStringUtf8();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "queryFileNameByMediaId ????????????ID???????????????=" + fileName);
        return fileName;
    }

    //*************************************************************** ?????????????????? **************************************************************

    /**
     * ????????????????????????
     *
     * @return
     */
    public InterfaceFilescorevote.pbui_Type_UserDefineFileScore queryFileScore() {
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, null);
        if (bytes != null) {
            try {
                return InterfaceFilescorevote.pbui_Type_UserDefineFileScore.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * ????????????id???????????????
     *
     * @param id ??????id
     * @return InterfaceFilescorevote.pbui_Type_UserDefineFileScore
     */
    public InterfaceFilescorevote.pbui_Type_UserDefineFileScore queryFileScoreById(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(id).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceFilescorevote.pbui_Type_UserDefineFileScore pbui_type_userDefineFileScore = InterfaceFilescorevote.pbui_Type_UserDefineFileScore.parseFrom(bytes);
                LogUtils.e(TAG, "????????????id??????????????? ??????");
                return pbui_type_userDefineFileScore;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "????????????id??????????????? ??????");
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @param voteid
     * @return
     */
    public InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatistic queryScoreSubmittedScore(int voteid) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(voteid).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                LogUtils.e(TAG, "queryScoreSubmittedScore -->" + "?????????????????????????????????");
                return InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatistic.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "queryScoreSubmittedScore -->" + "?????????????????????????????????");
        return null;
    }

    /**
     * ????????????
     *
     * @param voteid    ??????ID
     * @param voteflag  Pb_VoteStartFlag
     * @param timeouts
     * @param memberIds ??????ID
     */
    public void startScore(int voteid, int voteflag, int timeouts, List<Integer> memberIds) {
        InterfaceFilescorevote.pbui_Type_StartUserDefineFileScore build = InterfaceFilescorevote.pbui_Type_StartUserDefineFileScore.newBuilder()
                .addAllMemberid(memberIds)
                .setTimeouts(timeouts)
                .setVoteflag(voteflag)
                .setVoteid(voteid)
                .build();
        LogUtils.d(TAG, "startScore -->" + "???????????????votid= " + voteid + "???memberIds: " + memberIds.toString());
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START_VALUE, build.toByteArray());
    }

    /**
     * ????????????
     *
     * @param voteid
     */
    public void stopScore(int voteid) {
        InterfaceFilescorevote.pbui_Type_DeleteUserDefineFileScore build = InterfaceFilescorevote.pbui_Type_DeleteUserDefineFileScore.newBuilder()
                .addVoteid(voteid).build();
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP_VALUE, build.toByteArray());
    }

    /**
     * ??????????????????
     *
     * @param voteid
     * @param memberid
     * @param opinion  ????????????
     * @param allscore ????????????
     */
    public void submitScore(int voteid, int memberid, String opinion, List<Integer> allscore) {
        InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatisticNotify build = InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatisticNotify.newBuilder()
                .setContent(s2b(opinion))
                .setMemberid(memberid)
                .addAllScore(allscore)
                .setVoteid(voteid).build();
        LogUtils.d(TAG, "submitScore -->" + "??????????????????");
        jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SUBMIT_VALUE, build.toByteArray());
    }

    /**
     * ????????????id?????????
     *
     * @param roomId ??????id
     * @return
     */
    public InterfaceRoom.pbui_Type_MeetRoomDetailInfo queryRoomById(int roomId) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder()
                .setId(roomId).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceRoom.pbui_Type_MeetRoomDetailInfo pbui_type_meetRoomDetailInfo = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.parseFrom(bytes);
                LogUtils.d(TAG, "????????????id????????? ??????");
                return pbui_type_meetRoomDetailInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "????????????id????????? ??????");
        return null;
    }


    /**
     * ??????????????????
     *
     * @param propertyid Pb_MeetPropertyID
     * @param val1
     * @param val2
     * @return pbui_CommonInt32uProperty\pbui_CommonInt64uProperty
     */
    public byte[] queryMeetingProperty(int propertyid, int val1, int val2) {
        InterfaceBase.pbui_CommonQueryProperty build = InterfaceBase.pbui_CommonQueryProperty.newBuilder()
                .setPropertyid(propertyid)
                .setParameterval(val1)
                .setParameterval2(val2)
                .build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE,
                InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY_VALUE, build.toByteArray());
        if (bytes == null) {
            LogUtils.e(TAG, "queryMeetingProperty -->" + "???????????????????????? propertyid=" + propertyid + ", val1=" + val1 + ", val2=" + val2);
            return null;
        }
        LogUtils.d(TAG, "queryMeetingProperty -->" + "???????????????????????? propertyid=" + propertyid + ", val1=" + val1 + ", val2=" + val2);
        return bytes;
    }
    /**
     * ????????????id?????????
     *
     * @param id ??????id
     * @return ????????????
     */
    public InterfaceMeet.pbui_Type_MeetMeetInfo queryMeetingById(int id) {
        InterfaceBase.pbui_QueryInfoByID build = InterfaceBase.pbui_QueryInfoByID.newBuilder().setId(id).build();
        byte[] bytes = jni.call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE, InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID_VALUE, build.toByteArray());
        if (bytes != null) {
            try {
                InterfaceMeet.pbui_Type_MeetMeetInfo pbui_type_meetMeetInfo = InterfaceMeet.pbui_Type_MeetMeetInfo.parseFrom(bytes);
                LogUtils.i(TAG, "????????????id????????? ?????? id=" + id);
                return pbui_type_meetMeetInfo;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e(TAG, "????????????id????????? ?????? id=" + id);
        return null;
    }


    /**
     * ???????????????????????????????????????
     * @return ????????????
     */
    public boolean isOnline() {
        boolean isonline = false;
        byte[] bytes = queryDevicePropertiesById(InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_NETSTATUS_VALUE,
                GlobalValue.localDeviceId);
        if (bytes != null) {
            InterfaceDevice.pbui_DeviceInt32uProperty pbui_deviceInt32uProperty = null;
            try {
                pbui_deviceInt32uProperty = InterfaceDevice.pbui_DeviceInt32uProperty.parseFrom(bytes);
                int propertyval = pbui_deviceInt32uProperty.getPropertyval();
                isonline = propertyval == 1;
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return isonline;
    }
}
