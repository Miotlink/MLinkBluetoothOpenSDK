package com.miotlink.bluetooth.command;

import androidx.annotation.NonNull;


import com.miotlink.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;

/**
 * USER：create by qiaozhuang on 2024/11/14 17:47
 * EMAIL:qiaozhuang@miotlink.com
 */
public class ProductInfoCommand extends AbsMessage {

    int code = 0;

    public ProductInfoCommand(int code) {
        this.code = code;
    }

    @NonNull
    @Override
    public String toString() {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put((byte) code);
        byte[] array = buffer.array();
        return HexUtil.encodeHexStr(array);
    }
}
