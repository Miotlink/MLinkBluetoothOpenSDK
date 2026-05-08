package com.miotlink.bluetooth.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.miotlink.bluetooth.exception.BleException;
import com.miotlink.bluetooth.exception.BleErrorCode;
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
import java.util.List;
import java.util.UUID;

/**
 * BLE 服务请求实现类
 * <p>
 * USER：create by qiaozhuang on 2024/11/15 11:22
 * EMAIL: qiaozhuang@miotlink.com
 */
public final class BleRequestServiceImpl implements BleSmartService, Ble.InitCallback {

    private Context mContext;
    private BleSmartListener mSmartListener;
    private boolean isDebug = false;

    private final BleDeviceScanService bleDeviceScanService = new BleDeviceSacnServiceImpl();
    private final BleDeviceConnectService bleDeviceConnectService = new BleDeviceConnectServiceImpl();
    private final BleDeviceSmartConfigService bleDeviceSmartConfigService = new BleDeviceSmartConfigServiceImpl();

    private final BleFactory<BleModelDevice> bleFactory = new BleFactory<BleModelDevice>() {
        @Override
        public BleModelDevice create(String address, String name) {
            return new BleModelDevice(address, name);
        }
    };

    // ==================== 配置方法 ====================

    @Override
    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    @Override
    public void init(Context context, BleSmartListener listener) throws BleException {
        if (context == null || listener == null) {
            throw new BleException(BleErrorCode.INVALID_PARAM, "Context or listener is null");
        }
        this.mContext = context.getApplicationContext();
        this.mSmartListener = listener;

        try {
            Ble.options()
                    .setLogBleEnable(isDebug)
                    .setThrowBleException(true)
                    .setLogTAG("MLink_BLE")
                    .setAutoConnect(true)
                    .setIgnoreRepeat(true)
                    .setConnectFailedRetryCount(3)
                    .setConnectTimeout(10 * 1000)
                    .setScanPeriod(24 * 60 * 60 * 1000)
                    .setParseScanData(true)
                    .setMaxConnectNum(7)
                    .setUuidService(createUuid("6600"))
                    .setUuidWriteCha(createUuid("6602"))
                    .setUuidReadCha(createUuid("6601"))
                    .setUuidNotifyCha(createUuid("6601"))
                    .setUuidOtaWriteCha(createUuid("6603"))
                    .setFactory(bleFactory)
                    .create(mContext, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void setServiceUUID(String serviceUuid, String readUuid, String writeUuid, String otaUuid) throws Exception {
        try {
            Ble.options()
                    .setUuidService(createUuid(serviceUuid))
                    .setUuidWriteCha(createUuid(writeUuid))
                    .setUuidReadCha(createUuid(readUuid))
                    .setUuidNotifyCha(createUuid(readUuid))
                    .setUuidOtaWriteCha(createUuid(otaUuid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== 权限与蓝牙状态 ====================

    @Override
    public boolean checkPermission() {
        List<String> permissions = getRequiredPermissions();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void requestPermissions(Activity activity, int requestCode) {
        if (activity == null) return;

        List<String> permissions = getRequiredPermissionsForRequest();
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), requestCode);
        }
    }

    @Override
    public void openBluetooth() {
        Ble.getInstance().turnOnBlueToothNo();
    }

    @Override
    public void setMTU(int mtu) {
        Ble.options().setMTU(mtu);
    }

    @Override
    public boolean isBleEnable() {
        return Ble.getInstance().isBleEnable();
    }

    // ==================== 扫描相关 ====================

    @Override
    public void onScan(BleDeviceScanListener listener) throws Exception {
        ensureBluetoothReady();
        bleDeviceScanService.startScan(listener);
    }

    @Override
    public void onScan(int scanTime, BleDeviceScanListener listener) throws Exception {
        ensureBluetoothReady();
        bleDeviceScanService.startScan(scanTime, listener);
    }

    @Override
    public void onScanStop() throws Exception {
        ensureBluetoothReady();
        bleDeviceScanService.stopScan();
    }

    // ==================== 设备获取 ====================

    @Override
    public BleModelDevice getBleModelDevice(String macCode) {
        return BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
    }

    @Override
    public BleModelDevice getScanBindDevice(String macCode) {
        return BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
    }

    // ==================== 连接相关 ====================

    @Override
    public void connect(String macCode, SmartNotifyDeviceConnectListener listener) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        bleDeviceConnectService.connect(macCode, listener);
    }

    @Override
    public void setSmartNotifyUartDataListener(SmartNotifyUartDataListener smartNotifyUartDataListener) throws Exception {
        ensureBluetoothReady();
        bleDeviceConnectService.setSmartNotifyUartDataListener(smartNotifyUartDataListener);
    }

    @Override
    public void onStartSmartConfig(String macCode, String ssid, String password, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        validateNetworkParams(ssid, password);
        bleDeviceSmartConfigService.startSmartConfig(macCode, ssid, password, delayMillis, bleSmartConfigListener);
    }

    @Override
    public void onAwsStartSmartConfig(String macCode, String awsNetworkInfo, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        if (awsNetworkInfo == null || awsNetworkInfo.isEmpty()) {
            throw new BleException(BleErrorCode.INVALID_PARAM, "AWS network info cannot be empty");
        }
        bleDeviceSmartConfigService.onAwsStartSmartConfig(macCode, awsNetworkInfo, delayMillis, bleSmartConfigListener);
    }

    @Override
    public void onStopSmartConfig(String macCode) {
        try {
            bleDeviceSmartConfigService.stopSmartConfig();
        } catch (Exception e) {

        }
    }


    @Override
    public void send(String macCode, String data) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        bleDeviceConnectService.sendMessage(macCode, data);
    }

    @Override
    public void send(String macCode, byte[] data) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        bleDeviceConnectService.sendMessage(macCode, data);
    }


    @Override
    public void onDestory() throws Exception {
        Ble.getInstance().released();
    }

    @Override
    public void disconnect(String macCode) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        bleDeviceConnectService.disconnect(macCode);
    }

    @Override
    public void getDeviceVersion(String macCode) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        bleDeviceConnectService.getVersion(macCode);
    }

