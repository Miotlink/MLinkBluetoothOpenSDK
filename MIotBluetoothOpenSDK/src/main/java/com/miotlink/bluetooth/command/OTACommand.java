package com.miotlink.bluetooth.command;

import androidx.annotation.NonNull;


import com.miotlink.bluetooth.utils.ByteUtils;
import com.miotlink.bluetooth.utils.HexUtil;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * USER：create by qiaozhuang on 2024/11/14 17:50
 * EMAIL:qiaozhuang@miotlink.com
 */
public class OTACommand extends AbsMessage {
    File file;
    public OTACommand(File file) {
       this.file=file;
    }

    @NonNull
    @Override
    public String toString() {

        byte[] array = null;
        try {
            CRC32 crc32 = new CRC32();
            FileInputStream fileinputstream = new FileInputStream(file);
            int available = fileinputstream.available();
            byte[] fileByte = ByteUtils.toByteArray(file);
            crc32.update(fileByte);
            long crcValue = crc32.getValue();
            ByteBuffer buffer = ByteBuffer.allocate(11);
            buffer.put((byte) 0x0A);
            buffer.put((byte) 0x01);
            buffer.put((byte) 0x08);
            buffer.putInt(available);
            buffer.putInt((int)crcValue);
            array = buffer.array();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HexUtil.encodeHexStr(array);
    }
}
