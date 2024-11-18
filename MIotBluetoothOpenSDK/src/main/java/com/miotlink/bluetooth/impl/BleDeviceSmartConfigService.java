package com.miotlink.bluetooth.impl;

import com.miotlink.bluetooth.listener.BleSmartConfigListener;

/**
 * USER：create by qiaozhuang on 2024/10/14 14:25
 * EMAIL:qiaozhuang@miotlink.com
 */
 interface BleDeviceSmartConfigService {

    public void startSmartConfig(String macCode, String routeName, String password, int delayMillis, BleSmartConfigListener bleSmartConfigListener)throws Exception;

    public void onAwsStartSmartConfig(String macCode, String awsNetworkInfo, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception;

    public void stopSmartConfig()throws Exception;
}
