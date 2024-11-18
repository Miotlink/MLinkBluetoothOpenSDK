package com.miotlink.bluetooth.listener;




/**
 * USER：create by qiaozhuang on 2024/10/14 13:57
 * EMAIL:qiaozhuang@miotlink.com
 */
public interface SmartNotifyDeviceConnectListener {

    /**
     * 连接通知状态
     * @param errorCode  DISCONNECT = 0 CONNECTING = 1 CONNECTED = 2
     * @param errorMessage
     */
    public void onSmartNotifyConnectListener(int errorCode, String errorMessage, String macCode);

}
