package com.miotlink.bluetooth.command;

import android.text.TextUtils;

import androidx.annotation.NonNull;


import com.miotlink.bluetooth.utils.HexUtil;

import java.nio.ByteBuffer;

/**
 * USER：create by qiaozhuang on 2024/11/14 16:52
 * EMAIL:qiaozhuang@miotlink.com
 */
public class UartCommand extends AbsMessage{


    private String command="";
    public UartCommand(String command){
        this.command=command;
    }


    @NonNull
    @Override
    public String toString() {
        if(!TextUtils.isEmpty(command)){
            int length = HexUtil.decodeHex(command).length;
            ByteBuffer buffer = ByteBuffer.allocate(length+3);
            buffer.put((byte) 0x05);
            buffer.put((byte) 0x01);
            buffer.put((byte) HexUtil.decodeHex(command).length);
            buffer.put( HexUtil.decodeHex(command));
            byte[] array = buffer.array();
            return HexUtil.encodeHexStr(array);
        }
        return "";

    }
}
