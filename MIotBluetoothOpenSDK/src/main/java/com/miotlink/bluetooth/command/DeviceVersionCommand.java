package com.miotlink.bluetooth.command;

import java.nio.ByteBuffer;

/**
 * 设备版本查询命令
 * USER：create by qiaozhuang on 2024/11/14 17:04
 * EMAIL:qiaozhuang@miotlink.com
 */
public class DeviceVersionCommand extends AbsMessage {

    private static final byte CMD_VERSION = 0x09;
    private static final byte SUB_CMD = 0x01;
    private static final byte DATA_LENGTH = 0x04;

    @Override
    protected byte[] getCommandData() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put(CMD_VERSION);
        buffer.put(SUB_CMD);
        buffer.put(DATA_LENGTH);
        buffer.putInt((int) (System.currentTimeMillis() / 1000));
        return buffer.array();
    }
}