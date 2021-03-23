package com.xlk.jbpaperless.view.main;

import android.media.MediaCodecInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.jbpaperless.App;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.base.BasePresenter;
import com.xlk.jbpaperless.jni.Call;
import com.xlk.jbpaperless.model.EventMessage;
import com.xlk.jbpaperless.model.GlobalValue;
import com.xlk.jbpaperless.util.CodecUtil;
import com.xlk.jbpaperless.util.DateUtil;

import static com.xlk.jbpaperless.App.appContext;

import java.util.List;
import java.util.Objects;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo;

    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    public void initialization(String uniqueDeviceId) {
        if (GlobalValue.initializationIsOver) {
            initial();
        } else {
            App.threadPool.execute(() -> jni.javaInitSys(uniqueDeviceId));
        }
    }

    private void initial() {
        LogUtils.i(TAG, "initial ");
        //  修改本机界面状态
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace_VALUE);
        int format = CodecUtil.selectColorFormat(Objects.requireNonNull(CodecUtil.selectCodec("video/avc")), "video/avc");
        switch (format) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                Call.COLOR_FORMAT = 0;
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                Call.COLOR_FORMAT = 1;
                break;
            default:
                break;
        }
        jni.InitAndCapture(0, 2);
        jni.InitAndCapture(0, 3);
        cacheData();
        queryDeviceMeetInfo();
        initialData();
    }

    private void cacheData() {
        //缓存参会人信息(不然收不到参会人变更通知)
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE);
        //缓存会议信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE);
        //缓存排位信息(不然收不到排位变更通知)
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //平台登陆验证返回
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEVALIDATE_VALUE: {
                byte[] s = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_Type_DeviceValidate deviceValidate = InterfaceBase.pbui_Type_DeviceValidate.parseFrom(s);
                int valflag = deviceValidate.getValflag();
                List<Integer> valList = deviceValidate.getValList();
                List<Long> user64BitdefList = deviceValidate.getUser64BitdefList();
                String binaryString = Integer.toBinaryString(valflag);
                LogUtils.i("initFailed valflag=" + valflag + "，二进制：" + binaryString + ", valList=" + valList.toString() + ", user64List=" + user64BitdefList.toString());
                int count = 0, index;
                //  1 1101 1111
                char[] chars = binaryString.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if ((chars[chars.length - 1 - i]) == '1') {
                        //有效位个数+1
                        count++;
                        //有效位当前位于valList的索引（跟i是无关的）
                        index = count - 1;
                        int code = valList.get(index);
                        LogUtils.d("initFailed 有效位：" + i + ",当前有效位的个数：" + count);
                        switch (i) {
                            case 0:
                                LogUtils.e("initFailed 区域服务器ID：" + code);
                                break;
                            case 1:
                                LogUtils.e("initFailed 设备ID：" + code);
                                GlobalValue.localDeviceId = code;
                                mView.updateDeviceId();
                                break;
                            case 2:
                                LogUtils.e("initFailed 状态码：" + code);
                                initializationResult(code);
                                break;
                            case 3:
                                LogUtils.e("initFailed 到期时间：" + code);
                                break;
                            case 4:
                                LogUtils.e("initFailed 企业ID：" + code);
                                break;
                            case 5:
                                LogUtils.e("initFailed 协议版本：" + code);
                                break;
                            case 6:
                                LogUtils.e("initFailed 注册时自定义的32位整数值：" + code);
                                break;
                            case 7:
                                LogUtils.e("initFailed 当前在线设备数：" + code);
                                break;
                            case 8:
                                LogUtils.e("initFailed 最大在线设备数：" + code);
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            }
            //时间回调
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_TIME_VALUE: {
                Object[] objs = msg.getObjects();
                byte[] data = (byte[]) objs[0];
                InterfaceBase.pbui_Time pbui_time = InterfaceBase.pbui_Time.parseFrom(data);
                //微秒 转换成毫秒 除以 1000
                String[] gtmDate = DateUtil.getGTMDate(pbui_time.getUsec() / 1000);
                mView.updateTime(gtmDate);
                break;
            }
            //设备会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备会议信息变更通知");
                queryDeviceMeetInfo();
                break;
            }
            //会议签到结果返回
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON_VALUE) {
                    //签到密码返回
                    byte[] datas = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_Type_MeetDBServerOperError dbServerOperError = InterfaceBase.pbui_Type_MeetDBServerOperError.parseFrom(datas);
                    int type = dbServerOperError.getType();
                    int method1 = dbServerOperError.getMethod();
                    int status = dbServerOperError.getStatus();
                    if (status == InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_DONE_VALUE) {
                        LogUtils.i(TAG, "BusEvent -->" + "签到成功，进入会议");
                        ToastUtils.showShort(R.string.sign_in_successfully);
                        mView.jump2meet();
                    } else if (status == InterfaceMacro.Pb_DB_StatusCode.Pb_STATUS_PSWFAILED_VALUE) {
                        LogUtils.i(TAG, "BusEvent -->" + "签到密码错误");
                        ToastUtils.showShort(R.string.sign_in_password_error);
                        mView.readySignIn();
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private void initializationResult(int code) {
        String msg;
        switch (code) {
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE:
                msg = appContext.getString(R.string.error_0);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_EXPIRATION_VALUE:
                msg = appContext.getString(R.string.error_1);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_OPER_VALUE:
                msg = appContext.getString(R.string.error_2);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ENTERPRISE_VALUE:
                msg = appContext.getString(R.string.error_3);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NODEVICEID_VALUE:
                msg = appContext.getString(R.string.error_4);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOALLOWIN_VALUE:
                msg = appContext.getString(R.string.error_5);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_FILEERROR_VALUE:
                msg = appContext.getString(R.string.error_6);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_INVALID_VALUE:
                msg = appContext.getString(R.string.error_7);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_IDOCCUPY_VALUE:
                msg = appContext.getString(R.string.error_8);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NOTBEING_VALUE:
                msg = appContext.getString(R.string.error_9);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_ONLYDEVICEID_VALUE:
                msg = appContext.getString(R.string.error_10);
                break;
            case InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_DEVICETYPENOMATCH_VALUE:
                msg = appContext.getString(R.string.error_11);
                break;
            default:
                msg = "";
                break;
        }
        if (!msg.isEmpty()) {
            ToastUtils.showShort(msg);
        }
        //平台初始化成功
        if (code == InterfaceMacro.Pb_ValidateErrorCode.Pb_PARSER_ERROR_NONE_VALUE) {
            GlobalValue.initializationIsOver = true;
            initial();
        }
    }

    private void queryDeviceMeetInfo() {
        devMeetInfo = jni.queryDeviceMeetInfo();
        if (devMeetInfo != null) {
            GlobalValue.localMeetingId = devMeetInfo.getMeetingid();
            GlobalValue.localMemberId = devMeetInfo.getMemberid();
        }
        cacheData();
        mView.updateInterfaceContent(devMeetInfo);
    }
}
