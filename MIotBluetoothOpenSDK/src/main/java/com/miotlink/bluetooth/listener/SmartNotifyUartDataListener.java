package com.miotlink.bluetooth.listener;

/**
 * USER：create by qiaozhuang on 2024/11/15 11:52
 * EMAIL:qiaozhuang@miotlink.com
 */
public interface SmartNotifyUartDataListener {

    public void onSmartNotifyUartDataListener(String macCode, String command) throws Exception;

    public void onSmartNotifyBindListener(String macCode, int errorCode, String errorMessage);

    public void onSmartNotifyDeviceVersionListener(String macCode, int version);

    public void onNotifyUartData(String macCode, int errorCode, String errorMessage);
}
