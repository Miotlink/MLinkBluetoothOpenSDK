package com.miotlink.bluetooth.command;

import com.miotlink.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * USER：create by qiaozhuang on 2024/11/14 16:10
 * EMAIL:qiaozhuang@miotlink.com
 */
public class SmartDeviceNetworkCommand extends AbsMessage {

    private static final byte PREFIX_1 = 0x03;
    private static final byte PREFIX_2 = 0x02;

    private String ssid = "";

    private String password = "";

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid != null ? ssid : "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password : "";
    }

    @Override
    public String toString() {
        byte[] ssidBytes = ssid.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        int ssidLen = ssidBytes.length;
        int passwordLen = passwordBytes.length;
        if (ssidLen > 0xFF || passwordLen > 0xFF) {
            throw new IllegalArgumentException("ssid or password is too long");
        }
        ByteBuffer buffer = ByteBuffer.allocate(4 + ssidLen + passwordLen);
        buffer.put(PREFIX_1);
        buffer.put(PREFIX_2);
        buffer.put((byte) ssidLen);
        buffer.put(ssidBytes);
        buffer.put((byte) passwordLen);
        buffer.put(passwordBytes);
        return HexUtil.encodeHexStr(buffer.array());
    }
}
