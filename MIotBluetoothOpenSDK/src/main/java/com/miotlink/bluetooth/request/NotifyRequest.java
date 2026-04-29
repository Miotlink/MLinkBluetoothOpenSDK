package com.miotlink.bluetooth.request;

import android.bluetooth.BluetoothGattCharacteristic;


import com.miotlink.bluetooth.annotation.Implement;
import com.miotlink.bluetooth.callback.BleNotifyCallback;
import com.miotlink.bluetooth.callback.wrapper.BleWrapperCallback;
import com.miotlink.bluetooth.callback.wrapper.NotifyWrapperCallback;
import com.miotlink.bluetooth.model.BleDevice;
import com.miotlink.bluetooth.service.Ble;
import com.miotlink.bluetooth.service.BleRequestImpl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LiuLei on 2017/10/23.
 */
@Implement(NotifyRequest.class)
public class NotifyRequest<T extends BleDevice> implements NotifyWrapperCallback<T> {

    private static final String TAG = "NotifyRequest";
    private final Map<String, BleNotifyCallback<T>> notifyCallbackMap = new ConcurrentHashMap<>();
    private final BleWrapperCallback<T> bleWrapperCallback = Ble.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public void notify(T device, boolean enable, BleNotifyCallback<T> callback) {
        if (device != null) {
            if (enable && callback != null) {
                notifyCallbackMap.put(device.getBleAddress(), callback);
            } else if (!enable) {
                notifyCallbackMap.remove(device.getBleAddress());
            }
        }
        bleRequest.setCharacteristicNotification(device.getBleAddress(), enable);
    }

    public void notifyByUuid(T device, boolean enable, UUID serviceUUID, UUID characteristicUUID, BleNotifyCallback<T> callback) {
        if (device != null) {
            if (enable && callback != null) {
                notifyCallbackMap.put(device.getBleAddress(), callback);
            } else if (!enable) {
                notifyCallbackMap.remove(device.getBleAddress());
            }
        }
        bleRequest.setCharacteristicNotificationByUuid(device.getBleAddress(),enable, serviceUUID, characteristicUUID);
    }

    @Deprecated
    public void cancelNotify(T device, BleNotifyCallback<T> callback) {
        if (device != null) {
            notifyCallbackMap.remove(device.getBleAddress());
        }
        bleRequest.setCharacteristicNotification(device.getBleAddress(), false);
    }

    @Override
    public void onChanged(final T device, final BluetoothGattCharacteristic characteristic) {
        BleNotifyCallback<T> notifyCallback = device == null ? null : notifyCallbackMap.get(device.getBleAddress());
        if (null != notifyCallback){
            notifyCallback.onChanged(device, characteristic);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onChanged(device, characteristic);
        }
    }

    @Override
    public void onNotifySuccess(final T device) {
        BleNotifyCallback<T> notifyCallback = device == null ? null : notifyCallbackMap.get(device.getBleAddress());
        if (null != notifyCallback){
            notifyCallback.onNotifySuccess(device);
        }
        if (bleWrapperCallback != null){
            bleWrapperCallback.onNotifySuccess(device);
        }
    }

    @Override
    public void onNotifyFailed(T device, int failedCode) {
        BleNotifyCallback<T> notifyCallback = device == null ? null : notifyCallbackMap.remove(device.getBleAddress());
        if (null != notifyCallback){
            notifyCallback.onNotifyFailed(device, failedCode);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onNotifyFailed(device, failedCode);
        }
    }

    @Override
    public void onNotifyCanceled(T device) {
        BleNotifyCallback<T> notifyCallback = device == null ? null : notifyCallbackMap.remove(device.getBleAddress());
        if (null != notifyCallback){
            notifyCallback.onNotifyCanceled(device);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onNotifyCanceled(device);
        }
    }
}