    @Override
    public boolean isConnect(String macCode) {
        return bleDeviceConnectService.isConnect(macCode);
    }

    @Override
    public List<BleModelDevice> getConnectBleDevices() {
        List<BleModelDevice> devices = bleDeviceConnectService.getConnectBleDevices();
        return devices != null ? devices : new ArrayList<>();
    }

    @Override
    public boolean startOta(String macCode, File file, SmartNotifyOTAListener listener) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        if (file == null || !file.exists() || !file.isFile()) {
            throw new BleException(BleErrorCode.INVALID_PARAM, "OTA file is invalid");
        }
        throw new BleException(BleErrorCode.NOT_IMPLEMENTED, "OTA not implemented yet");
    }

    @Override
    public void stopOta(String macCode) throws BleException {
        // TODO: 实现停止 OTA 逻辑
        throw new BleException(BleErrorCode.NOT_IMPLEMENTED, "OTA not implemented yet");
    }

    // ==================== 绑定相关 ====================

    @Override
    public void bindPu(String macCode, SmartNotifyBindPuListener listener) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        bleDeviceConnectService.bindPu(macCode, listener);
    }

    @Override
    public void unBindPu(String macCode, int kindId, int modelId) throws Exception {
        ensureBluetoothReady();
        validateMacAddress(macCode);
        bleDeviceConnectService.unBindPu(macCode, kindId, modelId);
    }


    /**
     * 检查权限和蓝牙状态
     */
    private void ensureBluetoothReady() throws Exception {
        if (!checkPermission()) {
            throw new BleException(BleErrorCode.PERMISSION_DENIED, "Bluetooth permission not granted");
        }
        if (!isBleEnable()) {
            throw new BleException(BleErrorCode.BLUETOOTH_DISABLED, "Bluetooth is not enabled");
        }
    }

    /**
     * 验证 MAC 地址
     */
    private void validateMacAddress(String macCode) throws BleException {
        if (macCode == null || macCode.trim().isEmpty()) {
            throw new BleException(BleErrorCode.INVALID_PARAM, "MAC address cannot be empty");
        }
    }

    /**
     * 验证网络参数
     */
    private void validateNetworkParams(String ssid, String password) throws BleException {
        if (ssid == null || ssid.trim().isEmpty()) {
            throw new BleException(BleErrorCode.INVALID_PARAM, "SSID cannot be empty");
        }
        if (password == null) {
            throw new BleException(BleErrorCode.INVALID_PARAM, "Password cannot be null");
        }
    }

    /**
     * 创建 UUID
     */
    private UUID createUuid(String uuid16) {
        return UUID.fromString(UuidUtils.uuid16To128(uuid16));
    }

    /**
     * 获取需要检查的权限列表（用于检查）
     */
    private List<String> getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return permissions;
    }

    /**
     * 获取需要请求的权限列表（用于请求）
     */
    private List<String> getRequiredPermissionsForRequest() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return permissions;
    }
}