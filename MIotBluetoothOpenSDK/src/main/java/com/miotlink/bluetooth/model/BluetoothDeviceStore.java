package com.miotlink.bluetooth.model;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluetoothDeviceStore {

    private volatile static BluetoothDeviceStore INSTANCE = null;

    public static synchronized BluetoothDeviceStore getInstance() {
        if (INSTANCE == null) {
            synchronized (BluetoothDeviceStore.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothDeviceStore();
                }
            }
        }
        return INSTANCE;
    }

    private final Map<String, BleModelDevice> mDeviceMap;

    public BluetoothDeviceStore() {
        mDeviceMap = new HashMap<>();
    }


    public BleModelDevice getBleModelDevice(String macCode){
        if (mDeviceMap.containsKey(macCode)){
            return mDeviceMap.get(macCode);
        }
        return null;
    }

    public boolean save(BleModelDevice bleModelDevice) {
        if (bleModelDevice != null) {
            if (!TextUtils.isEmpty(bleModelDevice.getMacAddress())) {
                if (!mDeviceMap.containsKey(bleModelDevice.getMacAddress())) {
                     mDeviceMap.put(bleModelDevice.getMacAddress(), bleModelDevice);
                    return true;
                }
            }
        }
        return false;
    }

    public void addDevice(String macCode, BleModelDevice device) {
        if (device == null) {
            return;
        }
        if (!mDeviceMap.containsKey(macCode)) {
            mDeviceMap.put(macCode, device);
        }
    }

    public void addScanDevice(String macCode, BleModelDevice device) {
        if (mDeviceMap != null && !mDeviceMap.containsKey(macCode)) {
            mDeviceMap.put(macCode, device);
        }
    }

    public void remove(String macCode) {
        if (mDeviceMap != null && mDeviceMap.containsKey(macCode)) {
            mDeviceMap.remove(macCode);
        }
    }



    public void removeDevice(String deviceMac) {

        if (mDeviceMap.containsKey(deviceMac)) {
            mDeviceMap.remove(deviceMac);
        }
    }

    public void clear() {
        mDeviceMap.clear();
    }

    public Map<String, BleModelDevice> getDeviceMap() {
        return mDeviceMap;
    }

    /**
     * 是否存在设备
     *
     * @param address
     * @return
     */
    public boolean isHasDevice(String address) {
        if (mDeviceMap != null && mDeviceMap.containsKey(address)) {
            return true;
        }
        return false;
    }

    public List<BleModelDevice> getDeviceList() {
        final List<BleModelDevice> methodResult = new ArrayList<>(mDeviceMap.values());
        return methodResult;
    }

    @Override
    public String toString() {
        return "BluetoothLeDeviceStore{" +
                "DeviceList=" + getDeviceList() +
                '}';
    }
}
