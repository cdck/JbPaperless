package com.xlk.jbpaperless.view.agenda;


import android.os.Bundle;
import android.os.Environment;
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
public class AgendaActivity extends BaseActivity<AgendaPresenter> implements AgendaContract.View, TbsReaderView.ReaderCallback {
    /**
     * =true 加载的是系统内核（默认），=false 加载的是X5内核
     */
    public static boolean isNeedRestart;
    private android.widget.LinearLayout agendaRoot;
    private android.widget.ProgressBar progressBar;
    private android.widget.ScrollView agendaSv;
    private android.widget.TextView agendaTv;
    private TbsReaderView tbsReaderView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_agenda;
    }

    @Override
    protected void initView() {
        agendaRoot = (LinearLayout) findViewById(R.id.agenda_root);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        agendaSv = (ScrollView) findViewById(R.id.agenda_sv);
        agendaTv = (TextView) findViewById(R.id.agenda_tv);
    }

    @Override
    protected AgendaPresenter initPresenter() {
        return new AgendaPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

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
}