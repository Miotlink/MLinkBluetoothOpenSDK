package com.miotlink.bluetooth.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 智能设备配网命令
 * USER：create by qiaozhuang on 2024/11/14 16:10
 * EMAIL:qiaozhuang@miotlink.com
 */
public class SmartDeviceNetworkCommand extends AbsMessage {

    private static final byte CMD_NETWORK = 0x03;
    private static final byte SUB_CMD = 0x02;
    private static final int MAX_FIELD_LENGTH = 0xFF;

    private String ssid;
    private String password;

    public SmartDeviceNetworkCommand() {
        this.ssid = "";
        this.password = "";
    }

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
    protected byte[] getCommandData() throws Exception {
        byte[] ssidBytes = ssid.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        int ssidLen = ssidBytes.length;
        int passwordLen = passwordBytes.length;

        if (ssidLen > MAX_FIELD_LENGTH || passwordLen > MAX_FIELD_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("SSID or password too long. Max: %d, SSID: %d, Password: %d",
                            MAX_FIELD_LENGTH, ssidLen, passwordLen));
        }

        ByteBuffer buffer = ByteBuffer.allocate(4 + ssidLen + passwordLen);
        buffer.put(CMD_NETWORK);
        buffer.put(SUB_CMD);
        buffer.put((byte) ssidLen);
        buffer.put(ssidBytes);
        buffer.put((byte) passwordLen);
        buffer.put(passwordBytes);

        return buffer.array();
    }
}