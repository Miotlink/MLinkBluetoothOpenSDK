package com.miotlink.bluetooth.command;

import android.text.TextUtils;


import com.miotlink.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;

/**
 * USER：create by qiaozhuang on 2024/11/14 16:10
 * EMAIL:qiaozhuang@miotlink.com
 */
public class SmartDeviceNetworkCommand extends AbsMessage {

    private String ssid = "";

    private String password = "";

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        int ssidLen = 0;
        int passwordLen = 0;
        if (!TextUtils.isEmpty(ssid)) {
            ssidLen = ssid.length();
        }
        if (!TextUtils.isEmpty(password)) {
            passwordLen = password.length();
        }
        ByteBuffer buffer = ByteBuffer.allocate(4 + ssidLen + passwordLen);
        buffer.put((byte) 0x03);
        buffer.put((byte) 0x02);
        buffer.put((byte) ssidLen);
        buffer.put(ssid.getBytes());
        buffer.put((byte) passwordLen);
        buffer.put(password.getBytes());
        byte[] array = buffer.array();
        return HexUtil.encodeHexStr(array);
    }
}
