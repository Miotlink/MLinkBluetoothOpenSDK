package com.miotlink.bluetooth.impl;


import com.miotlink.bluetooth.listener.BleDeviceScanListener;

/**
 * USER：create by qiaozhuang on 2024/10/14 14:22
 * EMAIL:qiaozhuang@miotlink.com
 */
interface BleDeviceScanService {

    public boolean isBleEnable();

    public void startScan(BleDeviceScanListener listener) throws Exception;

    public void startScan(long scanPeriod, BleDeviceScanListener listener) throws Exception;

    public void stopScan() throws Exception;
}
