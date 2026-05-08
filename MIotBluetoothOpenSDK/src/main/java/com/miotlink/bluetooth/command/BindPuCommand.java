package com.miotlink.bluetooth.command;

import java.nio.ByteBuffer;

/**
 * 绑定命令
 */
public class BindPuCommand extends AbsMessage {

    private static final byte CMD_BIND = 0x07;
    private static final byte SUB_CMD = 0x01;

    private final int kindId;
    private final int modelId;

    public BindPuCommand(int kindId, int modelId) {
        this.kindId = kindId;
        this.modelId = modelId;
    }

    @Override
    protected byte[] getCommandData() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put(CMD_BIND);
        buffer.put(SUB_CMD);
        buffer.putShort((short) kindId);
        buffer.putShort((short) modelId);
        return buffer.array();
    }
}