package com.miotlink.bluetooth.impl;



import com.miotlink.bluetooth.listener.SmartNotifyOTAListener;

import java.io.File;

/**
 * USER：create by qiaozhuang on 2024/10/15 17:07
 * EMAIL:qiaozhuang@miotlink.com
 */
 interface BleDeviceOTAService {

    public boolean startOta(String macCode, File file, SmartNotifyOTAListener otaListener)throws Exception;
    public void stopOta(String macCode)throws Exception;
}
