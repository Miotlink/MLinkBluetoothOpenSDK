package com.miotlink.bluetooth.command;

import com.miotlink.bluetooth.service.BleLog;
import com.miotlink.bluetooth.utils.HexUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息协议解析器
 * USER：create by qiaozhuang on 2024/11/15 15:28
 * EMAIL:qiaozhuang@miotlink.com
 */
public class IMessageProtocol implements IReaderProtocol {

    private static final int HEADER_VALUE = 0x6667;
    private static final int MIN_PACKET_LENGTH = 9;
    private static final int HEADER_OVERHEAD = 5;

    @Override
    public CommmandBean getCommand(byte[] bytes) throws Exception {
        validateInput(bytes);

        CommmandBean commandBean = new CommmandBean();
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(bytes));

        int header = dataStream.readShort();
        if (header != HEADER_VALUE) {
            throw new IllegalArgumentException(
                    String.format("Invalid header: 0x%04X, expected: 0x%04X", header, HEADER_VALUE));
        }

        commandBean.setHead(header);

        int bodyLength = dataStream.readUnsignedByte();
        commandBean.setBodyLen(bodyLength);

        validateBodyLength(bodyLength, bytes.length);

        commandBean.setTimestamp(dataStream.readUnsignedShort());
        commandBean.setCode(dataStream.readUnsignedByte());

        int dataCount = dataStream.readUnsignedByte();
        commandBean.setDataNum(dataCount);

        byte[] bodyData = parseBody(dataStream, bodyLength);
        commandBean.setBytes(bodyData);

        Map<Integer, byte[]> valueMap = parseDataValues(bodyData, dataCount);
        commandBean.setValues(valueMap);

        return commandBean;
    }

    private void validateInput(byte[] bytes) {
        if (bytes == null || bytes.length < MIN_PACKET_LENGTH) {
            throw new IllegalArgumentException(
                    "Packet too short. Required: " + MIN_PACKET_LENGTH +
                            ", actual: " + (bytes != null ? bytes.length : 0));
        }
    }

    private void validateBodyLength(int bodyLength, int packetLength) {
        if (bodyLength < 4 || bodyLength + HEADER_OVERHEAD > packetLength) {
            throw new IllegalArgumentException(
                    String.format("Invalid body length: %d, packet length: %d",
                            bodyLength, packetLength));
        }
    }

    private byte[] parseBody(DataInputStream stream, int bodyLength) throws IOException {
        int actualBodyLen = bodyLength - 6;
        byte[] body = new byte[actualBodyLen];
        stream.readFully(body);
        return body;
    }

    private Map<Integer, byte[]> parseDataValues(byte[] bodyData, int dataCount) {
        Map<Integer, byte[]> valueMap = new HashMap<>();
        int position = 0;

        for (int i = 0; i < dataCount; i++) {
            if (position >= bodyData.length) {
                throw new IllegalStateException(
                        String.format("Data overflow at index %d, position: %d, length: %d",
                                i, position, bodyData.length));
            }
            int valueLength = bodyData[position] & 0xFF;
            int valueStart = position + 1;
            int valueEnd = valueStart + valueLength;
            if (valueEnd > bodyData.length) {
                throw new IllegalStateException(
                        String.format("Value overflow at index %d, valueEnd: %d, bodyLength: %d",
                                i, valueEnd, bodyData.length));
            }

            byte[] value = new byte[valueLength];
            System.arraycopy(bodyData, valueStart, value, 0, valueLength);
            valueMap.put(i, value);
            BleLog.d("parseDataValues", HexUtil.encodeHexStr(value));
            position = valueEnd;
        }

        return valueMap;
    }
}