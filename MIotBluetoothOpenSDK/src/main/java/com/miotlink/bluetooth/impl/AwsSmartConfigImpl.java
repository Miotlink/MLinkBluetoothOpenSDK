package com.miotlink.bluetooth.impl;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;
import android.util.Log;


import com.miotlink.bluetooth.callback.BleConnectCallback;
import com.miotlink.bluetooth.callback.BleMtuCallback;
import com.miotlink.bluetooth.callback.BleNotifyCallback;
import com.miotlink.bluetooth.callback.BleWriteCallback;
import com.miotlink.bluetooth.command.AwsSmartConfigCommand;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.service.Ble;


import java.util.List;


public abstract class AwsSmartConfigImpl extends BleConnectCallback<BleModelDevice> {
    private MyThread myThread = null;
    private String macCode;
    private String awsNetworkInfoStr;
    Ble<BleModelDevice> ble = null;

    public AwsSmartConfigImpl(Ble<BleModelDevice> ble, String macCode, String awsNetworkInfoStr) {
        this.macCode = macCode;
        this.awsNetworkInfoStr = awsNetworkInfoStr;
        this.ble = ble;
    }

    @Override
    public void onConnectionChanged(BleModelDevice device) {
        if (device.isConnected()) {

        }
    }

    @Override
    public void onServicesDiscovered(BleModelDevice device, BluetoothGatt gatt) {
        super.onServicesDiscovered(device, gatt);
        if (device.isConnected()) {
            try {
                if (myThread != null) {
                    myThread.interrupt();
                    myThread = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            myThread = new MyThread(device);
            myThread.start();
        }
    }

    @Override
    public void onReady(BleModelDevice device) {
        ble.enableNotify(device, true, bleNotifyCallback);
        super.onReady(device);

    }

    BleNotifyCallback<BleModelDevice> bleNotifyCallback = new BleNotifyCallback<BleModelDevice>() {
        @Override
        public void onChanged(BleModelDevice device, BluetoothGattCharacteristic characteristic) {
//            UUID uuid = characteristic.getUuid();
//            if (!TextUtils.isEmpty(uuid.toString()) && TextUtils.equals(Ble.options().getUuidReadCha().toString(), uuid.toString())) {
//                byte[] value = characteristic.getValue();
//                Log.e("hex", HexUtil.encodeHexStr(value));
//                if (value == null) {
//                    return;
//                }
//                BluetoothProtocol bluetoothProtocol = new BluetoothProtocolImpl();
//                Map<String, Object> decode = bluetoothProtocol.decode(value);
//                if (decode != null) {
//                    if (decode != null && decode.containsKey("code") && decode.containsKey("value")) {
//                        int code = (int) decode.get("code");
//                        if (code == 0x13) {
//                            if (decode.containsKey("byte")) {
//                                byte[] bytesValue = (byte[]) decode.get("byte");
//                                if (bytesValue != null) {
//                                    try {
//                                        String message = new String(bytesValue, "UTF-8");
//                                        if (!TextUtils.isEmpty(message)) {
//                                            Log.e("Bluetooth", message);
//                                            JSONObject jsonObject = new JSONObject(message);
//                                            if (jsonObject != null) {
//                                                if (!jsonObject.isNull("type") && TextUtils.equals("WifiReply", jsonObject.getString("type"))) {
//                                                    if (!jsonObject.isNull("data")) {
//                                                        JSONObject jsonData = new JSONObject(jsonObject.getString("data"));
//                                                        if (jsonData != null) {
//                                                            int errorCode = jsonData.getInt("Code");
//                                                            String messageDescribe = jsonData.getString("Describe");
//                                                            onReceiver(device, macCode, errorCode, messageDescribe, message);
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }

        }

        @Override
        public void onNotifySuccess(final BleModelDevice device) {
            super.onNotifySuccess(device);


        }
    };

    class MyThread extends Thread {
        private BleModelDevice device;

        public MyThread(BleModelDevice modelDevice) {
            this.device = modelDevice;
        }

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(1000);
                ble.setMTU(device.getBleAddress(), Ble.options().getMtu(), bleMtuCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    BleMtuCallback<BleModelDevice> bleMtuCallback = new BleMtuCallback<BleModelDevice>() {
        @Override
        public void onMtuChanged(final BleModelDevice device, int mtu, int status) {
            new Thread(() -> {
                AwsSmartConfigCommand awsSmartConfigCommand = new AwsSmartConfigCommand(macCode, Ble.options().getMtu(), awsNetworkInfoStr);
                try {
                    List<byte[]> list = awsSmartConfigCommand.packs();
                    if (list != null && list.size() > 0) {
                        for (byte[] bytes : list) {
                            ble.writeByUuid(device, bytes,
                                    Ble.options().getUuidService(),
                                    Ble.options().getUuidWriteCha(),
                                    bleWriteCallback);
                            Thread.sleep(200);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


        }
    };
    BleWriteCallback<BleModelDevice> bleWriteCallback = new BleWriteCallback<BleModelDevice>() {
        @Override
        public void onWriteSuccess(BleModelDevice device, BluetoothGattCharacteristic characteristic) {

        }
    };

    protected abstract void onReceiver(BleModelDevice device, String macCode, int errorCode, String message, String data) throws Exception;

}
