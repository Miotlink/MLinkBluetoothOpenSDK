package com.miotlink.bluetooth.command;

import java.nio.ByteBuffer;

/**
 * 产品信息查询命令
 * USER：create by qiaozhuang on 2024/11/14 17:47
 * EMAIL:qiaozhuang@miotlink.com
 */
public class ProductInfoCommand extends AbsMessage {

    private final int code;

    public ProductInfoCommand(int code) {
        this.code = code;
    }

    @Override
    protected byte[] getCommandData() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put((byte) code);
        return buffer.array();
    }
}