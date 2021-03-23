package com.xlk.jbpaperless.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.model.Constant;
import com.xlk.jbpaperless.model.EventMessage;
import com.xlk.jbpaperless.model.EventType;
import com.xlk.jbpaperless.model.GlobalValue;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public class BackService extends Service {
    private final String TAG = "BackService-->";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "onCreate ");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy ");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBusEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //平台下载
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD_VALUE: {
                downloadInform(msg);
                break;
            }
            default:
                break;

        }
    }

    private void downloadInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] data2 = (byte[]) msg.getObjects()[0];
        InterfaceDownload.pbui_Type_DownloadCb pbui_type_downloadCb = InterfaceDownload.pbui_Type_DownloadCb.parseFrom(data2);
        int mediaid = pbui_type_downloadCb.getMediaid();
        int progress = pbui_type_downloadCb.getProgress();
        int nstate = pbui_type_downloadCb.getNstate();
        int err = pbui_type_downloadCb.getErr();
        String filepath = pbui_type_downloadCb.getPathname().toStringUtf8();
        String userStr = pbui_type_downloadCb.getUserstr().toStringUtf8();
        String fileName = filepath.substring(filepath.lastIndexOf("/") + 1).toLowerCase();
        if (nstate == InterfaceMacro.Pb_Download_State.Pb_STATE_MEDIA_DOWNLOAD_WORKING_VALUE) {
            ToastUtils.showShort(getString(R.string.file_downloaded_percent, fileName, progress + "%"));
        } else if (nstate == InterfaceMacro.Pb_Download_State.Pb_STATE_MEDIA_DOWNLOAD_EXIT_VALUE) {
            //下载退出---不管成功与否,下载结束最后一次的状态都是这个
            if (GlobalValue.downloadingFiles.contains(mediaid)) {
                int index = GlobalValue.downloadingFiles.indexOf(mediaid);
                GlobalValue.downloadingFiles.remove(index);
            }
            File file = new File(filepath);
            if (file.exists()) {
                LogUtils.i(TAG, "BusEvent -->" + "下载完成：" + filepath);
                switch (userStr) {
                    //会议议程文件下载完成
                    case Constant.DOWNLOAD_AGENDA_FILE: {
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_AGENDA_FILE).objects(filepath, mediaid).build());
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }
}
