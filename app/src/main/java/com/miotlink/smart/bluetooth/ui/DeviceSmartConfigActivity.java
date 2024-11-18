package com.miotlink.smart.bluetooth.ui;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miotlink.MLinkSmartBluetoothSDK;
import com.miotlink.bluetooth.listener.BleSmartConfigListener;
import com.miotlink.smart.bluetooth.R;
import com.miotlink.smart.bluetooth.base.BaseActivity;
import com.miotlink.smart.bluetooth.view.RadarView;
import com.miotlink.smart.bluetooth.view.StatusBarUtils;

public class DeviceSmartConfigActivity extends BaseActivity implements BleSmartConfigListener {

    public static void startIntent(Context mContext, Bundle bundle) {
        Intent intent = new Intent(mContext, DeviceSmartConfigActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    private TextView routeNameTv;
    private EditText routePassEv;
    private Button startButton;
    private TextView resTv;
    private RelativeLayout relativeLayout;
    String routeName = "";
    String routePass = "";
    private boolean isStart = false;
    private String macCode = "";
    private RadarView radarView = null;

    private int configType = 0;

    @Override
    public void initView() throws Exception {
        StatusBarUtils.setColor(mContext, getResources().getColor(R.color.white));
        StatusBarUtils.setTextDark(mContext, true);
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);

        macCode = getIntent().getStringExtra("macCode");
        configType = getIntent().getIntExtra("configType", 0);
        routeNameTv = findViewById(R.id.route_name_tv);
        routePassEv = findViewById(R.id.route_password_tv);
        startButton = findViewById(R.id.start_smart);
        relativeLayout = findViewById(R.id.scan_blue_rl);
        radarView = findViewById(R.id.radar);
        resTv = findViewById(R.id.route_value_tv);
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null) {
                    String ssid = connectionInfo.getSSID();
                    if (!TextUtils.isEmpty(ssid)) {
                        ssid = ssid.replaceAll("\"", "");
                        routeNameTv.setText(ssid);
                    }
                }
            }
        }
        startButton.setOnClickListener((view) -> {
            if (isStart) {
                Toast.makeText(mContext, "配网中", Toast.LENGTH_SHORT).show();
                return;
            }

            routeName = routeNameTv.getText().toString();
            routePass = routePassEv.getText().toString();
            if (TextUtils.isEmpty(routeName)) {
                Toast.makeText(mContext, "输入路由器账户", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(routePass)) {
                Toast.makeText(mContext, "输入路由器密码", Toast.LENGTH_SHORT).show();
                return;
            }
            isStart = true;
            radarView.start();
            relativeLayout.setVisibility(View.VISIBLE);
            MLinkSmartBluetoothSDK.getInstance().startSmartConfig(macCode, routeName, routePass, 60, this);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MLinkSmartBluetoothSDK.getInstance().onStopSmartConfig(macCode);
    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_wifi_device;
    }

    @Override
    public void onLinkSmartConfigListener(int error, String errorMessage, String data) throws Exception {
        isStart = false;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                radarView.stop();
                relativeLayout.setVisibility(View.GONE);
                resTv.setText(data);
            }
        });
    }

    @Override
    public void onLinkSmartConfigTimeOut(int errorCode, String errorMessage) throws Exception {
        isStart = false;
        radarView.stop();
        relativeLayout.setVisibility(View.GONE);
        MLinkSmartBluetoothSDK.getInstance().onStopSmartConfig(macCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                radarView.stop();
                relativeLayout.setVisibility(View.GONE);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
