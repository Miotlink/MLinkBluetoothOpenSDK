package com.miotlink.bluetooth.impl;


import com.miotlink.bluetooth.listener.SmartNotifyDeviceConnectListener;
import com.miotlink.bluetooth.listener.SmartNotifyUartDataListener;

/**
 * USER：create by qiaozhuang on 2024/10/14 14:24
 * EMAIL:qiaozhuang@miotlink.com
 */
 interface BleDeviceConnectService {

    public void connect(String macCode, SmartNotifyDeviceConnectListener bleDeviceConnectListener) throws Exception;

    public void sendMessage(String macCode, byte[] bytes) throws Exception;

    public void setSmartNotifyUartDataListener(SmartNotifyUartDataListener smartNotifyUartDataListener) throws Exception;

    public void sendMessage(String macCode, String ttContent) throws Exception;

    public void getVersion(String macCode) throws Exception;

    public void unBindPu(String macCode, int kindId, int modelId) throws Exception;

    public void bindPu(String macCode)throws Exception;
    public void disconnect(String macCode) throws Exception;
}
