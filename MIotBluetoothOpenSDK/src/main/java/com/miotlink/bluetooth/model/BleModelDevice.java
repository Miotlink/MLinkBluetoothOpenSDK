package com.miotlink.bluetooth.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import androidx.annotation.RequiresApi;
import com.miotlink.bluetooth.utils.BlueTools;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class BleModelDevice extends BleDevice implements Parcelable {
    /**
     * 蓝牙信号
     */
    private int rssi;
    /**
     * 更新时间
     */
    private long rssiUpdateTime;
    /**
     * 蓝牙广播包数据
     */
    private ScanRecord scanRecord;
    /**
     * 设备品类
     */
    private int kindId = 0;
    /**
     * 设备型号
     */
    private int modelId = 0;

    private String prdouctId = "";

    /**
     * 设备名称
     */
    private String deviceName = "";

    /**
     * 设备MAC地址
     */
    private String macAddress = "";

    /**
     * 标志位
     */
    private int mark = -1;

    /**
     * 设备版本号
     */
    private int mVersion = 0;

    /**
     * 配网类型 7 妙联app 使用判断区分
     */
    private int mCode = 0;

    public int getKindId() {
        return kindId;
    }

    public void setKindId(int kindId) {
        this.kindId = kindId;
    }

    public int getModelId() {
        return modelId;
    }

    public String getDeviceName() {
        return deviceName;
    }


    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getmVersion() {
        return mVersion;
    }

    public void setmVersion(int mVersion) {
        this.mVersion = mVersion;
    }

    public int getmCode() {
        return mCode;
    }

    public void setmCode(int mCode) {
        this.mCode = mCode;
    }

    public String getPrdouctId() {
        return prdouctId;
    }

    public void setPrdouctId(String prdouctId) {
        this.prdouctId = prdouctId;
    }

    public BleModelDevice(String address, String name) {
        super(address, name);
    }


    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setScanRecord(ScanRecord scanRecord) {
        this.scanRecord = scanRecord;
        try {
            if (!TextUtils.isEmpty(getBleName())) {
                deviceName = getBleName();
            }
            if (scanRecord != null && scanRecord.getManufacturerSpecificData() != null) {
                byte[] bytes = scanRecord.getManufacturerSpecificData().get(0x6667);
                if (bytes != null) {
                    DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
                    dataInputStream.readShort();
                    mCode = 7;
                    mVersion = dataInputStream.readUnsignedByte();
                    mark = dataInputStream.readUnsignedByte();
                    dataInputStream.readUnsignedByte();
                    kindId = dataInputStream.readInt();
                    modelId = dataInputStream.readInt();
                    macAddress = "";
                    macAddress += BlueTools.byteToHex(bytes[18]) + ":";
                    macAddress += BlueTools.byteToHex(bytes[17]) + ":";
                    macAddress += BlueTools.byteToHex(bytes[16]) + ":";
                    macAddress += BlueTools.byteToHex(bytes[15]) + ":";
                    macAddress += BlueTools.byteToHex(bytes[14]) + ":";
                    macAddress += (BlueTools.byteToHex(bytes[13]) + "");
                    macAddress = macAddress.toUpperCase();
                    return;
                }
                mCode = 7;
                macAddress = getBleAddress();
            }
            if (!TextUtils.isEmpty(getBleName()) && getBleName().startsWith("Hi-Huawei-Mars")) {
                byte[] bytes = scanRecord.getBytes();
                if (bytes != null && bytes.length >= 18) {
                    byte[] b = new byte[4];
                    b[0] = bytes[15];
                    b[1] = bytes[16];
                    b[2] = bytes[17];
                    b[3] = bytes[18];
                    prdouctId = new String(b, "UTF-8");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getRssiUpdateTime() {
        return rssiUpdateTime;
    }

    public void setRssiUpdateTime(long rssiUpdateTime) {
        this.rssiUpdateTime = rssiUpdateTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.rssi);
        dest.writeLong(this.rssiUpdateTime);
        dest.writeParcelable(this.scanRecord, flags);
        dest.writeInt(this.kindId);
        dest.writeInt(this.modelId);
        dest.writeString(this.deviceName);
        dest.writeString(this.macAddress);
        dest.writeString(this.prdouctId);
        dest.writeInt(this.mark);
        dest.writeInt(this.mVersion);
        dest.writeInt(this.mCode);
    }

    protected BleModelDevice(Parcel in) {
        super(in);
        this.rssi = in.readInt();
        this.rssiUpdateTime = in.readLong();
        this.scanRecord = in.readParcelable(ScanRecord.class.getClassLoader());
        this.kindId = in.readInt();
        this.modelId = in.readInt();
        this.deviceName = in.readString();
        this.macAddress = in.readString();
        this.mark = in.readInt();
        this.mVersion = in.readInt();
        this.mCode = in.readInt();
        this.prdouctId = in.readString();
    }

    public static final Creator<BleModelDevice> CREATOR = new Creator<BleModelDevice>() {
        @Override
        public BleModelDevice createFromParcel(Parcel source) {
            return new BleModelDevice(source);
        }

        @Override
        public BleModelDevice[] newArray(int size) {
            return new BleModelDevice[size];
        }
    };
}
