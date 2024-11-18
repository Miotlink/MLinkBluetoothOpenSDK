package com.miotlink.bluetooth.command;


import android.text.TextUtils;

import com.miotlink.bluetooth.service.BleLog;
import com.miotlink.bluetooth.utils.AesEncryptUtil;
import com.miotlink.bluetooth.utils.BlueTools;
import com.miotlink.bluetooth.utils.CRC16Utils;
import com.miotlink.bluetooth.utils.HexUtil;
import com.miotlink.bluetooth.utils.PacketUtils;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * USER：create by qiaozhuang on 2024/11/14 15:08
 * EMAIL:qiaozhuang@miotlink.com
 */
public class AbsMessage implements IMessage {
    @Override
    public byte[] pack() throws Exception {
        byte[] bytes = HexUtil.decodeHex(toString());
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 9);
        buffer.put((byte) 0x66);
        buffer.put((byte) 0x67);
        buffer.put((byte) (4 + bytes.length));
        buffer.put(BlueTools.int16ToBytes((int) (System.currentTimeMillis() % 65536)));
        buffer.put(bytes);
        String crcValue = CRC16Utils.getCRCValue(bytes);
        buffer.put((byte) CRC16Utils.getCrcMinLen(crcValue));
        buffer.put((byte) CRC16Utils.getCrcMaxLen(crcValue));
        buffer.put((byte) 0x0D);
        buffer.put((byte) 0x0A);
        BleLog.d("Hex", HexUtil.encodeHexStr(buffer.array()));
        return buffer.array();
    }

    @Override
    public List<byte[]> packs() throws Exception {
        String string = toString();
        if (TextUtils.isEmpty(string)) {
            throw new Exception("params null");
        }
        JSONObject jsonObject = new JSONObject(string);
        if (jsonObject == null) {
            throw new Exception("json is error");
        }
        String Code = jsonObject.getString("Code");
        if (TextUtils.isEmpty(Code)) {
            throw new Exception("Code is error");
        }
        if (Code.equals("AwsSmartConfig")) {
            String key = "";
            JSONObject jsonObject1 = jsonObject.getJSONObject("Data");
            if (jsonObject1 == null) {
                throw new Exception("Data is error");
            }
            int mtu = jsonObject1.getInt("mtu");
            String mac = jsonObject1.getString("mac");
            String command = jsonObject1.getString("command");
            List<byte[]> list = new ArrayList<>();
            int timeId = (int) System.currentTimeMillis() % 65536;
            byte[] buff = BlueTools.Int16ToBytes(timeId);
            String timeIdHex = HexUtil.encodeHexStr(buff);
            if (!TextUtils.isEmpty(mac)) {
                key = mac.replaceAll(":", "").toUpperCase();
            }
            key += timeIdHex.toUpperCase();
            byte[] encrypt = AesEncryptUtil.encrypt(key, command);
            List<byte[]> networkInfos = PacketUtils.getPackets(mtu - 12, encrypt);
            if (networkInfos != null) {
                if (networkInfos.size() == 1) {
                    byte[] bytes = networkInfos.get(0);
                    ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 9);
                    buffer.put((byte) 0x66);
                    buffer.put((byte) 0x67);
                    buffer.put((byte) (7 + bytes.length));
                    buffer.put(BlueTools.int16ToBytes(timeId));
                    buffer.put((byte) 0x12);
                    buffer.put((byte) 0x00);
                    buffer.put((byte) bytes.length);
                    buffer.put(bytes);
                    String crcValue = CRC16Utils.getCRCValue(bytes);
                    buffer.put((byte) CRC16Utils.getCrcMinLen(crcValue));
                    buffer.put((byte) CRC16Utils.getCrcMaxLen(crcValue));
                    buffer.put((byte) 0x0D);
                    buffer.put((byte) 0x0A);
                    list.add(buffer.array());
                } else if (networkInfos.size() > 2) {
                    for (int i = 0; i < networkInfos.size(); i++) {
                        byte[] bytes = networkInfos.get(i);
                        byte type = 0x00;
                        if (i == 0) {
                            type = 0x01;
                        } else if (i >= networkInfos.size() - 1) {
                            type = 0x01;

                        } else {
                            type = 0x10;
                        }
                        ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 9);
                        buffer.put((byte) 0x66);
                        buffer.put((byte) 0x67);
                        buffer.put((byte) (7 + bytes.length));
                        buffer.put(BlueTools.int16ToBytes(timeId));
                        buffer.put((byte) 0x12);
                        buffer.put(type);
                        buffer.put((byte) bytes.length);
                        buffer.put(bytes);
                        String crcValue = CRC16Utils.getCRCValue(bytes);
                        buffer.put((byte) CRC16Utils.getCrcMinLen(crcValue));
                        buffer.put((byte) CRC16Utils.getCrcMaxLen(crcValue));
                        buffer.put((byte) 0x0D);
                        buffer.put((byte) 0x0A);
                        list.add(buffer.array());
                    }
                    return list;
                }
            }
        }
        return new ArrayList<>();
    }
}
