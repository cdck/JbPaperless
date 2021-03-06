package com.xlk.jbpaperless.view.agenda;


import android.os.Bundle;
import android.os.Environment;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsReaderView;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.base.BaseActivity;
import com.xlk.jbpaperless.model.GlobalValue;

import static com.xlk.jbpaperless.App.appContext;

/**
 * @author xlk
 * @date 2021/3/22
 * @desc: 查看会议议程
 */
public class AgendaActivity extends BaseActivity<AgendaPresenter> implements AgendaContract.View, TbsReaderView.ReaderCallback, View.OnGenericMotionListener {
    /**
     * =true 加载的是系统内核（默认），=false 加载的是X5内核
     */
    public static boolean isNeedRestart;
    private android.widget.LinearLayout agendaRoot;
    private android.widget.ProgressBar progressBar;
    private android.widget.ScrollView agendaSv;
    private android.widget.TextView agendaTv;
    private TbsReaderView tbsReaderView;
    private TextView tvPageTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_agenda;
    }

    @Override
    protected void initView() {
        tvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        agendaRoot = (LinearLayout) findViewById(R.id.agenda_root);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        agendaSv = (ScrollView) findViewById(R.id.agenda_sv);
        agendaTv = (TextView) findViewById(R.id.agenda_tv);
        findViewById(R.id.iv_close).setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected AgendaPresenter initPresenter() {
        return new AgendaPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        tvPageTitle.setText(getString(R.string.meeting_agenda));
    }

    @Override
    public void updateAgendaContent(String agendaContent) {
        if (tbsReaderView != null) {
            agendaRoot.removeView(tbsReaderView);
            tbsReaderView.onStop();
            tbsReaderView = null;
        }
        //也有可能下载X5内核完成，但是议程变成文本类
        progressBar.setVisibility(View.GONE);
        agendaSv.setVisibility(View.VISIBLE);
        agendaTv.setText(agendaContent);
    }

    @Override
    public void displayAgendaFile(String path) {
        agendaSv.setVisibility(View.GONE);
        if (GlobalValue.initX5Finished) {
            //加载完成
            if (isNeedRestart) {
                //加载的是系统内核
                progressBar.setVisibility(View.GONE);
                agendaSv.setVisibility(View.VISIBLE);
                agendaTv.setText(getString(R.string.init_x5_failure));
                return;
            }
            //进入加载完成，并且加载的是X5内核↓↓↓
        } else {
            //X5内核没有加载完成
            progressBar.setVisibility(View.VISIBLE);
            TbsDownloader.startDownload(appContext);
            return;
        }
        /* **** **  加载完成，并且加载的是X5内核  ** **** */
        progressBar.setVisibility(View.GONE);
        String tempPath = Environment.getExternalStorageDirectory().getPath();
        tbsReaderView = new TbsReaderView(this, this);
//        tbsReaderView.setOnGenericMotionListener(this);
        agendaRoot.addView(tbsReaderView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        Bundle bundle = new Bundle();
        bundle.putString("filePath", path);
        bundle.putString("tempPath", tempPath);//bsReaderTemp
        String suffix = path.substring(path.lastIndexOf(".") + 1);
        LogUtils.i(TAG, "displayFile 打开文件 -->" + path + "， 后缀： " + suffix + ", tempPath= " + tempPath);
        try {
            boolean result = tbsReaderView.preOpen(suffix, false);
            LogUtils.e(TAG, "displayFile :  result --> " + result);
            if (result) {
                tbsReaderView.openFile(bundle);
            } else {
                ToastUtils.showShort(R.string.not_supported);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //不停止掉，下次进入是打开文件会卡在加载中状态
        if (tbsReaderView != null) {
            tbsReaderView.onStop();
            tbsReaderView = null;
        }
    }

    @Override
    public boolean onGenericMotion(View v, MotionEvent event) {
        LogUtils.e(TAG,"onGenericMotion");
//The input source is a pointing device associated with a display.
//输入源为可显示的指针设备，如：mouse pointing device(鼠标指针),stylus pointing device(尖笔设备)
        if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
            switch (event.getAction()) {
                // process the scroll wheel movement...处理滚轮事件
                case MotionEvent.ACTION_SCROLL:
                    //获得垂直坐标上的滚动方向,也就是滚轮向下滚
                    if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f) {
                        LogUtils.i("fortest::onGenericMotionEvent", "down");
                    }
                    //获得垂直坐标上的滚动方向,也就是滚轮向上滚
                    else {
                        LogUtils.i("fortest::onGenericMotionEvent", "up");
                    }
                    return true;
            }
        }
        return super.onGenericMotionEvent(event);
    }
}