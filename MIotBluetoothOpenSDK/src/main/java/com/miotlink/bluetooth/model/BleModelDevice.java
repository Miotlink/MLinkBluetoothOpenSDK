package com.miotlink.bluetooth.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.miotlink.bluetooth.utils.BlueTools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class BleModelDevice extends BleDevice implements Parcelable {
    private static final String TAG = "BleModelDevice";

    // 厂商特定数据 ID
    private static final int MANUFACTURER_ID = 0x6667;

    // 协议版本常量
    private static final byte PROTOCOL_VERSION_4 = 0x04;

    // IMEI 标识
    private static final byte IMEI_IDENTIFIER = 0x38;
    private static final int IMEI_LENGTH = 15;

    // MAC 地址字节索引
    private static final int MAC_ADDRESS_LENGTH = 6;
    private static final int[] MAC_BYTE_ORDER = {18, 17, 16, 15, 14, 13};

    // 华为设备标识
    private static final String HUAWEI_DEVICE_PREFIX = "Hi-Huawei-Mars";
    private static final int HUAWEI_PRODUCT_ID_OFFSET = 15;
    private static final int HUAWEI_PRODUCT_ID_LENGTH = 4;

    // 默认配网类型
    private static final int DEFAULT_CONFIG_CODE = 7;

    private int rssi;
    private long rssiUpdateTime;
    private ScanRecord scanRecord;
    private int kindId;
    private int modelId;
    private String productId = "";  // 修复拼写错误
    private String dType = "WiFi";
    private String imei = "";
    private String deviceName = "";
    private String macAddress = "";
    private int mark = -1;
    private int mVersion;
    private int mCode = 7;

    // ========== 构造函数 ==========

    public BleModelDevice(String address, String name) {
        super(address, name);
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
        this.productId = in.readString();
        this.dType = in.readString();
    }

    // ========== Setter/Getter ==========

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setScanRecord(ScanRecord scanRecord) {
        this.scanRecord = scanRecord;
        if (scanRecord == null) {
            return;
        }

        // 优先使用广播名作为设备名
        if (!TextUtils.isEmpty(getBleName())) {
            deviceName = getBleName();
        }

        try {
            byte[] manufacturerData = getManufacturerData(scanRecord);
            if (manufacturerData != null) {
                parseManufacturerData(manufacturerData);
            } else {
                handleNonManufacturerDevice();
            }

            parseHuaweiDeviceIfPresent(scanRecord);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing scan record", e);
        }
    }

    // ========== 数据解析方法 ==========

    /**
     * 获取厂商特定数据
     */
    private byte[] getManufacturerData(ScanRecord scanRecord) {
        if (scanRecord.getManufacturerSpecificData() == null) {
            return null;
        }
        return scanRecord.getManufacturerSpecificData().get(MANUFACTURER_ID);
    }

    /**
     * 解析厂商特定数据
     */
    private void parseManufacturerData(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return;
        }

        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));

        // 标记当前位置，用于后续可能的回退
        dataStream.mark(data.length);

        // 跳过前两个字节（厂商 ID）
        dataStream.readShort();

        mCode = DEFAULT_CONFIG_CODE;
        mVersion = dataStream.readByte();
        mark = dataStream.readByte();


        // 处理 V4 协议
        if (mVersion == PROTOCOL_VERSION_4) {
            if (parseV4Protocol(dataStream, data.length)) {
                return; // 成功解析 IMEI
            }
            // 回退到标记位置重新解析
            dataStream.reset();
            dataStream.readShort(); // 重新跳过厂商 ID
            mVersion = dataStream.readByte();
            mark = dataStream.readByte();
        }
        if (mVersion == 0x02) {
            dType = "BLE";
        }
        // 解析品类和型号
        parseDeviceInfo(dataStream, data);
    }

    /**
     * 解析 V4 协议数据
     *
     * @return true 如果成功解析 IMEI 并可以提前返回
     */
    private boolean parseV4Protocol(DataInputStream stream, int dataLength) throws IOException {
        byte[] buffer = new byte[IMEI_LENGTH];
        int bytesRead = stream.read(buffer);

        if (bytesRead != IMEI_LENGTH) {
            return false;
        }

        if (buffer[0] == IMEI_IDENTIFIER) {
            dType = "4G";
            imei = new String(buffer, StandardCharsets.UTF_8);
            macAddress = imei;
            return true;
        }

        return false;
    }

    /**
     * 解析设备品类和型号信息
     */
    private void parseDeviceInfo(DataInputStream stream, byte[] rawData) throws IOException {
        stream.readByte(); // 跳过标识字节

        kindId = stream.readInt();
        modelId = stream.readInt();

        macAddress = extractMacAddress(rawData);
    }

    /**
     * 从原始字节数组中提取 MAC 地址
     */
    private String extractMacAddress(byte[] rawData) {
        if (rawData.length < MAC_BYTE_ORDER[0] + 1) {
            return "";
        }

        StringBuilder macBuilder = new StringBuilder();
        boolean isFirst = true;

        for (int index : MAC_BYTE_ORDER) {
            if (!isFirst) {
                macBuilder.append(":");
            }
            macBuilder.append(BlueTools.byteToHex(rawData[index]));
            isFirst = false;
        }

        return macBuilder.toString().toUpperCase();
    }

    /**
     * 处理非厂商特定数据的情况
     */
    private void handleNonManufacturerDevice() {
        mCode = DEFAULT_CONFIG_CODE;
        macAddress = getBleAddress();
    }

    /**
     * 解析华为设备数据（如果存在）
     */
    private void parseHuaweiDeviceIfPresent(ScanRecord scanRecord) {
        String bleName = getBleName();
        if (TextUtils.isEmpty(bleName) || !bleName.startsWith(HUAWEI_DEVICE_PREFIX)) {
            return;
        }

        byte[] rawBytes = scanRecord.getBytes();
        if (rawBytes == null || rawBytes.length < HUAWEI_PRODUCT_ID_OFFSET + HUAWEI_PRODUCT_ID_LENGTH) {
            return;
        }

        try {
            byte[] productIdBytes = Arrays.copyOfRange(
                    rawBytes,
                    HUAWEI_PRODUCT_ID_OFFSET,
                    HUAWEI_PRODUCT_ID_OFFSET + HUAWEI_PRODUCT_ID_LENGTH
            );
            productId = new String(productIdBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.w(TAG, "Failed to parse Huawei product ID", e);
        }
    }

    // ========== Getter/Setter 方法 ==========

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getRssiUpdateTime() {
        return rssiUpdateTime;
    }


    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    public void setRssiUpdateTime(long rssiUpdateTime) {
        this.rssiUpdateTime = rssiUpdateTime;
    }

    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    public int getKindId() {
        return kindId;
    }

    public void setKindId(int kindId) {
        this.kindId = kindId;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getDeviceName() {
        return deviceName;
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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getmCode() {
        return mCode;
    }

    public void setmCode(int mCode) {
        this.mCode = mCode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(rssi);
        dest.writeLong(rssiUpdateTime);
        dest.writeParcelable(scanRecord, flags);
        dest.writeInt(kindId);
        dest.writeInt(modelId);
        dest.writeString(deviceName);
        dest.writeString(macAddress);
        dest.writeInt(mark);
        dest.writeInt(mVersion);
        dest.writeInt(mCode);
        dest.writeString(productId);
        dest.writeString(dType);
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