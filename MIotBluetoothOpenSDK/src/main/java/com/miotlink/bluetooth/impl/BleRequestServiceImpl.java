package com.miotlink.bluetooth.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.miotlink.bluetooth.listener.SmartNotifyBindPuListener;
import com.miotlink.bluetooth.listener.SmartNotifyDeviceConnectListener;
import com.miotlink.bluetooth.listener.BleDeviceScanListener;
import com.miotlink.bluetooth.listener.BleSmartConfigListener;
import com.miotlink.bluetooth.listener.BleSmartListener;
import com.miotlink.bluetooth.listener.SmartNotifyOTAListener;
import com.miotlink.bluetooth.listener.SmartNotifyUartDataListener;
import com.miotlink.bluetooth.model.BleFactory;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.model.BluetoothDeviceStore;
import com.miotlink.bluetooth.service.Ble;

import com.miotlink.bluetooth.utils.UuidUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * USER：create by qiaozhuang on 2024/11/15 11:22
 * EMAIL:qiaozhuang@miotlink.com
 */
public final class BleRequestServiceImpl implements BleSmartService, Ble.InitCallback {

    private Context mContext = null;

    private BleSmartListener mSmartListener = null;

    private boolean isDebug = false;

    BleDeviceScanService bleDeviceScanService = new BleDeviceSacnServiceImpl();
    BleDeviceConnectService bleDeviceConnectService = new BleDeviceConnectServiceImpl();

    BleDeviceSmartConfigService bleDeviceSmartConfigService = new BleDeviceSmartConfigServiceImpl();

    @Override
    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    BleFactory bleFactory = new BleFactory<BleModelDevice>() {
        @Override
        public BleModelDevice create(String address, String name) {
            return new BleModelDevice(address, name);
        }
    };

    @Override
    public void init(Context mContext, BleSmartListener mSmartListener) throws Exception {
        this.mContext = mContext;
        this.mSmartListener = mSmartListener;

        Ble.options().setLogBleEnable(isDebug)//设置是否输出打印蓝牙日志
                .setThrowBleException(true)//设置是否抛出蓝牙异常
                .setLogTAG("MLink_BLE")//设置全局蓝牙操作日志TAG
                .setAutoConnect(true)//设置是否自动连接
                .setIgnoreRepeat(true)//设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
                .setConnectFailedRetryCount(3)//连接异常时（如蓝牙协议栈错误）,重新连接次数
                .setConnectTimeout(10 * 1000)//设置连接超时时长
                .setScanPeriod(24 * 60 * 60 * 1000)//设置扫描时长
                .setParseScanData(true)
                .setMaxConnectNum(7)//最大连接数量
                .setUuidService(UUID.fromString(UuidUtils.uuid16To128("6600")))//设置主服务的uuid
                .setUuidWriteCha(UUID.fromString(UuidUtils.uuid16To128("6602")))//设置可写特征的uuid
                .setUuidReadCha(UUID.fromString(UuidUtils.uuid16To128("6601")))//设置可读特征的uuid （选填）
                .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128("6601")))//设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
                .setUuidOtaWriteCha(UUID.fromString(UuidUtils.uuid16To128("6603")))
                .setFactory(bleFactory)
                .create(mContext, this);
    }

    @Override
    public void initialization(int error, String errorMessage) {
        try {
            if (mSmartListener != null) {
                mSmartListener.onSmartListener(error, errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setServiceUUID(String serviceUuId, String readUuid, String writeUuid, String otaUUid) throws Exception {
        Ble.options().setUuidService(UUID.fromString(UuidUtils.uuid16To128(serviceUuId)))//设置主服务的uuid
                .setUuidWriteCha(UUID.fromString(UuidUtils.uuid16To128(writeUuid)))//设置可写特征的uuid
                .setUuidReadCha(UUID.fromString(UuidUtils.uuid16To128(readUuid)))//设置可读特征的uuid （选填）
                .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128(readUuid)))//设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
                .setUuidOtaWriteCha(UUID.fromString(UuidUtils.uuid16To128(otaUUid)));
    }

    @Override
    public boolean checkPermission() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        List<String> hasPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                hasPermissions.add(permission);
            }
        }

        if (hasPermissions.size() > 0) {
            return false;
        }
        return true;
    }

    @Override
    public void requestPermissions(Activity activity, int error) {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), error);
    }

    @Override
    public void openBluetooth() {
        Ble.getInstance().turnOnBlueToothNo();
    }

    @Override
    public void onScan(BleDeviceScanListener bleDeviceScanListener) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceScanService.startScan(bleDeviceScanListener);
    }

    @Override
    public void onScan(int scanTime, BleDeviceScanListener bleDeviceScanListener) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceScanService.startScan(scanTime, bleDeviceScanListener);
    }

    @Override
    public void onScanStop() throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceScanService.stopScan();
    }

    @Override
    public BleModelDevice getBleModelDevice(String macCode) {

        return BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
    }

    @Override
    public BleModelDevice getScanBindDevice(String macCode) {
        return BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
    }

    @Override
    public void connect(String macCode, SmartNotifyDeviceConnectListener mBleDeviceConnectListener) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceConnectService.connect(macCode, mBleDeviceConnectListener);
    }

    @Override
    public void setSmartNotifyUartDataListener(SmartNotifyUartDataListener smartNotifyUartDataListener) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceConnectService.setSmartNotifyUartDataListener(smartNotifyUartDataListener);
    }


    @Override
    public void onStartSmartConfig(String macCode, String ssid, String password, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceSmartConfigService.startSmartConfig(macCode, ssid, password, delayMillis, bleSmartConfigListener);
    }

    @Override
    public void onAwsStartSmartConfig(String macCode, String awsNetworkInfo, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceSmartConfigService.onAwsStartSmartConfig(macCode, awsNetworkInfo, delayMillis, bleSmartConfigListener);
    }

    @Override
    public void onStopSmartConfig(String macCode) {
        try {
            bleDeviceSmartConfigService.stopSmartConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String mac, String data) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceConnectService.sendMessage(mac, data);

    }

    @Override
    public void send(String mac, byte[] data) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceConnectService.sendMessage(mac, data);
    }

    @Override
    public void onDisConnect(String macCode) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceConnectService.disconnect(macCode);
    }

    @Override
    public void onDestory() throws Exception {
        Ble.getInstance().released();
    }

    @Override
    public void getDeviceVersion(String macCode) throws Exception {
        if (!checkPermission()) {
            throw new Exception("permission Exception");
        }
        if (!Ble.getInstance().isBleEnable()) {
            throw new Exception("isBleEnable 蓝牙未打开");
        }
        bleDeviceConnectService.getVersion(macCode);
    }

    @Override
    public boolean startOta(String macCode, File file, SmartNotifyOTAListener otaListener) throws Exception {
        return false;
    }

    @Override
    public void stopOta(String macCode) throws Exception {

    }

    @Override
    public void bindPu(String macCode, SmartNotifyBindPuListener smartNotifyBindPuListener) throws Exception {
        bleDeviceConnectService.bindPu(macCode,smartNotifyBindPuListener);
    }

    @Override
    public void unBindPu(String macCode, int kindId, int modelId) throws Exception {
        bleDeviceConnectService.unBindPu(macCode, kindId, modelId);
    }

    @Override
    public List<BleModelDevice> getConnectBleDevices() {
        return bleDeviceConnectService.getConnectBleDevices();
    }

    @Override
    public boolean isBleEnable() {
        return bleDeviceScanService.isBleEnable();
    }
}
