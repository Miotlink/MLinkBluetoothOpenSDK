package com.miotlink.bluetooth.command;



import com.miotlink.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;

/**
 * USER：create by qiaozhuang on 2024/11/14 17:04
 * EMAIL:qiaozhuang@miotlink.com
 */
public class DeviceNameCommand extends AbsMessage {

    @Override
    public String toString() {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put((byte) 0x07);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x01);
        byte[] array = buffer.array();
        return HexUtil.encodeHexStr(array);
    }
}
