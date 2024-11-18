package com.miotlink.bluetooth.callback.wrapper;

/**
 * Created by LiuLei on 2018/6/2.
 */

public interface MtuWrapperCallback<T> {

    void onMtuChanged(T device, int mtu, int status);

}
