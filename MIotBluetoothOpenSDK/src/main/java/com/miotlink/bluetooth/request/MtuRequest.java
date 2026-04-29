package com.miotlink.bluetooth.request;


import com.miotlink.bluetooth.annotation.Implement;
import com.miotlink.bluetooth.callback.BleMtuCallback;
import com.miotlink.bluetooth.callback.wrapper.BleWrapperCallback;
import com.miotlink.bluetooth.callback.wrapper.MtuWrapperCallback;
import com.miotlink.bluetooth.model.BleDevice;
import com.miotlink.bluetooth.service.Ble;
import com.miotlink.bluetooth.service.BleRequestImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by LiuLei on 2017/10/23.
 */
@Implement(MtuRequest.class)
public class MtuRequest<T extends BleDevice> implements MtuWrapperCallback<T> {

    private final Map<String, BleMtuCallback<T>> mtuCallbackMap = new ConcurrentHashMap<>();
    private final BleWrapperCallback<T> bleWrapperCallback = Ble.options().getBleWrapperCallback();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getBleRequest();

    public boolean setMtu(String address, int mtu, BleMtuCallback<T> callback){
        if (address != null && callback != null) {
            mtuCallbackMap.put(address, callback);
        }
        return bleRequest.setMtu(address, mtu);
    }

    @Override
    public void onMtuChanged(T device, int mtu, int status) {
        BleMtuCallback<T> bleMtuCallback = device == null ? null : mtuCallbackMap.remove(device.getBleAddress());
        if(null != bleMtuCallback){
            bleMtuCallback.onMtuChanged(device, mtu, status);
        }

        if (bleWrapperCallback != null){
            bleWrapperCallback.onMtuChanged(device, mtu, status);
        }
    }
}
