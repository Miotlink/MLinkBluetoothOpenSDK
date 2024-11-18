package com.miotlink.smart.bluetooth.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;


import com.miotlink.MLinkSmartBluetoothSDK;
import com.miotlink.smart.bluetooth.R;
import com.miotlink.smart.bluetooth.base.BaseActivity;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoadingActivity extends BaseActivity {


    @Override
    public void initView() throws Exception {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        new Handler().postDelayed(() -> PermissionX.init(LoadingActivity.this).permissions(permissions.toArray(new String[permissions.size()])).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                if (allGranted) {
                    if (MLinkSmartBluetoothSDK.getInstance().checkPermissions()) {
                        MLinkSmartBluetoothSDK.getInstance().openPermissions(LoadingActivity.this, 1001);
                    }
                    mContext.startActivity(new Intent(mContext, ScanDeviceActivity.class));
                    finish();

                }
            }
        }), 3000);


    }

    @Override
    public int getContentView() {
        return R.layout.activity_loading;
    }


    @Override
    public void initData() throws Exception {

    }
}
