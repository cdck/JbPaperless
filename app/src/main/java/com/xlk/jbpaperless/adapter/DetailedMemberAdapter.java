package com.xlk.jbpaperless.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.jbpaperless.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/3/17.
 * @desc
 */
public class DetailedMemberAdapter extends BaseQuickAdapter<InterfaceMember.pbui_Item_MeetMemberDetailInfo, BaseViewHolder> {
    public int selectedId = -1;

    public DetailedMemberAdapter(@Nullable List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> data) {
        super(R.layout.item_bind_member, data);
    }

    public void choose(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getMemberid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceMember.pbui_Item_MeetMemberDetailInfo item) {
        String memberName = item.getMembername().toStringUtf8();
        holder.setText(R.id.item_view_1, memberName);
        boolean isSelected = selectedId == item.getMemberid();
        boolean isBind = item.getDevid() != 0;
        holder.setTextColor(R.id.item_view_1, isSelected ? Color.BLACK : Color.WHITE);
        Button item_view_1 = holder.getView(R.id.item_view_1);
        int length = memberName.length();
        if (length <= 3) {
            item_view_1.setTextSize(20f);
        } else {
            item_view_1.setTextSize(18f);
        }
        if (isBind) {
            item_view_1.setBackground(getContext().getDrawable(R.drawable.btn_logged_bg));
        } else {
            item_view_1.setBackground(isSelected ? getContext().getDrawable(R.drawable.btn_choosed_bg) : getContext().getDrawable(R.drawable.btn_can_choose_bg));
        }
    }
}
