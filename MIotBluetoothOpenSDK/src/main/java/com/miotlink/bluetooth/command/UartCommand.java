package com.miotlink.bluetooth.command;

import android.text.TextUtils;
import com.miotlink.bluetooth.utils.HexUtil;
import java.nio.ByteBuffer;

/**
 * UART 透传命令
 * USER：create by qiaozhuang on 2024/11/14 16:52
 * EMAIL:qiaozhuang@miotlink.com
 */
public class UartCommand extends AbsMessage {

    private static final byte CMD_UART = 0x05;
    private static final byte SUB_CMD = 0x01;

    private final String command;

    public UartCommand(String command) {
        this.command = command != null ? command : "";
    }

    @Override
    protected byte[] getCommandData() throws Exception {
        if (TextUtils.isEmpty(command)) {
            throw new IllegalArgumentException("UART command is empty");
        }

        byte[] decodedCommand = HexUtil.decodeHex(command);
        int dataLength = decodedCommand.length;

        ByteBuffer buffer = ByteBuffer.allocate(dataLength + 3);
        buffer.put(CMD_UART);
        buffer.put(SUB_CMD);
        buffer.put((byte) dataLength);
        buffer.put(decodedCommand);

        return buffer.array();
    }

    @Override
    public String toString() {
        if (TextUtils.isEmpty(command)) {
            return "";
        }
        try {
            return HexUtil.encodeHexStr(getCommandData());
        } catch (Exception e) {
            return "";
        }
    }
}