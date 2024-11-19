package com.miotlink.bluetooth.listener;

/**
 * USER：create by qiaozhuang on 2024/11/15 11:52
 * EMAIL:qiaozhuang@miotlink.com
 */
public interface SmartNotifyBindPuListener {
    public void notifyBindPuListener(String macCode,int errorCode,String errorMessage)throws Exception;



}
