package com.xlk.jbpaperless.view.meet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.model.EventMessage;
import com.xlk.jbpaperless.model.EventType;
import com.xlk.jbpaperless.view.agenda.AgendaActivity;
import com.xlk.jbpaperless.view.draw.DrawActivity;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author Created by xlk on 2021/3/18.
 * @desc
 */
public class MeetMenuFragment extends Fragment implements View.OnClickListener {

    private TextView tvAgenda;
    private TextView tvIssue;
    private TextView tvFile;
    private TextView tvAnnotationFile;
    private TextView tvMember;
    private TextView tvVote;
    private TextView tvDraw;
    private TextView tvWeb;
    private TextView tvVideo;
    private TextView tvService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_meet_menu, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        tvAgenda = (TextView) inflate.findViewById(R.id.tv_agenda);
        tvIssue = (TextView) inflate.findViewById(R.id.iv_issue);
        tvFile = (TextView) inflate.findViewById(R.id.tv_file);
        tvAnnotationFile = (TextView) inflate.findViewById(R.id.tv_annotation_file);
        tvMember = (TextView) inflate.findViewById(R.id.tv_member);
        tvVote = (TextView) inflate.findViewById(R.id.tv_vote);
        tvDraw = (TextView) inflate.findViewById(R.id.tv_draw);
        tvWeb = (TextView) inflate.findViewById(R.id.tv_web);
        tvVideo = (TextView) inflate.findViewById(R.id.tv_video);
        tvService = (TextView) inflate.findViewById(R.id.tv_service);
        tvAgenda.setOnClickListener(this);
        tvIssue.setOnClickListener(this);
        tvFile.setOnClickListener(this);
        tvAnnotationFile.setOnClickListener(this);
        tvMember.setOnClickListener(this);
        tvVote.setOnClickListener(this);
        tvDraw.setOnClickListener(this);
        tvWeb.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        tvService.setOnClickListener(this);
        //主持人
        inflate.findViewById(R.id.rl_host).setOnClickListener(v -> {
            LogUtils.d("点击主持人");
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MEETING_MENU).objects(false).build());
        });
        //常用
        inflate.findViewById(R.id.rl_commonly).setOnClickListener(v -> {
            LogUtils.d("点击常用");
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MEETING_MENU).objects(true).build());
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_agenda: {
                startActivity(new Intent(getContext(), AgendaActivity.class));
                break;
            }
            case R.id.iv_issue: {
                break;
            }
            case R.id.tv_file: {
                break;
            }
            case R.id.tv_annotation_file: {
                break;
            }
            case R.id.tv_member: {
                break;
            }
            case R.id.tv_vote: {
                break;
            }
            case R.id.tv_draw: {
                startActivity(new Intent(getContext(), DrawActivity.class));
                break;
            }
            case R.id.tv_web: {
                break;
            }
            case R.id.tv_video: {
                break;
            }
            case R.id.tv_service: {
                break;
            }
            default:
                break;
        }
    }
}
