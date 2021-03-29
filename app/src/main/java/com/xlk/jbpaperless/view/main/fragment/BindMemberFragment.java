package com.xlk.jbpaperless.view.main.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gcssloop.widget.PagerGridLayoutManager;
import com.gcssloop.widget.PagerGridSnapHelper;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.adapter.DetailedMemberAdapter;
import com.xlk.jbpaperless.base.BaseFragment;
import com.xlk.jbpaperless.model.GlobalValue;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/3/17.
 * @desc
 */
public class BindMemberFragment extends BaseFragment<BindMemberPresenter> implements BindMemberContract.View {
    private TextView tvMeetName;
    private RecyclerView rvMember;
    private Button btnEnsure;
    private TextView tvDeviceId;
    private DetailedMemberAdapter memberAdapter;
    private PagerGridLayoutManager pagerGridLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_bind_member;
    }

    @Override
    protected void initView(View inflate) {
        tvMeetName = (TextView) inflate.findViewById(R.id.tv_meet_name);
        rvMember = (RecyclerView) inflate.findViewById(R.id.rv_member);
        btnEnsure = (Button) inflate.findViewById(R.id.btn_ensure);
        tvDeviceId = (TextView) inflate.findViewById(R.id.tv_device_id);
        tvDeviceId.setText("ID：" + GlobalValue.localDeviceId);
        inflate.findViewById(R.id.ll_pre).setOnClickListener(v -> {
            pagerGridLayoutManager.smoothPrePage();
        });
        inflate.findViewById(R.id.ll_next).setOnClickListener(v -> {
            pagerGridLayoutManager.smoothNextPage();
        });
        btnEnsure.setOnClickListener(v -> {
            presenter.queryMemberDetailed();
            int memberId = memberAdapter.getSelectedId();
            if (memberId == -1) {
                ToastUtils.showShort(R.string.please_choose_member);
                return;
            }
            jni.modifyMeetRanking(memberId, 0, GlobalValue.localDeviceId);
        });
    }

    @Override
    protected BindMemberPresenter initPresenter() {
        return new BindMemberPresenter(this);
    }

    @Override
    protected void initial() {
        onShow();
    }

    @Override
    protected void onShow() {
        presenter.queryDeviceMeetInfo();
        presenter.queryMemberDetailed();
    }

    @Override
    public void updateMeetingName(String meetName) {
        tvMeetName.setText(meetName);
    }

    @Override
    public void updateMemberDetailList(List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> memberDetailInfos) {
        LogUtils.e(TAG, "updateMemberDetailList memberDetailInfos.size=" + memberDetailInfos.size());
        if (memberAdapter == null) {
            memberAdapter = new DetailedMemberAdapter(memberDetailInfos);
            pagerGridLayoutManager = new PagerGridLayoutManager(2, 6, PagerGridLayoutManager.HORIZONTAL);
            rvMember.setLayoutManager(pagerGridLayoutManager);
            // 2.设置滚动辅助工具
            PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
            pageSnapHelper.attachToRecyclerView(rvMember);
            rvMember.setAdapter(memberAdapter);
            memberAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    InterfaceMember.pbui_Item_MeetMemberDetailInfo item = memberDetailInfos.get(position);
                    if (item.getDevid() == 0) {
                        memberAdapter.choose(item.getMemberid());
                    }
                }
            });
        } else {
            memberAdapter.notifyDataSetChanged();
        }
    }

//    @Override
//    public void updateMemberList() {
////        if (memberAdapter == null) {
////            memberAdapter = new DetailedMemberAdapter(presenter.memberDetailInfos);
////            pagerGridLayoutManager = new PagerGridLayoutManager(2, 6, PagerGridLayoutManager.HORIZONTAL);
////            rvMember.setLayoutManager(pagerGridLayoutManager);
////            // 2.设置滚动辅助工具
////            PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
////            pageSnapHelper.attachToRecyclerView(rvMember);
//////            pagerGridLayoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
//////                @Override
//////                public void onPageSizeChanged(int pageSize) {
//////                    LogUtils.d(TAG, "总页数=" + pageSize);
//////                }
//////
//////                @Override
//////                public void onPageSelect(int pageIndex) {
//////                    LogUtils.d(TAG, "选中页码=" + pageIndex);
//////                }
//////            });
////
//////            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
//////            rvMember.setLayoutManager(layoutManager);
////            rvMember.setAdapter(memberAdapter);
////            memberAdapter.setOnItemClickListener(new OnItemClickListener() {
////                @Override
////                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
////                    InterfaceMember.pbui_Item_MeetMemberDetailInfo item = presenter.memberDetailInfos.get(position);
////                    if (item.getDevid() == 0) {
////                        memberAdapter.choose(item.getMemberid());
////                    }
////                }
////            });
////        } else {
////            memberAdapter.notifyDataSetChanged();
////        }
//    }
}
