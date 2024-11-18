package com.miotlink.bluetooth.command;

import androidx.annotation.NonNull;

import com.miotlink.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;

public class BindPuCommand extends AbsMessage{

    private int kindId=0;
    private int modelId=0;


    @NonNull
    @Override
    public String toString() {
        ByteBuffer buffer = ByteBuffer.allocate(4+3);
        buffer.put((byte) 0x07);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x01);
        byte[] array = buffer.array();
        return HexUtil.encodeHexStr(array);

    }
}
