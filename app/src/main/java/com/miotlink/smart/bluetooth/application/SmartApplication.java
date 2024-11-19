package com.miotlink.smart.bluetooth.application;

import android.app.Application;

import com.miotlink.MLinkSmartBluetoothSDK;


public class SmartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MLinkSmartBluetoothSDK.getInstance().setDebug(false);
        MLinkSmartBluetoothSDK.getInstance().init(this, "", (code, message) -> {

        });
    }
}
