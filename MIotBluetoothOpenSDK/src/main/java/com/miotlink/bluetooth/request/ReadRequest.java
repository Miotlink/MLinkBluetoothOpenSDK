package com.miotlink.bluetooth.request;

import android.bluetooth.BluetoothGattCharacteristic;


import com.miotlink.bluetooth.annotation.Implement;
import com.miotlink.bluetooth.callback.BleReadCallback;
import com.miotlink.bluetooth.callback.wrapper.BleWrapperCallback;
import com.miotlink.bluetooth.callback.wrapper.ReadWrapperCallback;
import com.miotlink.bluetooth.model.BleDevice;
import com.miotlink.bluetooth.service.Ble;
import com.miotlink.bluetooth.service.BleRequestImpl;

import java.util.UUID;

/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(ReadRequest.class)
public class ReadRequest<T extends BleDevice> implements ReadWrapperCallback<T> {

    private BleReadCallback<T> bleReadCallback;
    private final BleWrapperCallback<T> bleWrapperCallback = Ble.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean read(T device, BleReadCallback<T> callback){
        this.bleReadCallback = callback;
        return bleRequest.readCharacteristic(device.getBleAddress());
    }

    public boolean readByUuid(T device, UUID serviceUUID, UUID characteristicUUID, BleReadCallback<T> callback){
        this.bleReadCallback = callback;
        return bleRequest.readCharacteristicByUuid(device.getBleAddress(), serviceUUID, characteristicUUID);
    }

    @Override
    public void onReadSuccess(T device, BluetoothGattCharacteristic characteristic) {
        if(bleReadCallback != null){
            bleReadCallback.onReadSuccess(device, characteristic);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onReadSuccess(device, characteristic);
        }
    }

    @Override
    public void onReadFailed(T device, int failedCode) {
        if(bleReadCallback != null){
            bleReadCallback.onReadFailed(device, failedCode);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onReadFailed(device, failedCode);
        }
    }
}
