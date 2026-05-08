package com.miotlink.bluetooth.command;

import java.nio.ByteBuffer;

/**
 * 心跳命令
 * USER：create by qiaozhuang on 2024/11/14 16:10
 * EMAIL:qiaozhuang@miotlink.com
 */
public class HeartbeatCommand extends AbsMessage {

    private static final byte CMD_HEARTBEAT = 0x01;
    private static final byte SUB_CMD = 0x01;
    private static final byte DATA_LENGTH = 0x04;

    @Override
    protected byte[] getCommandData() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put(CMD_HEARTBEAT);
        buffer.put(SUB_CMD);
        buffer.put(DATA_LENGTH);
        buffer.putInt((int) (System.currentTimeMillis() / 1000));
        return buffer.array();
    }
}