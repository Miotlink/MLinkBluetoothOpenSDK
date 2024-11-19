package com.miotlink;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;


import com.miotlink.bluetooth.impl.BleRequestServiceImpl;
import com.miotlink.bluetooth.impl.BleSmartService;
import com.miotlink.bluetooth.listener.BleDeviceScanListener;
import com.miotlink.bluetooth.listener.BleSmartConfigListener;
import com.miotlink.bluetooth.listener.BleSmartListener;
import com.miotlink.bluetooth.listener.SmartNotifyDeviceConnectListener;
import com.miotlink.bluetooth.listener.SmartNotifyOTAListener;
import com.miotlink.bluetooth.listener.SmartNotifyUartDataListener;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.service.Ble;
import com.miotlink.bluetooth.service.BleLog;

import org.json.JSONObject;

import java.util.List;

public class MLinkSmartBluetoothSDK {

    public static final String TAG = MLinkSmartBluetoothSDK.class.getName();


    private static volatile MLinkSmartBluetoothSDK instance = null;


    public static synchronized MLinkSmartBluetoothSDK getInstance() {

        if (instance == null) {
            synchronized (MLinkSmartBluetoothSDK.class) {
                if (instance == null) {
                    instance = new MLinkSmartBluetoothSDK();
                }
            }
        }
        return instance;
    }

    BleSmartService bleSmartService = new BleRequestServiceImpl();

    /**
     * 打印数据
     *
     * @param isDebug
     */
    public void setDebug(boolean isDebug) {
        try {
            bleSmartService.setDebug(isDebug);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置UUID  参数
     *
     * @param serverUuid
     * @param writeUuid
     * @param readUuid
     * @param notifyUuid
     */
    public void setServerUUID(String serverUuid, String writeUuid, String readUuid, String notifyUuid, String otaUuid) {

        try {
            if (TextUtils.isEmpty(serverUuid)
                    || TextUtils.isEmpty(writeUuid)
                    || TextUtils.isEmpty(readUuid)
                    || TextUtils.isEmpty(notifyUuid)) {
                BleLog.e(TAG, "UUID is Empty");
                return;
            }
            bleSmartService.setServiceUUID(serverUuid, writeUuid, readUuid, otaUuid);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 初始化参数
     *
     * @param mContext
     * @param smartListener
     * @throws Exception
     */
    public void init(Context mContext, String appKey, BleSmartListener smartListener) {
        try {
            bleSmartService.init(mContext, smartListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<BleModelDevice> getConnectBleDevices(){

    }


    /**
     * 扫描蓝牙设备
     *
     * @param scanCallBack
     */
    public void startScan(final BleDeviceScanListener scanCallBack) {
        try {
            bleSmartService.onScan(scanCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startScan(int scanPeriod, BleDeviceScanListener scanCallBack) {
        try {
            bleSmartService.onScan(scanPeriod, scanCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param macCode     mac地址
     * @param ssid        路由器账户
     * @param passowrd    路由器密码
     * @param delayMillis 设置超时时间 默认60s
     */
    public void startSmartConfig(String macCode, String ssid, String passowrd, int delayMillis, BleSmartConfigListener bleSmartConfigListener) {

        try {
            bleSmartService.onStartSmartConfig(macCode, ssid, passowrd, delayMillis, bleSmartConfigListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param macCode
     * @param ssid
     * @param passowrd
     * @param delayMillis
     */
    public void onAwsSmartConfig(String macCode, String ssid, String passowrd, int delayMillis, BleSmartConfigListener bleSmartConfigListener) {
        try {
            JSONObject awsNetWorkInfo = new JSONObject();
            awsNetWorkInfo.put("Ssid", ssid);
            awsNetWorkInfo.put("Pwd", passowrd);
            awsNetWorkInfo.put("PF", 0);
            awsNetWorkInfo.put("Host", "");
            awsNetWorkInfo.put("Port", 0);
            awsNetWorkInfo.put("PId", "");
            awsNetWorkInfo.put("DId", "");
            awsNetWorkInfo.put("BindCode", "");
            awsNetWorkInfo.put("Token", "");
            awsNetWorkInfo.put("ActUrl", "");
            awsNetWorkInfo.put("CertUrl", "");
            awsNetWorkInfo.put("BindUrl", "");
            awsNetWorkInfo.put("OtaUrl", "");
            awsNetWorkInfo.put("Res", "");
            JSONObject awsNetWorks = new JSONObject();
            if (awsNetWorkInfo != null) {
                awsNetWorks.put("type", "Wifi");
                awsNetWorks.put("data", awsNetWorkInfo);
            }
            try {
                bleSmartService.onAwsStartSmartConfig(macCode, awsNetWorks.toString(), delayMillis, bleSmartConfigListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onStopSmartConfig(String macAddress) {
        bleSmartService.onStopSmartConfig(macAddress);
    }

    public boolean checkPermissions() {
        return bleSmartService.checkPermission();
    }

    public void openPermissions(Activity activity, int requestCode) {
        bleSmartService.requestPermissions(activity, requestCode);
    }

    /**
     * 强制打开蓝牙开关
     */
    public void turnOnBlueToothNo() {
        bleSmartService.openBluetooth();
    }

    public BleModelDevice getScanDevice(String macCode) {
        return bleSmartService.getBleModelDevice(macCode);
    }

    public void getVersion(String macCode) {
        try {
            bleSmartService.getDeviceVersion(macCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void connect(String macCode, final SmartNotifyDeviceConnectListener smartNotifyDeviceConnectListener) {
        try {
            bleSmartService.connect(macCode, smartNotifyDeviceConnectListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startBluetooth(Activity activity, int requestCode) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(Ble.getInstance().getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivityForResult(enableBtIntent, requestCode);
    }


    @Deprecated
    public void send(String macCode, String uartData, SmartNotifyUartDataListener smartNotifyListener) {
        try {
            bleSmartService.setSmartNotifyUartDataListener(smartNotifyListener);
            bleSmartService.send(macCode, uartData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(String macCode, byte[] uartData, SmartNotifyUartDataListener smartNotifyListener) {
        try {
            bleSmartService.setSmartNotifyUartDataListener(smartNotifyListener);
            bleSmartService.send(macCode, uartData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取数据通知
     *
     * @param notifyUartDataListener
     */
    public void setSmartNotifyUartDataListener(SmartNotifyUartDataListener notifyUartDataListener) {
        try {
            bleSmartService.setSmartNotifyUartDataListener(notifyUartDataListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindPu(String macCode) {
        try {
            bleSmartService.bindPu(macCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unBindPu(String macCode) {
        try {
            bleSmartService.unBindPu(macCode, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 升级OTA通知
     *
     * @param smartNotifyOTAListener
     */
    public void setSmartNotifyOTAListener(SmartNotifyOTAListener smartNotifyOTAListener) {

    }

    /**
     * 停止扫描
     */
    public void onStopScan() {
        try {
            bleSmartService.onScanStop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void disConnect(String macCode) {
        try {
            bleSmartService.onDisConnect(macCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestory() {
        try {
            bleSmartService.onDestory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
