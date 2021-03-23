package com.xlk.jbpaperless.view.agenda;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.base.BasePresenter;
import com.xlk.jbpaperless.model.Constant;
import com.xlk.jbpaperless.model.EventMessage;
import com.xlk.jbpaperless.model.EventType;
import com.xlk.jbpaperless.model.GlobalValue;

import java.io.File;

/**
 * @author Created by xlk on 2021/3/22.
 * @desc
 */
class AgendaPresenter extends BasePresenter<AgendaContract.View> implements AgendaContract.Presenter {
    public AgendaPresenter(AgendaContract.View view) {
        super(view);
        queryAgenda();
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //议程文件下载完成
            case EventType.BUS_AGENDA_FILE: {
                String path = (String) msg.getObjects()[0];
                mView.displayAgendaFile(path);
                break;
            }
            //腾讯X5内核加载完成
            case EventType.BUS_X5_INSTALL:
                //议程变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA_VALUE:
                queryAgenda();
                break;
            default:
                break;
        }
    }

    private void queryAgenda() {
        InterfaceAgenda.pbui_meetAgenda info = jni.queryAgenda();
        if (info != null) {
            int agendatype = info.getAgendatype();
            if (agendatype == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TEXT_VALUE) {
                mView.updateAgendaContent(info.getText().toStringUtf8());
            } else if (agendatype == InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_FILE_VALUE) {
                int mediaid = info.getMediaid();
                byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaid);
                InterfaceBase.pbui_CommonTextProperty textProperty = null;
                try {
                    textProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                String fileName = textProperty.getPropertyval().toStringUtf8();
                LogUtils.i(TAG, "fun_queryAgenda 获取到文件议程 -->" + mediaid + ", 文件名：" + fileName);
                FileUtils.createOrExistsDir(Constant.download_dir);
                File file = new File(Constant.download_dir + fileName);
                if (file.exists()) {
                    if (GlobalValue.downloadingFiles.contains(mediaid)) {
                        ToastUtils.showShort(R.string.file_downloading);
                    } else {
                        mView.displayAgendaFile(file.getAbsolutePath());
                    }
                } else {
                    jni.creationFileDownload(Constant.download_dir + fileName, mediaid, 1, 0, Constant.DOWNLOAD_AGENDA_FILE);
                }
            }
        }
    }

}
