package com.miotlink.bluetooth.command;

import com.miotlink.bluetooth.utils.HexUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * USER：create by qiaozhuang on 2024/11/15 15:25
 * EMAIL:qiaozhuang@miotlink.com
 */
public class CommmandBean {

    public int head=0;

    public int bodyLen=0;

    public int timestamp=0;

    private int Code=0;

    private int dataNum=0;

    private byte[] bytes=null;

    public Map<Integer,byte[]> values=new HashMap<>();

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getDataNum() {
        return dataNum;
    }

    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getBodyLen() {
        return bodyLen;
    }

    public void setBodyLen(int bodyLen) {
        this.bodyLen = bodyLen;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public Map<Integer, byte[]> getValues() {
        return values;
    }

    public void setValues(Map<Integer, byte[]> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "CommmandBean{" +
                "head=" + head +
                ", bodyLen=" + bodyLen +
                ", timestamp=" + timestamp +
                ", Code=" + Code +
                ", dataNum=" + dataNum +
                ", bytes=" + Arrays.toString(bytes) +
                ", values=" + values +
                '}';
    }
}
