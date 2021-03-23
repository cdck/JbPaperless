package com.xlk.jbpaperless.base;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.jbpaperless.jni.JniHelper;
import com.xlk.jbpaperless.util.IniUtil;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseContract.View {
    protected String TAG = this.getClass().getSimpleName();
    protected T presenter;
    protected JniHelper jni = JniHelper.getInstance();
    protected IniUtil ini = IniUtil.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        presenter = initPresenter();
        init(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.i(TAG, "onNewIntent " + this);
        super.onNewIntent(intent);
    }

    /**
     * 通过子类获取布局资源文件
     * @return 布局资源文件
     */
    protected abstract int getLayoutId();

    protected abstract void initView();

    /**
     * 通过子类创建想要的Presenter
     * @return T
     */
    protected abstract T initPresenter();

    /**
     * 布局文件和Presenter获取之后
     * @param savedInstanceState
     */
    protected abstract void init(Bundle savedInstanceState);

    protected void dismissPop(PopupWindow pop) {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void updateMemberList(List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos) {

    }

    @Override
    public void updateDeviceList(List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceInfos) {

    }

    @Override
    public void updateMemberDetailList(List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> memberDetailInfos) {

    }

    @Override
    public void updateOnlineStatus(boolean isOnline) {

    }
}
