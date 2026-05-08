package com.miotlink.bluetooth.command;

import android.text.TextUtils;
import com.miotlink.bluetooth.service.BleLog;
import com.miotlink.bluetooth.utils.*;
import org.json.JSONObject;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息抽象基类 - 提供通用打包逻辑
 * USER：create by qiaozhuang on 2024/11/14 15:08
 * EMAIL:qiaozhuang@miotlink.com
 */
public abstract class AbsMessage implements IMessage {

    // 协议常量
    protected static final byte HEADER_HIGH = (byte) 0x66;
    protected static final byte HEADER_LOW = (byte) 0x67;
    protected static final byte TERMINATOR_HIGH = (byte) 0x0D;
    protected static final byte TERMINATOR_LOW = (byte) 0x0A;

    // 分包类型常量
    protected static final byte PACKET_TYPE_FIRST = 0x01;
    protected static final byte PACKET_TYPE_MIDDLE = 0x10;
    protected static final byte PACKET_TYPE_LAST = 0x01;

    // 命令码
    protected static final byte CMD_AWS_CONFIG = 0x12;

    // 基础封装长度：header(2) + length(1) + timestamp(2) + crc(2) + terminator(2) = 9
    protected static final int BASE_OVERHEAD = 9;

    /**
     * 子类实现此方法，返回命令特定的字节数据（不含协议封装）
     */
    protected abstract byte[] getCommandData() throws Exception;

    /**
     * 获取协议封装后的完整命令数据
     */
    protected byte[] getEncapsulatedData() throws Exception {
        byte[] commandData = getCommandData();
        return encapsulatePacket(commandData,
                (byte) (4 + commandData.length));
    }

    /**
     * 标准协议封装
     */
    protected byte[] encapsulatePacket(byte[] data, byte bodyLength) {
        int timestamp = (int) (System.currentTimeMillis() % 65536);
        String crcValue = CRC16Utils.getCRCValue(data);

        ByteBuffer buffer = ByteBuffer.allocate(data.length + BASE_OVERHEAD);
        buffer.put(HEADER_HIGH);
        buffer.put(HEADER_LOW);
        buffer.put(bodyLength);
        buffer.put(BlueTools.int16ToBytes(timestamp));
        buffer.put(data);
        buffer.put((byte) CRC16Utils.getCrcMinLen(crcValue));
        buffer.put((byte) CRC16Utils.getCrcMaxLen(crcValue));
        buffer.put(TERMINATOR_HIGH);
        buffer.put(TERMINATOR_LOW);

        byte[] result = buffer.array();
        BleLog.d("Hex", HexUtil.encodeHexStr(result));
        return result;
    }

    @Override
    public byte[] pack() throws Exception {
        return encapsulatePacket(getCommandData(),
                (byte) (4 + getCommandData().length));
    }

    @Override
    public List<byte[]> packs() throws Exception {
        String jsonStr = toString();
        if (TextUtils.isEmpty(jsonStr)) {
            throw new IllegalArgumentException("Command data is null");
        }

        JSONObject jsonObject = new JSONObject(jsonStr);
        String code = jsonObject.optString("Code");

        if (!"AwsSmartConfig".equals(code)) {
            return new ArrayList<>();
        }

        return handleAwsSmartConfigPackets(jsonObject);
    }

    /**
     * 处理 AWS SmartConfig 分包逻辑
     */
    private List<byte[]> handleAwsSmartConfigPackets(JSONObject jsonObject) throws Exception {
        JSONObject data = jsonObject.getJSONObject("Data");
        if (data == null) {
            throw new IllegalArgumentException("Data field is missing");
        }

        int mtu = data.getInt("mtu");
        String mac = data.optString("mac", data.optString("macCode", ""));
        String command = data.getString("command");

        int timestamp = (int) System.currentTimeMillis() % 65536;
        String timestampHex = HexUtil.encodeHexStr(BlueTools.Int16ToBytes(timestamp));

        String encryptionKey = mac.replaceAll(":", "").toUpperCase() + timestampHex.toUpperCase();
        byte[] encrypted = AesUtils.encrypt(encryptionKey, command);

        List<byte[]> packets = PacketUtils.getPackets(mtu - 12, encrypted);
        if (packets == null || packets.isEmpty()) {
            return new ArrayList<>();
        }

        List<byte[]> result = new ArrayList<>();
        int packetCount = packets.size();

        for (int i = 0; i < packetCount; i++) {
            byte[] packetData = packets.get(i);
            byte type = determinePacketType(i, packetCount);

            ByteBuffer buffer = ByteBuffer.allocate(packetData.length + BASE_OVERHEAD);
            buffer.put(HEADER_HIGH);
            buffer.put(HEADER_LOW);
            buffer.put((byte) (7 + packetData.length));
            buffer.put(BlueTools.int16ToBytes(timestamp));
            buffer.put(CMD_AWS_CONFIG);
            buffer.put(type);
            buffer.put((byte) packetData.length);
            buffer.put(packetData);

            String crcValue = CRC16Utils.getCRCValue(packetData);
            buffer.put((byte) CRC16Utils.getCrcMinLen(crcValue));
            buffer.put((byte) CRC16Utils.getCrcMaxLen(crcValue));
            buffer.put(TERMINATOR_HIGH);
            buffer.put(TERMINATOR_LOW);

            result.add(buffer.array());
        }

        return result;
    }

    private byte determinePacketType(int index, int total) {
        if (total == 1) return PACKET_TYPE_FIRST;
        if (index == 0 || index == total - 1) return PACKET_TYPE_FIRST;
        return PACKET_TYPE_MIDDLE;
    }
}