package com.miotlink.bluetooth.scan;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.miotlink.bluetooth.callback.wrapper.ScanWrapperCallback;
import com.miotlink.bluetooth.service.Ble;


class BluetoothScannerImplJB extends BleScannerCompat {

    @Override
    public void startScan(ScanWrapperCallback scanWrapperCallback) {
        super.startScan(scanWrapperCallback);
        if (ActivityCompat.checkSelfPermission(Ble.getInstance().getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothAdapter.startLeScan(leScanCallback);
    }

    @Override
    public void stopScan() {
        super.stopScan();
        if (ActivityCompat.checkSelfPermission(Ble.getInstance().getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> scanWrapperCallback.onLeScan(device, rssi, scanRecord);
}
