package com.miotlink.bluetooth.request;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;

import androidx.core.os.HandlerCompat;


import com.miotlink.bluetooth.annotation.Implement;
import com.miotlink.bluetooth.callback.BleScanCallback;
import com.miotlink.bluetooth.callback.wrapper.BleWrapperCallback;
import com.miotlink.bluetooth.callback.wrapper.ScanWrapperCallback;
import com.miotlink.bluetooth.model.BleDevice;
import com.miotlink.bluetooth.model.ScanRecord;
import com.miotlink.bluetooth.scan.BleScannerCompat;
import com.miotlink.bluetooth.service.Ble;
import com.miotlink.bluetooth.service.BleHandler;
import com.miotlink.bluetooth.service.BleStates;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiuLei on 2017/10/21.
 */
@Implement(ScanRequest.class)
public class ScanRequest<T extends BleDevice> implements ScanWrapperCallback {

    private static final String TAG = "ScanRequest";
    private static final String HANDLER_TOKEN = "stop_token";
    private boolean scanning;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BleScanCallback<T> bleScanCallback;
    private final Map<String, T> scanDevices = new HashMap<>();
    private final Handler handler = BleHandler.of();
    private final BleWrapperCallback<T> bleWrapperCallback = Ble.options().getBleWrapperCallback();

    public void startScan(BleScanCallback<T> callback, long scanPeriod) {
        if (callback == null) throw new IllegalArgumentException("BleScanCallback can not be null!");
        scanDevices.clear();
        bleScanCallback = callback;
        if (!isEnableInternal()) {
            return;
        }
        if (scanning) {
            if (bleScanCallback != null){
                bleScanCallback.onScanFailed(BleStates.ScanAlready);
            }
            return;
        }
        if (scanPeriod >= 0){
            HandlerCompat.postDelayed(handler, new Runnable() {
                @Override
                public void run() {
                    if (scanning) {
                        stopScan();
                    }
                }
            }, HANDLER_TOKEN, scanPeriod);
        }
        BleScannerCompat.getScanner().startScan(this);
    }

    private boolean isEnableInternal() {
        if (!bluetoothAdapter.isEnabled()) {
            if (bleScanCallback != null) {
                bleScanCallback.onScanFailed(BleStates.BluetoothNotOpen);
                return false;
            }
        }
        return true;
    }

    public void stopScan() {
        if (!isEnableInternal()) {
            return;
        }
        if (!scanning) {
            if (bleScanCallback != null) {
                bleScanCallback.onScanFailed(BleStates.ScanStopAlready);
            }
            return;
        }
        handler.removeCallbacksAndMessages(HANDLER_TOKEN);
        BleScannerCompat.getScanner().stopScan();
    }

    @Override
    public void onStart() {
        scanning = true;
        if (bleScanCallback != null) {
            bleScanCallback.onStart();
        }

        if(bleWrapperCallback != null){
            bleWrapperCallback.onStart();
        }
    }

    @Override
    public void onStop() {
        scanning = false;
        if (bleScanCallback != null) {
            bleScanCallback.onStop();
            bleScanCallback = null;
        }
        if(bleWrapperCallback != null){
            bleWrapperCallback.onStop();
        }

    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        try {
            if (device == null) return;
            String address = device.getAddress();
            T bleDevice = getDevice(address);
            if (bleDevice == null) {
                bleDevice = (T) Ble.options().getFactory().create(address, device.getName());
                bleDevice.setDeviceType(device.getType());
                if (bleScanCallback != null) {
                    bleScanCallback.onLeScan(bleDevice, rssi, scanRecord);
                }
                if (bleWrapperCallback != null){
                    bleWrapperCallback.onLeScan(bleDevice, rssi, scanRecord);
                }
                scanDevices.put(device.getAddress(), bleDevice);
            } else {
                if (!Ble.options().isIgnoreRepeat) {//无需过滤
                    if (bleScanCallback != null) {
                        bleScanCallback.onLeScan(bleDevice, rssi, scanRecord);
                    }
                    if (bleWrapperCallback != null){
                        bleWrapperCallback.onLeScan(bleDevice, rssi, scanRecord);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        if (bleScanCallback != null) {
            bleScanCallback.onScanFailed(errorCode);
        }
    }

    @Override
    public void onParsedData(BluetoothDevice device, ScanRecord scanRecord) {
        if (bleScanCallback != null) {
            T bleDevice = getDevice(device.getAddress());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bleScanCallback.onParsedData(bleDevice, scanRecord);
            }
        }
    }

    public boolean isScanning() {
        return scanning;
    }

    //获取已扫描到的设备（重复设备）
    private T getDevice(String address) {
        return scanDevices.get(address);
    }

    public void cancelScanCallback(){
        bleScanCallback = null;
    }

}
