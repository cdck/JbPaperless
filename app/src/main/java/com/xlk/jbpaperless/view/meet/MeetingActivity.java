package com.xlk.jbpaperless.view.meet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.android.material.tabs.TabLayout;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.adapter.PagerAdapter;
import com.xlk.jbpaperless.base.BaseActivity;
import com.xlk.jbpaperless.util.PopUtil;
import com.xlk.jbpaperless.view.meet.fragment.MeetHostFragment;
import com.xlk.jbpaperless.view.meet.fragment.MeetMenuFragment;
import com.xlk.jbpaperless.view.meet.fragment.MeetOtherFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


/**
 * @author xlk
 * @date 2021/3/22
 * @desc: 签到成功后进入的界面
 */
public class MeetingActivity extends BaseActivity<MeetingPresenter> implements MeetingContract.View, View.OnClickListener {

    private android.widget.TextView tvMemberName;
    private android.widget.RelativeLayout rlTop;
    private android.widget.ImageView ivMember;
    private android.widget.LinearLayout llMeetContent;
    private TextView tvMeetName;
    private android.widget.Button btnMeetingMessage;
    private android.widget.Button btnMike;
    private android.widget.Button btnMeetingSignIn;
    private ViewPager viewPager;
    private TabLayout tab_layout;
    private TextView tvMeetingName;
    private TextView tvMeetingPlace;
    private TextView tvMeetingTime;
    private TextView tvMeetingHost;
    private TextView tvMeetingMembers;

    /**
     * 会议信息PopupWindow数据
     */
    private PopupWindow popMeetingMessage;
    private String mMeetingName;
    /**
     * 会议地点
     */
    private String mMeetingAddr;
    /**
     * 会议时间 开始时间至结束时间
     */
    private String mMeetingTime;
    private String mMeetingMembers;
    private String mMeetingHostName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meeting;
    }

    @Override
    protected MeetingPresenter initPresenter() {
        return new MeetingPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace_VALUE);
        presenter.queryDeviceMeetInfo();
        presenter.queryMember();
        initViewPager();
        presenter.updateMeetingMessage();
    }

    private void initViewPager() {
        ArrayList<Fragment> fs = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        fs.add(new MeetHostFragment());
        fs.add(new MeetMenuFragment());
        fs.add(new MeetOtherFragment());
        titles.add("主持人");
        titles.add("会议功能");
        titles.add("常用功能");
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), fs, titles));
//        tab_layout.addOnTabSelectedListener();
        tab_layout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onSelectedPage(boolean isNext) {
        int currentItem = viewPager.getCurrentItem();
        LogUtils.d(TAG, "currentItem=" + currentItem + ",isNext=" + isNext);
        if (isNext) {
            if (currentItem <= 2) {
                viewPager.setCurrentItem(++currentItem);
            }
        } else {
            if (currentItem >= 0) {
                viewPager.setCurrentItem(--currentItem);
            }
        }
    }

    @Override
    protected void initView() {
        tvMemberName = (TextView) findViewById(R.id.tv_member_name);
        rlTop = (RelativeLayout) findViewById(R.id.rl_top);
        ivMember = (ImageView) findViewById(R.id.iv_member);
        llMeetContent = (LinearLayout) findViewById(R.id.ll_meet_content);
        tvMeetName = (TextView) findViewById(R.id.tv_meet_name);
        btnMeetingMessage = (Button) findViewById(R.id.btn_meeting_message);
        btnMike = (Button) findViewById(R.id.btn_mike);
        btnMeetingSignIn = (Button) findViewById(R.id.btn_meeting_sign_in);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        btnMeetingMessage.setOnClickListener(this);
        btnMike.setOnClickListener(this);
        btnMeetingSignIn.setOnClickListener(this);
    }

    @Override
    public void updateDeviceMeetingInfo(InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo) {
        tvMemberName.setText(devMeetInfo.getMembername().toStringUtf8());
        mMeetingName = devMeetInfo.getMeetingname().toStringUtf8();
        tvMeetName.setText(mMeetingName);
        if (popMeetingMessage != null && popMeetingMessage.isShowing()) {
            tvMeetingName.setText(mMeetingName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //会议信息
            case R.id.btn_meeting_message: {
                presenter.updateMeetingMessage();
                showMeetingMessagePop();
                break;
            }
            case R.id.btn_mike: {
                break;
            }
            case R.id.btn_meeting_sign_in: {
                break;
            }
            default:
                break;
        }
    }

    /* **** **  更新会议信息  ** **** */

    private void showMeetingMessagePop() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_meeting_message, null);
        int w = ScreenUtils.getScreenWidth() / 2;
        int h = ScreenUtils.getScreenHeight() / 2;
        popMeetingMessage = PopUtil.createCenter(inflate, w, h, viewPager);
        tvMeetingName = (TextView) inflate.findViewById(R.id.tv_meeting_name);
        tvMeetingPlace = (TextView) inflate.findViewById(R.id.tv_meeting_place);
        tvMeetingTime = (TextView) inflate.findViewById(R.id.tv_meeting_time);
        tvMeetingHost = (TextView) inflate.findViewById(R.id.tv_meeting_host);
        tvMeetingMembers = (TextView) inflate.findViewById(R.id.tv_members);
        updateMeetingMessageTv();
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> {
            popMeetingMessage.dismiss();
        });
    }

    private void updateMeetingMessageTv() {
        tvMeetingName.setText(mMeetingName);
        tvMeetingPlace.setText(mMeetingAddr);
        tvMeetingTime.setText(mMeetingTime);
        tvMeetingHost.setText(mMeetingHostName);
        tvMeetingMembers.setText(mMeetingMembers);
    }

    @Override
    public void updateMeetingHostName(String hostName) {
        mMeetingHostName = hostName;
        if (popMeetingMessage != null && popMeetingMessage.isShowing()) {
            tvMeetingHost.setText(mMeetingHostName);
        }
    }

    @Override
    public void updateMemberList(List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memberInfos.size(); i++) {
            InterfaceMember.pbui_Item_MemberDetailInfo member = memberInfos.get(i);
            if (i == memberInfos.size() - 1) {
                sb.append(member.getName().toStringUtf8());
            } else {
                sb.append(member.getName().toStringUtf8());
                sb.append("、");
            }
        }
        mMeetingMembers = sb.toString();
        if (popMeetingMessage != null && popMeetingMessage.isShowing()) {
            tvMeetingMembers.setText(mMeetingMembers);
        }
    }

    @Override
    public void updateMeetingUseTime(String time) {
        mMeetingTime = time;
        if (popMeetingMessage != null && popMeetingMessage.isShowing()) {
            tvMeetingTime.setText(mMeetingTime);
        }
    }

    @Override
    public void updateMeetingAddr(String addr) {
        mMeetingAddr = addr;
        if (popMeetingMessage != null && popMeetingMessage.isShowing()) {
            tvMeetingPlace.setText(mMeetingAddr);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  修改本机界面状态
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace_VALUE);
    }
}