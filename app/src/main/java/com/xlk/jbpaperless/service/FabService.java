package com.xlk.jbpaperless.service;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.jni.JniHelper;
import com.xlk.jbpaperless.model.EventMessage;
import com.xlk.jbpaperless.model.GlobalValue;
import com.xlk.jbpaperless.ui.ArtBoard;
import com.xlk.jbpaperless.ui.DialogUtil;
import com.xlk.jbpaperless.view.draw.DrawActivity;
import com.xlk.jbpaperless.view.draw.DrawPresenter;

import androidx.annotation.Nullable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public class FabService extends Service implements IFab {
    private final String TAG = "FabService-->";
    private Context context;
    private FabPresenter presenter;
    private JniHelper jni = JniHelper.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG, "onCreate");
        context = getApplicationContext();
        presenter = new FabPresenter(this, context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "onDestroy");
        presenter.onDestroy();
    }

    @Override
    public void openArtBoardInform(EventMessage msg) {
        try {
            LogUtils.i(TAG, "openArtBoardInform 收到打开白板通知");
            byte[] o = (byte[]) msg.getObjects()[0];
            InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard object = InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard.parseFrom(o);
            int operflag = object.getOperflag();//指定操作标志 参见Pb_MeetPostilOperType
            String medianame = object.getMedianame().toStringUtf8();//白板操作描述
            DrawPresenter.disposePicOpermemberid = object.getOpermemberid();//当前该命令的人员ID
            DrawPresenter.disposePicSrcmemid = object.getSrcmemid();//发起人的人员ID 白板标识使用
            DrawPresenter.disposePicSrcwbidd = object.getSrcwbid();//发起人的白板标识 取微秒级的时间作标识 白板标识使用
            if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_FORCEOPEN.getNumber()) {
                LogUtils.i(TAG, "openArtBoardInform: 强制打开白板  直接强制同意加入..");
                jni.agreeJoin(GlobalValue.localMemberId, DrawPresenter.disposePicSrcmemid, DrawPresenter.disposePicSrcwbidd);
                DrawPresenter.isSharing = true;//如果同意加入就设置已经在共享中
                DrawPresenter.mSrcmemid = DrawPresenter.disposePicSrcmemid;//设置发起的人员ID
                DrawPresenter.mSrcwbid = DrawPresenter.disposePicSrcwbidd;//设置白板标识
                Intent intent = new Intent(context, DrawActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber()) {
                LogUtils.i(TAG, "openArtBoardInform: 询问打开白板..");
                whetherOpen(DrawPresenter.disposePicSrcmemid, DrawPresenter.disposePicSrcwbidd, medianame, DrawPresenter.disposePicOpermemberid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void whetherOpen(final int srcmemid, final long srcwbidd, String medianame, final int opermemberid) {
        DialogUtil.createTipDialog(context, context.getString(R.string.title_whether_agree_join, medianame),
                context.getString(R.string.agree), context.getString(R.string.reject), new DialogUtil.onDialogClickListener() {
                    @Override
                    public void positive(DialogInterface dialog) {
                        //同意加入
                        jni.agreeJoin(GlobalValue.localMemberId, srcmemid, srcwbidd);
                        DrawPresenter.isSharing = true;//如果同意加入就设置已经在共享中
                        DrawPresenter.mSrcmemid = srcmemid;//设置发起的人员ID
                        DrawPresenter.mSrcwbid = srcwbidd;
                        Intent intent1 = new Intent(context, DrawActivity.class);
                        if (DrawPresenter.tempPicData != null) {
                            DrawPresenter.savePicData = DrawPresenter.tempPicData;
                            /** **** **  作为接收者保存  ** **** **/
                            ArtBoard.DrawPath drawPath = new ArtBoard.DrawPath();
                            drawPath.operid = GlobalValue.operid;
                            drawPath.srcwbid = srcwbidd;
                            drawPath.srcmemid = srcmemid;
                            drawPath.opermemberid = opermemberid;
                            drawPath.picdata = DrawPresenter.savePicData;
                            GlobalValue.operid = 0;
                            DrawPresenter.tempPicData = null;
                            //将路径保存到共享中绘画信息
                            DrawPresenter.pathList.add(drawPath);
                        }
                        intent1.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                        //自己不是发起人的时候,每次收到绘画通知都要判断是不是同一个发起人和白板标识
                        //并且集合中没有这一号人,将其添加进集合中
                        if (!DrawPresenter.togetherIDs.contains(opermemberid)) {
                            DrawPresenter.togetherIDs.add(opermemberid);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void negative(DialogInterface dialog) {
                        jni.rejectJoin(GlobalValue.localMemberId, srcmemid, srcwbidd);
                        dialog.dismiss();
                    }

                    @Override
                    public void dismiss(DialogInterface dialog) {

                    }
                });
    }
}
