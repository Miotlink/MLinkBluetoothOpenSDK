package com.miotlink.bluetooth.command;


import com.miotlink.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;

/**
 * USER：create by qiaozhuang on 2024/11/14 17:04
 * EMAIL:qiaozhuang@miotlink.com
 */
public class DeviceVersionCommand extends AbsMessage {

    @Override
    public String toString() {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put((byte) 0x09);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x04);
        buffer.putInt((int) System.currentTimeMillis() / 1000);
        byte[] array = buffer.array();
        return HexUtil.encodeHexStr(array);
    }
}
