package com.xlk.jbpaperless.view.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.jbpaperless.App;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.base.BaseActivity;
import com.xlk.jbpaperless.model.Constant;
import com.xlk.jbpaperless.model.GlobalValue;
import com.xlk.jbpaperless.view.main.fragment.BindMemberFragment;
import com.xlk.jbpaperless.view.meet.MeetingActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import static com.xlk.jbpaperless.model.GlobalValue.camera_height;
import static com.xlk.jbpaperless.model.GlobalValue.camera_width;
import static com.xlk.jbpaperless.model.GlobalValue.screen_width;
import static com.xlk.jbpaperless.util.ConvertUtil.s2b;

/**
 * @author xlk
 * @date 2021/3/15
 * @desc: 主页界面
 */
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private TextView tvStatus;
    private android.widget.LinearLayout llDate;
    private TextView tvDay;
    private TextView tvWeek;
    private TextView tvTime;
    private TextView tvDeviceId;
    private TextView tvMeetName;
    private TextView tvMemberName;
    private TextView tvUnit;
    private TextView tvPosition;
    private android.widget.Button btnEnterMeet;
    private FrameLayout fl_main;
    private BindMemberFragment bindMemberFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        llDate = (LinearLayout) findViewById(R.id.ll_date);
        tvDay = (TextView) findViewById(R.id.tv_day);
        tvWeek = (TextView) findViewById(R.id.tv_week);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvStatus = (TextView) findViewById(R.id.tv_status);

        tvMeetName = (TextView) findViewById(R.id.tv_meet_name);
        tvMemberName = (TextView) findViewById(R.id.tv_member_name);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        tvPosition = (TextView) findViewById(R.id.tv_position);
        tvDeviceId = (TextView) findViewById(R.id.tv_device_id);
        btnEnterMeet = (Button) findViewById(R.id.btn_enter_meet);
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        tvStatus.setOnClickListener(v -> {
            // TODO: 2021/3/18 进入设置ip地址页面
        });
        btnEnterMeet.setOnClickListener(v -> {
            if (!XXPermissions.hasPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                applyAlertWindowPermission();
            } else {
                readySignIn();
            }
        });
    }

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        applyPermissions(new String[]{
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.RECORD_AUDIO,
                Permission.CAMERA,
                Permission.READ_PHONE_STATE
        });
    }

    private void applyPermissions(String[] pers) {
        XXPermissions.with(this).constantRequest()
                .permission(pers).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    start();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {

            }
        });
    }

    private void start() {
        initConfigFile();
        updateAppVersion();
        try {
            initCameraSize();
        } catch (Exception e) {
            LogUtils.e(TAG, "start --> 相机使用失败：" + e.toString());
            e.printStackTrace();
        }
        applyReadFrameBufferPermission();
    }

    private void applyReadFrameBufferPermission() {
        try {
            MediaProjectionManager manager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            App.mMediaProjectionManager = manager;
            if (App.mIntent != null && App.mResult != 0) {
                initial();
            } else {
                /** **** **  第一次时保存 manager  ** **** **/
                startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_CODE_READ_FRAME_BUFFER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            initial();
        }
    }

    /**
     * 权限获取成功后开始初始化
     */
    private void initial() {
        LogUtils.i(TAG, "initial");
        presenter.initialization(DeviceUtils.getUniqueDeviceId());
    }

    private final int REQUEST_CODE_READ_FRAME_BUFFER = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_READ_FRAME_BUFFER) {
            if (resultCode == Activity.RESULT_OK) {
                App.mResult = resultCode;
                App.mIntent = data;
                App.mMediaProjection = App.mMediaProjectionManager.getMediaProjection(resultCode, data);
                initial();
            } else {
                applyReadFrameBufferPermission();
            }
        }
    }

    private void initConfigFile() {
        long l = System.currentTimeMillis();
        LogUtils.d("进入initConfigFile");
        FileUtils.createOrExistsDir(Constant.root_dir);
        boolean exists = FileUtils.isFileExists(Constant.root_dir + "client.ini");
        if (!exists) {
            copyTo("client.ini", Constant.root_dir, "client.ini");
        }
        File file = new File(Constant.root_dir + "client.dev");
        if (file.exists()) {
            file.delete();
        }
        copyTo("client.dev", Constant.root_dir, "client.dev");
        LogUtils.i(TAG, "initConfigFile 用时=" + (System.currentTimeMillis() - l));
    }

    /**
     * 复制文件
     *
     * @param fromPath
     * @param toPath
     * @param fileName
     */
    private void copyTo(String fromPath, String toPath, String fileName) {
        // 复制位置
        File toFile = new File(toPath);
        FileUtils.createOrExistsFile(toFile);
        try {
            // 根据文件名获取assets文件夹下的该文件的inputstream
            InputStream fromFileIs = getResources().getAssets().open(fromPath);
            // 获取文件的字节数
            int length = fromFileIs.available();
            // 创建byte数组
            byte[] buffer = new byte[length];
            FileOutputStream fileOutputStream = new FileOutputStream(toFile
                    + "/" + fileName); // 字节输入流
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    fromFileIs);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    fileOutputStream);
            int len = bufferedInputStream.read(buffer);
            while (len != -1) {
                bufferedOutputStream.write(buffer, 0, len);
                len = bufferedInputStream.read(buffer);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
            fromFileIs.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCameraSize() {
        int type = 1;
        LogUtils.d(TAG, "initCameraSize :   --> ");
        //获取摄像机的个数 一般是前/后置两个
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 2) {
            LogUtils.d(TAG, "initCameraSize: 该设备只有后置像头");
            //如果没有2个则说明只有后置像头
            type = 0;
        }
        ArrayList<Integer> supportW = new ArrayList<>();
        ArrayList<Integer> supportH = new ArrayList<>();
        int largestW = 0, largestH = 0;
        Camera c = Camera.open(type);
        Camera.Parameters param = null;
        if (c != null) {
            param = c.getParameters();
        }
        if (param == null) {
            return;
        }
        for (int i = 0; i < param.getSupportedPreviewSizes().size(); i++) {
            int w = param.getSupportedPreviewSizes().get(i).width, h = param.getSupportedPreviewSizes().get(i).height;
            LogUtils.d(TAG, "initCameraSize: w=" + w + " h=" + h);
            supportW.add(w);
            supportH.add(h);
        }
        for (int i = 0; i < supportH.size(); i++) {
            try {
                largestW = supportW.get(i);
                largestH = supportH.get(i);
                LogUtils.d(TAG, "initCameraSize :   --> largestW= " + largestW + " , largestH=" + largestH);
                MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", largestW, largestH);
                if (MediaCodec.createEncoderByType("video/avc").getCodecInfo().getCapabilitiesForType("video/avc").isFormatSupported(mediaFormat)) {
                    if (largestW * largestH > camera_width * camera_height) {
                        camera_width = largestW;
                        camera_height = largestH;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.setPreviewCallback(null);
                    c.stopPreview();
                    c.release();
                    c = null;
                }
            }
        }
        LogUtils.d(TAG, "initCameraSize -->" + "前置像素：" + camera_width + " X " + camera_height);
        if (camera_width * camera_height > 1280 * 720) {
            camera_width = 1280;
            camera_height = 720;
        }
    }

    private void updateAppVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
//            appVersion.setText(getString(R.string.version_, packageInfo.versionName));
            String hardver = "";
            String softver = "";
            if (packageInfo.versionName.contains(".")) {
                hardver = packageInfo.versionName.substring(0, packageInfo.versionName.indexOf("."));
                softver = packageInfo.versionName.substring(packageInfo.versionName.indexOf(".") + 1);
            }
            if (ini.loadFile()) {
                String iniHardver = ini.get("selfinfo", "hardver");
                String iniSoftver = ini.get("selfinfo", "softver");
                String lastTime = ini.get("other", "lastTime");
                if (!iniHardver.equals(hardver) || !iniSoftver.equals(softver)) {
                    LogUtils.i(TAG, "setVersion 设置到ini文件中");
                    ini.put("selfinfo", "hardver", hardver);
                    ini.put("selfinfo", "softver", softver);
                    ini.put("other", "lastTime", System.currentTimeMillis());
                    ini.store();
                }
            }
            LogUtils.e(TAG, "packageInfo.versionCode=" + packageInfo.versionCode + ",packageInfo.lastUpdateTime=" + packageInfo.lastUpdateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDeviceId() {
        tvDeviceId.setText("ID：" + GlobalValue.localDeviceId);
    }

    @Override
    public void updateTime(String[] date) {
        tvTime.setText(date[0]);
        tvDay.setText(date[1]);
        tvWeek.setText(date[2]);
    }

    @Override
    public void updateOnlineStatus(boolean isOnline) {
        LogUtils.d(TAG, "updateOnlineStatus 是否在线=" + isOnline);
        if (isOnline) {
            tvStatus.setText(getString(R.string.online));
        } else {
            tvStatus.setText(getString(R.string.offline));
        }
    }

    private void changeUi(boolean isSignInPage) {
        if (isSignInPage) {
            fl_main.setVisibility(View.GONE);
            btnEnterMeet.setVisibility(View.VISIBLE);
            tvMeetName.setVisibility(View.VISIBLE);
            tvMemberName.setVisibility(View.VISIBLE);
            tvUnit.setVisibility(View.VISIBLE);
            tvPosition.setVisibility(View.VISIBLE);
            tvDeviceId.setVisibility(View.VISIBLE);
        } else {
            fl_main.setVisibility(View.VISIBLE);
            btnEnterMeet.setVisibility(View.GONE);
            tvMeetName.setVisibility(View.GONE);
            tvMemberName.setVisibility(View.GONE);
            tvUnit.setVisibility(View.GONE);
            tvPosition.setVisibility(View.GONE);
            tvDeviceId.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateInterfaceContent(InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo) {
        LogUtils.i(TAG, "updateInterfaceContent 是否为空=" + (devMeetInfo == null));
        if (devMeetInfo == null) {
            changeUi(true);
            tvMeetName.setText("");
            tvMemberName.setText("");
            tvUnit.setText("");
            tvPosition.setText("");
            GlobalValue.localMeetingId = 0;
            GlobalValue.localMemberId = 0;
            return;
        }
        GlobalValue.localMeetingId = devMeetInfo.getMeetingid();
        GlobalValue.localMemberId = devMeetInfo.getMemberid();
        GlobalValue.signInType = devMeetInfo.getSigninType();
        LogUtils.d(TAG, "会议名称=" + devMeetInfo.getMeetingname().toStringUtf8()
                + ",参会人名称=" + devMeetInfo.getMembername().toStringUtf8()
                + ",Company=" + devMeetInfo.getCompany().toStringUtf8()
                + ",Job=" + devMeetInfo.getJob().toStringUtf8()
        );
        tvMeetName.setText(devMeetInfo.getMeetingname().toStringUtf8());
        tvMemberName.setText(devMeetInfo.getMembername().toStringUtf8());
        tvUnit.setText(devMeetInfo.getCompany().toStringUtf8());
        tvPosition.setText(devMeetInfo.getJob().toStringUtf8());
        if (GlobalValue.localMeetingId == 0) {
            changeUi(true);
            //未加入会议
            tvMeetName.setText(getString(R.string.no_meeting));
            return;
        }
        boolean isBindMember = GlobalValue.localMemberId != 0;
        if (isBindMember) {
            changeUi(true);
        } else {
            changeUi(false);
            //加入会议，但是未绑定参会人
            showFragment(1);
        }
    }


    @Override
    public void readySignIn() {
        String signInType = "";
        switch (GlobalValue.signInType) {
            //个人密码签到
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_psw_VALUE: {
                signInType = getString(R.string.personal_password_signin);
                break;
            }
            //手写签到
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_photo_VALUE: {
                signInType = getString(R.string.handwriting_signin);
                break;
            }
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw_VALUE: {
                signInType = getString(R.string.meeting_password_signin);
                break;
            }
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw_photo_VALUE: {
                signInType = getString(R.string.meeting_password_and_handwriting_signin);
                break;
            }
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_psw_photo_VALUE: {
                signInType = getString(R.string.personal_password_and_handwriting_signin);
                break;
            }
            default:
                jni.sendSign(0, GlobalValue.signInType, "", s2b(""));
                break;
        }
    }

    @Override
    public void jump2meet() {
        ActivityUtils.startActivity(MeetingActivity.class);
    }

    /**
     * 申请悬浮窗权限
     */
    private void applyAlertWindowPermission() {
        XXPermissions.with(this).constantRequest()
                .permission(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                    }
                });
    }

    private void showFragment(int type) {
        LogUtils.d(TAG, "showFragment type=" + type);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideFragment(ft);
        switch (type) {
            case 1: {
                if (bindMemberFragment == null) {
                    bindMemberFragment = new BindMemberFragment();
                    ft.add(R.id.fl_main, bindMemberFragment);
                }
                ft.show(bindMemberFragment);
                break;
            }
            default:
                break;
        }
        ft.commitAllowingStateLoss();//允许状态丢失，其他完全一样
    }

    private void hideFragment(FragmentTransaction ft) {
        if (bindMemberFragment != null) ft.hide(bindMemberFragment);
    }
}