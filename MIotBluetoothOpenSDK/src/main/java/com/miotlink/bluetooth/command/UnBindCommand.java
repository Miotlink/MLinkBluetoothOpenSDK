package com.miotlink.bluetooth.command;

import java.nio.ByteBuffer;

/**
 * 解绑命令
 */
public class UnBindCommand extends AbsMessage {

    private static final byte CMD_UNBIND = 0x0C;
    private static final byte SUB_CMD = 0x01;

    private final int kindId;
    private final int modelId;

    public UnBindCommand(int kindId, int modelId) {
        this.kindId = kindId;
        this.modelId = modelId;
    }

    @Override
    protected byte[] getCommandData() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put(CMD_UNBIND);
        buffer.put(SUB_CMD);
        buffer.putShort((short) kindId);
        buffer.putShort((short) modelId);
        return buffer.array();
    }
}