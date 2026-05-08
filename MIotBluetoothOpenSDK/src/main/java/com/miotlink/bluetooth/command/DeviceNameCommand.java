package com.miotlink.bluetooth.command;

import java.nio.ByteBuffer;

/**
 * 设备名称查询命令
 * USER：create by qiaozhuang on 2024/11/14 17:04
 * EMAIL:qiaozhuang@miotlink.com
 */
public class DeviceNameCommand extends AbsMessage {

    private static final byte CMD_DEVICE_NAME = 0x07;
    private static final byte SUB_CMD = 0x01;

    @Override
    protected byte[] getCommandData() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(CMD_DEVICE_NAME);
        buffer.put(SUB_CMD);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x01);
        return buffer.array();
    }
}