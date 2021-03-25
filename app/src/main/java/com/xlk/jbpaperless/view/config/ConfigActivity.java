package com.xlk.jbpaperless.view.config;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xlk.jbpaperless.R;
import com.xlk.jbpaperless.base.BaseActivity;

public class ConfigActivity extends BaseActivity<ConfigPresenter> implements ConfigContract.View {

    private android.widget.RelativeLayout rlTop;
    private android.widget.ImageView ivClose;
    private android.widget.ImageView dividingLineHorizontal;
    private android.widget.TextView ntvServer;
    private android.widget.LinearLayout nllServer;
    private android.widget.EditText edtHostIp;
    private android.widget.Button btnHostEnsure;
    private android.widget.Button btnDetectionUpgrade;
    private android.widget.TextView ntvLocal;
    private android.widget.LinearLayout nllLocal;
    private android.widget.EditText edtLocalIp;
    private android.widget.Button btnLocalEnsure;
    private android.widget.Button btnClearCache;
    private android.widget.Button btnOtherSettings;
    private android.widget.LinearLayout nllCache;
    private android.widget.CheckBox cbCache;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_config;
    }

    @Override
    protected void initView() {
        rlTop = (RelativeLayout) findViewById(R.id.rl_top);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        dividingLineHorizontal = (ImageView) findViewById(R.id.dividing_line_horizontal);
        ntvServer = (TextView) findViewById(R.id.ntv_server);
        nllServer = (LinearLayout) findViewById(R.id.nll_server);
        edtHostIp = (EditText) findViewById(R.id.edt_host_ip);
        btnHostEnsure = (Button) findViewById(R.id.btn_host_ensure);
        btnDetectionUpgrade = (Button) findViewById(R.id.btn_detection_upgrade);
        ntvLocal = (TextView) findViewById(R.id.ntv_local);
        nllLocal = (LinearLayout) findViewById(R.id.nll_local);
        edtLocalIp = (EditText) findViewById(R.id.edt_local_ip);
        btnLocalEnsure = (Button) findViewById(R.id.btn_local_ensure);
        btnClearCache = (Button) findViewById(R.id.btn_clear_cache);
        btnOtherSettings = (Button) findViewById(R.id.btn_other_settings);
        nllCache = (LinearLayout) findViewById(R.id.nll_cache);
        ivClose.setOnClickListener(v -> {
            onBackPressed();
        });
        btnHostEnsure.setOnClickListener(v -> {

        });
        //检测升级
        btnDetectionUpgrade.setOnClickListener(v -> {

        });
        btnLocalEnsure.setOnClickListener(v -> {
            String ip = edtLocalIp.getText().toString();
            if (!ip.isEmpty() && RegexUtils.isIP(ip)) {
                ini.put("areaaddr", "area0ip", ip);
                ini.store();
                AppUtils.relaunchApp(true);
            } else {
                ToastUtils.showShort(R.string.ip_format_err);
            }
        });
        //清除缓存
        btnClearCache.setOnClickListener(v -> {

        });
        //其它设置
        btnOtherSettings.setOnClickListener(v -> {

        });
        cbCache = (CheckBox) findViewById(R.id.cb_cache);
        cbCache.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ini.put("other", "autoCache", isChecked ? 1 : 0);
                ini.store();
            }
        });
    }

    @Override
    protected ConfigPresenter initPresenter() {
        return new ConfigPresenter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        String localIp = ini.get("areaaddr", "area0ip");
        String autoCache = ini.get("other", "autoCache");
        int i = Integer.parseInt(autoCache);
        edtLocalIp.setText(localIp);
        cbCache.setChecked(i == 1);
    }
}