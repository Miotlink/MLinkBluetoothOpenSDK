package com.miotlink.bluetooth.impl;

import android.text.TextUtils;

import com.miotlink.bluetooth.callback.BleScanCallback;
import com.miotlink.bluetooth.listener.BleDeviceScanListener;
import com.miotlink.bluetooth.model.BleFactory;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.model.BluetoothDeviceStore;
import com.miotlink.bluetooth.model.ScanRecord;
import com.miotlink.bluetooth.service.Ble;


/**
 * USER：create by qiaozhuang on 2024/10/14 14:36
 * EMAIL:qiaozhuang@miotlink.com
 */
class BleDeviceSacnServiceImpl implements BleDeviceScanService {
    Ble<BleModelDevice> ble = null;
    BleFactory bleFactory = new BleFactory<BleModelDevice>() {
        @Override
        public BleModelDevice create(String address, String name) {
            return new BleModelDevice(address, name);
        }
    };
    BleDeviceScanListener bleDeviceScanListener = null;


    @Override
    public void startScan(BleDeviceScanListener bleDeviceScanListener) throws Exception {
        this.bleDeviceScanListener = bleDeviceScanListener;
        BluetoothDeviceStore.getInstance().clear();
        Ble.options().setFactory(bleFactory);
        ble = Ble.getInstance();
        ble.setBleStatusCallback(isOn -> {
            if (isOn) {
                ble.startScan(bleScanCallback);
            } else {
                ble.stopScan();
            }
        });
        if (!ble.isBleEnable()) {
            return;
        }
        ble.startScan(bleScanCallback);
    }

    @Override
    public boolean isBleEnable() {
        return Ble.getInstance().isBleEnable();
    }


    @Override
    public void startScan(long scanPeriod, BleDeviceScanListener bleDeviceScanListener) throws Exception {

        BluetoothDeviceStore.getInstance().clear();
        this.bleDeviceScanListener = bleDeviceScanListener;
        Ble.options().setFactory(bleFactory);
        ble = Ble.getInstance();
        ble.setBleStatusCallback(isOn -> {
            if (isOn) {
                ble.startScan(bleScanCallback);
            } else {
                ble.stopScan();
            }
        });
        if (!ble.isBleEnable()) {
            return;
        }
        ble.startScan(bleScanCallback, scanPeriod);
    }

    @Override
    public void stopScan() throws Exception {
        Ble.getInstance().stopScan();
    }

    BleScanCallback<BleModelDevice> bleScanCallback = new BleScanCallback<BleModelDevice>() {

        @Override
        public void onLeScan(BleModelDevice bleModelDevice, int rssi, byte[] scanRecord) {
            bleModelDevice.setScanRecord(ScanRecord.parseFromBytes(scanRecord));
            bleModelDevice.setRssi(rssi);
            if ((!TextUtils.isEmpty(bleModelDevice.getBleName()) && bleModelDevice.getBleName().startsWith("Hi-Huawei-Mars")) ||
                    (!TextUtils.isEmpty(bleModelDevice.getBleName()) && bleModelDevice.getBleName().startsWith("MLink_")) ||
                    (bleModelDevice.getScanRecord() != null && bleModelDevice.getScanRecord().getManufacturerSpecificData().get(0x6667) != null)) {
                if (BluetoothDeviceStore.getInstance().save(bleModelDevice)) {
                    try {
                        if (bleModelDevice.getMark() == 0) {
                            bleDeviceScanListener.onHasBindDiscoverDevices(bleModelDevice);
                            return;
                        }
                        bleDeviceScanListener.onDiscoverDevices(bleModelDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


}
