package com.miotlink.bluetooth.impl;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;


import com.miotlink.bluetooth.callback.BleConnectCallback;
import com.miotlink.bluetooth.callback.BleNotifyCallback;
import com.miotlink.bluetooth.callback.BleWriteCallback;
import com.miotlink.bluetooth.command.OTACommand;
import com.miotlink.bluetooth.listener.SmartNotifyOTAListener;
import com.miotlink.bluetooth.model.BleFactory;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.model.BluetoothDeviceStore;
import com.miotlink.bluetooth.service.Ble;

import java.io.File;

/**
 * USER：create by qiaozhuang on 2024/10/15 17:07
 * EMAIL:qiaozhuang@miotlink.com
 */
 class BleDeviceOTAServiceImpl extends BleConnectCallback<BleModelDevice> implements BleDeviceOTAService {

    Ble<BleModelDevice> ble = null;
    BleFactory bleFactory = new BleFactory<BleModelDevice>() {
        @Override
        public BleModelDevice create(String address, String name) {
            return new BleModelDevice(address, name);
        }
    };

    private BleDeviceOTAServiceImpl() {
        ble = Ble.getInstance();
    }

    private SmartNotifyOTAListener otaListener = null;

    private File file = null;

    private String address = "";

    @Override
    public boolean startOta(String macCode, File file, SmartNotifyOTAListener otaListener) throws Exception {

        this.otaListener = otaListener;
        this.file = file;
        if (ble == null) {
            ble = Ble.getInstance();
        }
        this.address = macCode;
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice == null) {
            return false;
        }
        BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
        if (connectedDevice != null) {
            OTACommand otaCommand = new OTACommand(file);
            ble.writeByUuid(bleModelDevice, otaCommand.pack(), Ble.options().getUuidService(), Ble.options().getUuidWriteCha(), bleWriteCallback);
            ble.enableNotify(connectedDevice, true, bleNotifyCallback);
            return true;
        }


        return false;
    }


    @Override
    public void stopOta(String macCode) throws Exception {

    }

    BleWriteCallback<BleModelDevice> bleWriteCallback = new BleWriteCallback<BleModelDevice>() {
        @Override
        public void onWriteSuccess(BleModelDevice device, BluetoothGattCharacteristic characteristic) {

        }

        @Override
        public void onWriteFailed(BleModelDevice device, int failedCode) {
            super.onWriteFailed(device, failedCode);
        }
    };

    @Override
    public void onConnectionChanged(BleModelDevice device) {

    }

    @Override
    public void onServicesDiscovered(BleModelDevice device, BluetoothGatt gatt) {
        super.onServicesDiscovered(device, gatt);
        ble.enableNotify(device, true, bleNotifyCallback);
    }

    BleNotifyCallback<BleModelDevice> bleNotifyCallback = new BleNotifyCallback<BleModelDevice>() {
        @Override
        public void onChanged(BleModelDevice device, BluetoothGattCharacteristic characteristic) {

        }
    };
}
