package com.xlk.jbpaperless.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.jbpaperless.jni.JniHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author Created by xlk on 2021/3/15.
 * @desc
 */
public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseContract.View {
    protected String TAG = BaseFragment.class.getSimpleName();
    protected T presenter;
    protected JniHelper jni = JniHelper.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutId(), container, false);
        initView(inflate);
        presenter = initPresenter();
        initial();
        return inflate;
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View inflate);

    protected abstract T initPresenter();

    protected abstract void initial();

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            onHide();
        } else {
            onShow();
        }
        super.onHiddenChanged(hidden);
    }

    protected void onHide() {

    }

    protected void onShow() {

    }

    protected void dismissPop(PopupWindow pop) {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }

    @Override
    public void updateMemberDetailList(List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> memberDetailInfos) {

    }

    @Override
    public void updateDeviceList(List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceInfos) {

    }

    @Override
    public void updateMemberList(List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos) {

    }
    @Override
    public void updateOnlineStatus(boolean isOnline) {

    }
}
