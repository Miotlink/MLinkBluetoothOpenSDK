package com.miotlink.bluetooth.command;

import com.miotlink.bluetooth.utils.HexUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令数据实体
 * USER：create by qiaozhuang on 2024/11/15 15:25
 * EMAIL:qiaozhuang@miotlink.com
 */
public class CommmandBean {

    private int head;
    private int bodyLen;
    private int timestamp;
    private int code;           // 统一命名规范
    private int dataNum;
    private byte[] bytes;
    private Map<Integer, byte[]> values = new HashMap<>();

    // Getters and Setters
    public int getHead() { return head; }
    public void setHead(int head) { this.head = head; }

    public int getBodyLen() { return bodyLen; }
    public void setBodyLen(int bodyLen) { this.bodyLen = bodyLen; }

    public int getTimestamp() { return timestamp; }
    public void setTimestamp(int timestamp) { this.timestamp = timestamp; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public int getDataNum() { return dataNum; }
    public void setDataNum(int dataNum) { this.dataNum = dataNum; }

    public byte[] getBytes() { return bytes != null ? bytes.clone() : null; }
    public void setBytes(byte[] bytes) { this.bytes = bytes != null ? bytes.clone() : null; }

    public Map<Integer, byte[]> getValues() { return new HashMap<>(values); }
    public void setValues(Map<Integer, byte[]> values) {
        this.values = values != null ? new HashMap<>(values) : new HashMap<>();
    }

    @Override
    public String toString() {
        return "CommmandBean{" +
                "head=" + head +
                ", bodyLen=" + bodyLen +
                ", timestamp=" + timestamp +
                ", code=" + code +
                ", dataNum=" + dataNum +
                ", bytes=" + HexUtil.encodeHexStr(bytes) +
                ", values=" + values.toString() +
                '}';
    }
}