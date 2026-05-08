package com.miotlink.bluetooth.exception;

/**
 * USER：create by qiaozhuang on 2026/5/7 09:24
 * EMAIL:qiaozhuang@miotlink.com
 */
// BleErrorCode.java

public interface BleErrorCode {
    int PERMISSION_DENIED = 1001;
    int BLUETOOTH_DISABLED = 1002;
    int INVALID_PARAM = 1003;
    int INVALID_UUID = 1004;
    int INIT_FAILED = 1005;
    int NOT_IMPLEMENTED = 1006;
    int CONNECTION_FAILED = 1007;
    int SEND_FAILED = 1008;
}
