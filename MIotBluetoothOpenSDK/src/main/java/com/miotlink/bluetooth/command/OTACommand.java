package com.miotlink.bluetooth.command;

import com.miotlink.bluetooth.utils.ByteUtils;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * OTA 升级命令
 * USER：create by qiaozhuang on 2024/11/14 17:50
 * EMAIL:qiaozhuang@miotlink.com
 */
public class OTACommand extends AbsMessage {

    private static final byte CMD_OTA = 0x0A;
    private static final byte SUB_CMD = 0x01;
    private static final byte PROTOCOL_VERSION = 0x08;

    private final File file;

    public OTACommand(File file) {
        this.file = file;
    }

    @Override
    protected byte[] getCommandData() throws Exception {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("OTA file not found: " + file);
        }

        long crcValue;
        byte[] fileBytes = ByteUtils.toByteArray(file);

        // 使用局部变量保证线程安全
        CRC32 crc32 = new CRC32();
        crc32.update(fileBytes);
        crcValue = crc32.getValue();

        ByteBuffer buffer = ByteBuffer.allocate(11);
        buffer.put(CMD_OTA);
        buffer.put(SUB_CMD);
        buffer.put(PROTOCOL_VERSION);
        buffer.putInt(fileBytes.length);
        buffer.putInt((int) crcValue);

        return buffer.array();
    }
}