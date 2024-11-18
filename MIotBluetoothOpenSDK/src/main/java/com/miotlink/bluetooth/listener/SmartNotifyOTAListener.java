package com.miotlink.bluetooth.listener;

/**
 * USER：create by qiaozhuang on 2024/11/15 11:52
 * EMAIL:qiaozhuang@miotlink.com
 */
public interface SmartNotifyOTAListener {
    public void checkOtaStateListener(String macCode,int version)throws Exception;

    public void firmwareOtaListener(String macCode,int version)throws Exception;

    public void progress(String macCode,int porgress)throws Exception;

}
