package com.miotlink.bluetooth.impl;

import android.app.Activity;
import android.content.Context;


import com.miotlink.bluetooth.listener.SmartNotifyBindPuListener;
import com.miotlink.bluetooth.listener.SmartNotifyDeviceConnectListener;
import com.miotlink.bluetooth.listener.BleDeviceScanListener;
import com.miotlink.bluetooth.listener.BleSmartConfigListener;
import com.miotlink.bluetooth.listener.BleSmartListener;
import com.miotlink.bluetooth.listener.SmartNotifyOTAListener;
import com.miotlink.bluetooth.listener.SmartNotifyUartDataListener;
import com.miotlink.bluetooth.model.BleModelDevice;

import java.io.File;
import java.util.List;

public interface BleSmartService {

    public void setDebug(boolean isDebug);

    public void init(Context mContext, BleSmartListener mSmartListener) throws Exception;


    public void setServiceUUID(String serviceUuId, String readUuid, String writeUuid, String otaUUid) throws Exception;

    public boolean checkPermission();

    public void requestPermissions(Activity activity, int error);

    public void openBluetooth();

    /**
     * 扫描妙联蓝牙设备
     *
     * @throws Exception
     */
    public void onScan(BleDeviceScanListener bleDeviceScanListener) throws Exception;

    public void onScan(int scanTime, BleDeviceScanListener bleDeviceScanListener) throws Exception;

    public void onScanStop() throws Exception;

    /**
     * 获取扫描设备的信息
     *
     * @param macCode
     * @return
     */
    public BleModelDevice getBleModelDevice(String macCode);


    public BleModelDevice getScanBindDevice(String macCode);

    /**
     * 停止扫描蓝牙设备
     *
     * @throws Exception
     */


    public void connect(String macCode, SmartNotifyDeviceConnectListener mBleDeviceConnectListener) throws Exception;


    public void setSmartNotifyUartDataListener(SmartNotifyUartDataListener smartNotifyUartDataListener) throws Exception;

    /**
     * 开启配网信息
     *
     * @param ssid
     * @param password
     * @throws Exception
     */
    public void onStartSmartConfig(String macCode, String ssid, String password, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception;


    public void onAwsStartSmartConfig(String macCode, String awsNetworkInfo, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception;


    public void onStopSmartConfig(String macCode);

    public boolean isConnect(String macCode);

    /**
     * 发送串口数据
     *
     * @param mac
     * @param data
     * @throws Exception
     */
    public void send(String mac, String data) throws Exception;

    public void send(String mac, byte[] data) throws Exception;


    public void onDisConnect(String macCode) throws Exception;

    public void onDestory() throws Exception;

    public void getDeviceVersion(String macCode) throws Exception;

    public boolean startOta(String macCode, File file, SmartNotifyOTAListener otaListener) throws Exception;

    public void stopOta(String macCode) throws Exception;

    public void bindPu(String macCode, SmartNotifyBindPuListener smartNotifyBindPuListener) throws Exception;

    public void unBindPu(String macCode, int kindId, int modelId) throws Exception;

    List<BleModelDevice> getConnectBleDevices();

    boolean isBleEnable();
}
