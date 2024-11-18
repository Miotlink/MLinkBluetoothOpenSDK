package com.miotlink.bluetooth.listener;


import com.miotlink.bluetooth.model.BleModelDevice;

/**
 * USER：create by qiaozhuang on 2024/10/14 10:22
 * EMAIL:qiaozhuang@miotlink.com
 */
public interface BleDeviceScanListener {

    public void onDiscoverDevices(BleModelDevice bleModelDevice)throws Exception;

    public void onHasBindDiscoverDevices(BleModelDevice bleModelDevice)throws Exception;

}
