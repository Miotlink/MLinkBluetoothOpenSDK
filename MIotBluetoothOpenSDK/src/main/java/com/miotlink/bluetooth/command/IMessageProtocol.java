package com.miotlink.bluetooth.command;

import com.miotlink.bluetooth.service.BleLog;

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
        if (bytes == null) {
            throw new Exception("value is empty");
        }
        CommmandBean commmandBean = new CommmandBean();
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        int headValue = dataInputStream.readShort();
        if (headValue == 0x6667) {
            commmandBean.setHead(headValue);
            int length = dataInputStream.readByte();
            commmandBean.setBodyLen(length);
            if (length > bytes.length) {
                throw new Exception(" 数据异常");
            }
            commmandBean.setTimestamp(dataInputStream.readShort());
            commmandBean.setCode(dataInputStream.readByte());
            byte dataNum = dataInputStream.readByte();
            commmandBean.setDataNum(dataNum);
            byte[] bodys = new byte[length - 6];
            dataInputStream.read(bodys);
            commmandBean.setBytes(bodys);
            Map<Integer, byte[]> mapValue = new HashMap<>();
            int len = 0;
            for (int i = 0; i < dataNum; i++) {
                len = len + i;
                byte[] value = new byte[bodys[len]];
                System.arraycopy(bodys, len+1, value, 0, bodys[len]);
                mapValue.put(i, value);
                len = len + bodys[len];
            }
            commmandBean.setValues(mapValue);
            BleLog.e("command", commmandBean.toString());
        }
        return commmandBean;
    }
}
