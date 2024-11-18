package com.miotlink.bluetooth.impl;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Message;
import com.miotlink.bluetooth.callback.BleConnectCallback;
import com.miotlink.bluetooth.callback.BleNotifyCallback;
import com.miotlink.bluetooth.callback.BleWriteCallback;
import com.miotlink.bluetooth.command.AwsSmartConfigCommand;
import com.miotlink.bluetooth.command.CommmandBean;
import com.miotlink.bluetooth.command.IMessageProtocol;
import com.miotlink.bluetooth.command.SmartDeviceNetworkCommand;
import com.miotlink.bluetooth.listener.BleSmartConfigListener;
import com.miotlink.bluetooth.model.BleFactory;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.model.BluetoothDeviceStore;
import com.miotlink.bluetooth.service.Ble;
import com.miotlink.bluetooth.service.BleLog;

import java.util.List;
import java.util.UUID;

/**
 * USER：create by qiaozhuang on 2024/10/14 16:06
 * EMAIL:qiaozhuang@miotlink.com
 */
 class BleDeviceSmartConfigServiceImpl implements BleDeviceSmartConfigService {

    BleSmartConfigListener bleSmartConfigListener = null;


    private String macCode = "";
    private String ssid = "";
    private String password = "";
    private int delayMillis = 60;
    BleFactory bleFactory = new BleFactory<BleModelDevice>() {
        @Override
        public BleModelDevice create(String address, String name) {
            return new BleModelDevice(address, name);
        }
    };
    Ble<BleModelDevice> ble = null;

    private String errorMessage = "";

    private int errorCode = 0;

    private BleModelDevice bleModelDevice = null;

    private String awsNetworkInfo = "";

    @Override
    public void startSmartConfig(String macCode, String routeName, String password, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception {
        this.bleSmartConfigListener = bleSmartConfigListener;
        ble = Ble.getInstance();
        this.macCode = macCode;
        this.ssid = routeName;
        this.password = password;
        if (delayMillis > 60) {
            this.delayMillis = delayMillis;
        }
        bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        Ble.options().setFactory(bleFactory);

        if (bleModelDevice == null) {
            bleSmartConfigListener.onLinkSmartConfigListener(7000, "no find device", null);
            return;
        }
        handler.sendEmptyMessageDelayed(1000, delayMillis * 1000);
        ble.connect(bleModelDevice.getBleAddress(), new BleConnectCallback<BleModelDevice>() {
            @Override
            public void onConnectionChanged(BleModelDevice device) {
            }

            @Override
            public void onReady(BleModelDevice device) {
                super.onReady(device);
                ble.enableNotify(device, true, bleNotifyCallback);
            }

            @Override
            public void onServicesDiscovered(BleModelDevice device, BluetoothGatt gatt) {
                super.onServicesDiscovered(device, gatt);
                try {
                    SmartDeviceNetworkCommand bluetoothProtocol = new SmartDeviceNetworkCommand();
                    bluetoothProtocol.setSsid(ssid);
                    bluetoothProtocol.setPassword(password);
                    byte[] bytes = bluetoothProtocol.pack();
                    ble.writeByUuid(device, bytes, Ble.options().getUuidService(), Ble.options().getUuidWriteCha(), bleWriteCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onAwsStartSmartConfig(String macCode, String awsNetworkInfo, int delayMillis, BleSmartConfigListener bleSmartConfigListener) throws Exception {
        this.bleSmartConfigListener = bleSmartConfigListener;
        this.macCode = macCode;
        this.awsNetworkInfo = awsNetworkInfo;
        if (delayMillis > 60) {
            this.delayMillis = delayMillis;
        }

        bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        Ble.options().setFactory(bleFactory);
        ble = Ble.getInstance();
        if (bleModelDevice == null) {
            bleSmartConfigListener.onLinkSmartConfigListener(7000, "no find device", null);
            return;
        }
        handler.sendEmptyMessageDelayed(1000, delayMillis * 1000);
        ble.connect(bleModelDevice.getBleAddress(), new BleConnectCallback<BleModelDevice>() {
            @Override
            public void onConnectionChanged(BleModelDevice device) {
            }

            @Override
            public void onReady(BleModelDevice device) {
                super.onReady(device);
                ble.enableNotify(device, true, bleNotifyCallback);
            }

            @Override
            public void onServicesDiscovered(BleModelDevice device, BluetoothGatt gatt) {
                super.onServicesDiscovered(device, gatt);
                AwsSmartConfigCommand awsSmartConfigCommand = new AwsSmartConfigCommand(macCode, Ble.options().getMtu(), awsNetworkInfo);
                new Thread(() -> {
                    try {
                        List<byte[]> list = awsSmartConfigCommand.packs();
                        if (list != null && list.size() > 0) {
                            for (byte[] bytes : list) {
                                ble.writeByUuid(device, bytes,
                                        Ble.options().getUuidService(),
                                        Ble.options().getUuidWriteCha(),
                                        bleWriteCallback);
                                Thread.sleep(300);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });
    }

    @Override
    public void stopSmartConfig() throws Exception {
        handler.removeMessages(1000);
        if (ble != null && bleModelDevice != null) {
            ble.disconnect(bleModelDevice);
        }
    }

    BleWriteCallback<BleModelDevice> bleWriteCallback = new BleWriteCallback<BleModelDevice>() {
        @Override
        public void onWriteSuccess(BleModelDevice device, BluetoothGattCharacteristic characteristic) {
            try {
                if (bleSmartConfigListener == null) {
                    bleSmartConfigListener.onLinkSmartConfigListener(7000, "no find device", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    BleNotifyCallback<BleModelDevice> bleNotifyCallback = new BleNotifyCallback<BleModelDevice>() {
        @Override
        public void onChanged(BleModelDevice device, BluetoothGattCharacteristic characteristic) {
            try {
                bleModelDevice = device;
                UUID uuid = characteristic.getUuid();
                if (uuid.equals(Ble.options().getUuidReadCha())) {
                    byte[] value = characteristic.getValue();
                    if (value==null){
                        BleLog.d("onChanged", "value is empty");
                        return;
                    }
                    IMessageProtocol iMessageProtocol=new IMessageProtocol();
                    CommmandBean command = iMessageProtocol.getCommand(value);
//                    BluetoothProtocol bluetoothProtocol = new BluetoothProtocolImpl();
//                    Map<String, Object> decode = bluetoothProtocol.decode(value);
//                    if (decode != null && decode.containsKey("code") && decode.containsKey("value")) {
//                        int code = (int) decode.get("code");
//                        if (code == 4) {
//                            String valueCode = (String) decode.get("value");
//                            BleLog.e("onChanged", "Code:" + code + "value:" + valueCode);
//                            if (!TextUtils.isEmpty(valueCode)) {
//                                if (TextUtils.equals("00", valueCode)) {
//                                    errorMessage = "未配网";
//                                    errorCode = 7001;
//                                } else if (TextUtils.equals("01", valueCode)) {
//                                    errorMessage = "未配网";
//                                    errorMessage = ble.getContext().getResources().getString(R.string.ble_device_start_net_error_message);
//                                    errorCode = 7001;
//                                } else if (TextUtils.equals("02", valueCode)) {
//                                    errorMessage = ble.getContext().getResources().getString(R.string.ble_device_start_connting_error_message);
//                                    errorCode = 7002;
//                                } else if (TextUtils.equals("03", valueCode)) {
//                                    errorCode = 7003;
//                                    errorMessage = ble.getContext().getResources().getString(R.string.ble_device_start_error_message);
//                                } else if (TextUtils.equals("0f", valueCode)) {
//                                    handler.removeMessages(1000);
//                                    errorMessage = "配网成功。";
//                                    errorCode = 7015;
//                                    ble.disconnect(device);
//                                    bleSmartConfigListener.onLinkSmartConfigListener(errorCode, errorMessage, device.getMacAddress());
//                                } else if (TextUtils.equals("ff", valueCode)) {
//                                    ble.disconnect(device);
//                                    errorCode = 7255;
//                                    errorMessage = ble.getContext().getResources().getString(R.string.ble_device_connect_error_message);
//                                    bleSmartConfigListener.onLinkSmartConfigListener(errorCode, errorMessage, device.getMacAddress());
//                                }
//                            }
//                        } else if (code == 0x13) {
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
//                                                            errorCode = jsonData.getInt("Code");
//                                                            String messageDescribe = jsonData.getString("Describe");
//                                                            if (errorCode == 65) {
//                                                                handler.removeMessages(1000);
//                                                                ble.disconnect(device);
//                                                                errorMessage = "配网成功。";
//                                                                errorCode = 7015;
//                                                                if (bleSmartConfigListener != null) {
//                                                                    bleSmartConfigListener.onLinkSmartConfigListener(errorCode, errorMessage, macCode);
//                                                                }
//                                                                return;
//                                                            }
//                                                            errorMessage = messageDescribe;
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                try {
                    if (bleSmartConfigListener != null) {
                        bleSmartConfigListener.onLinkSmartConfigTimeOut(errorCode, errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
