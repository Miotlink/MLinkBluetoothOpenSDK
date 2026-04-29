package com.miotlink.bluetooth.request;


import com.miotlink.bluetooth.annotation.Implement;
import com.miotlink.bluetooth.callback.BleReadRssiCallback;
import com.miotlink.bluetooth.callback.wrapper.ReadRssiWrapperCallback;
import com.miotlink.bluetooth.model.BleDevice;
import com.miotlink.bluetooth.service.BleRequestImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(ReadRssiRequest.class)
public class ReadRssiRequest<T extends BleDevice> implements ReadRssiWrapperCallback<T> {

    private final Map<String, BleReadRssiCallback<T>> readRssiCallbackMap = new ConcurrentHashMap<>();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean readRssi(T device, BleReadRssiCallback<T> callback){
        if (device != null && callback != null) {
            readRssiCallbackMap.put(device.getBleAddress(), callback);
        }
        boolean result = false;
        if (bleRequest != null) {
            result = bleRequest.readRssi(device.getBleAddress());
        }
        return result;
    }

    @Override
    public void onReadRssiSuccess(T device, int rssi) {
        BleReadRssiCallback<T> readRssiCallback = device == null ? null : readRssiCallbackMap.remove(device.getBleAddress());
        if(readRssiCallback != null){
            readRssiCallback.onReadRssiSuccess(device, rssi);
        }
    }
}
