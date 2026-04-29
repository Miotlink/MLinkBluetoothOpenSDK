package com.miotlink.bluetooth.command;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * USER：create by qiaozhuang on 2024/11/15 15:28
 * EMAIL:qiaozhuang@miotlink.com
 */
public class IMessageProtocol implements IReaderProtocol {
    @Override
    public CommmandBean getCommand(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length < 9) {
            throw new Exception("value is empty");
        }
        CommmandBean commmandBean = new CommmandBean();
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        int headValue = dataInputStream.readShort();
        if (headValue == 0x6667) {
            commmandBean.setHead(headValue);
            int length = dataInputStream.readUnsignedByte();
            commmandBean.setBodyLen(length);
            if (length < 4 || length + 5 > bytes.length) {
                throw new Exception(" 数据异常");
            }
            commmandBean.setTimestamp(dataInputStream.readShort());
            commmandBean.setCode(dataInputStream.readUnsignedByte());
            int dataNum = dataInputStream.readUnsignedByte();
            commmandBean.setDataNum((byte) dataNum);
            int bodyLen = length - 4;
            byte[] bodys = new byte[bodyLen];
            dataInputStream.readFully(bodys);
            commmandBean.setBytes(bodys);
            Map<Integer, byte[]> mapValue = new HashMap<>();
            int len = 0;
            for (int i = 0; i < dataNum; i++) {
                if (len >= bodys.length) {
                    throw new Exception(" 数据异常");
                }
                int valueLen = bodys[len] & 0xFF;
                int valueStart = len + 1;
                int valueEnd = valueStart + valueLen;
                if (valueEnd > bodys.length) {
                    throw new Exception(" 数据异常");
                }
                byte[] value = new byte[valueLen];
                System.arraycopy(bodys, valueStart, value, 0, valueLen);
                mapValue.put(i, value);
                len = valueEnd;
            }
            commmandBean.setValues(mapValue);
        }
        return commmandBean;
    }
}
