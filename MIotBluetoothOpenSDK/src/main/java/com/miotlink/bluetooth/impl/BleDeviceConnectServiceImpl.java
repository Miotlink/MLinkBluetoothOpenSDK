package com.miotlink.bluetooth.impl;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import com.miotlink.bluetooth.callback.BleConnectCallback;
import com.miotlink.bluetooth.callback.BleNotifyCallback;
import com.miotlink.bluetooth.callback.BleReadCallback;
import com.miotlink.bluetooth.callback.BleWriteCallback;
import com.miotlink.bluetooth.command.BindPuCommand;
import com.miotlink.bluetooth.command.CommmandBean;
import com.miotlink.bluetooth.command.DeviceVersionCommand;
import com.miotlink.bluetooth.command.IMessageProtocol;
import com.miotlink.bluetooth.command.UartCommand;
import com.miotlink.bluetooth.command.UnBindCommand;
import com.miotlink.bluetooth.listener.SmartNotifyDeviceConnectListener;
import com.miotlink.bluetooth.listener.SmartNotifyUartDataListener;
import com.miotlink.bluetooth.model.BleFactory;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.model.BluetoothDeviceStore;
import com.miotlink.bluetooth.service.Ble;
import com.miotlink.bluetooth.service.BleLog;
import com.miotlink.bluetooth.utils.HexUtil;
import com.miotlink.bluetooth.utils.ThreadUtils;

import java.util.Map;
import java.util.UUID;


/**
 * USER：create by qiaozhuang on 2024/10/14 14:24
 * EMAIL:qiaozhuang@miotlink.com
 */
class BleDeviceConnectServiceImpl extends BleConnectCallback<BleModelDevice> implements BleDeviceConnectService {

    Ble<BleModelDevice> ble = null;
    BleFactory bleFactory = new BleFactory<BleModelDevice>() {
        @Override
        public BleModelDevice create(String address, String name) {
            return new BleModelDevice(address, name);
        }
    };
    SmartNotifyDeviceConnectListener bleDeviceConnectListener = null;

    SmartNotifyUartDataListener smartNotifyUartDataListener = null;

    public BleDeviceConnectServiceImpl() {
        ble = Ble.getInstance();
    }

    @Override
    public void connect(String macCode, SmartNotifyDeviceConnectListener bleDeviceConnectListener) throws Exception {
        this.bleDeviceConnectListener = bleDeviceConnectListener;
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice != null) {
            BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
            if (connectedDevice != null) {
                if (bleDeviceConnectListener != null) {
                    bleDeviceConnectListener.onSmartNotifyConnectListener(2, "CONNECTED：" + bleModelDevice.getBleAddress(), bleModelDevice.getMacAddress());
                }
                ble.enableNotify(connectedDevice, true, bleNotifyCallback);
                return;
            }
            ble.connect(bleModelDevice.getBleAddress(), this);
        }
    }

    @Override
    public void sendMessage(String macCode, byte[] bytes) throws Exception {
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice != null) {
            BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
            if (connectedDevice == null) {
                return;
            }
            ThreadUtils.execute(() -> {
                try {
                    UartCommand uartCommand = new UartCommand(HexUtil.encodeHexStr(bytes));
                    ble.write(connectedDevice, uartCommand.pack(), bleModelDeviceBleWriteCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        if (smartNotifyUartDataListener != null) {
            smartNotifyUartDataListener.onNotifyUartData(macCode, 100, "Device is not Connect");
        }
    }

    @Override
    public void setSmartNotifyUartDataListener(SmartNotifyUartDataListener smartNotifyUartDataListener) throws Exception {
        this.smartNotifyUartDataListener = smartNotifyUartDataListener;
    }

    @Override
    public void sendMessage(String macCode, String ttContent) throws Exception {
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice != null) {
            BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
            if (connectedDevice == null) {
                return;
            }
            ThreadUtils.execute(() -> {
                try {
                    UartCommand uartCommand = new UartCommand(ttContent);
                    ble.write(connectedDevice, uartCommand.pack(), bleModelDeviceBleWriteCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return;
        }
        if (smartNotifyUartDataListener != null) {
            smartNotifyUartDataListener.onNotifyUartData(macCode, 100, "Device is not Connect");
        }
    }

    @Override
    public void getVersion(String macCode) throws Exception {
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice != null) {
            BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
            if (connectedDevice == null) {
                return;
            }
            ThreadUtils.execute(() -> {
                try {
                    DeviceVersionCommand deviceVersionCommand = new DeviceVersionCommand();
                    ble.write(connectedDevice, deviceVersionCommand.pack(), bleModelDeviceBleWriteCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        if (smartNotifyUartDataListener != null) {
            smartNotifyUartDataListener.onNotifyUartData(macCode, 100, "Device is not Connect");
        }
    }

    @Override
    public void unBindPu(String macCode, int kindId, int modelId) throws Exception {
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice != null) {
            BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
            if (connectedDevice == null) {
                return;
            }
            ThreadUtils.execute(() -> {
                try {
                    UnBindCommand deviceVersionCommand = new UnBindCommand(kindId, modelId);
                    ble.write(connectedDevice, deviceVersionCommand.pack(), bleModelDeviceBleWriteCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return;
        }
        if (smartNotifyUartDataListener != null) {
            smartNotifyUartDataListener.onNotifyUartData(macCode, 100, "Device is not Connect");
        }
    }

    @Override
    public void bindPu(String macCode) throws Exception {
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice != null) {
            BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
            if (connectedDevice == null) {
                return;
            }
            ThreadUtils.execute(() -> {
                try {
                    BindPuCommand deviceVersionCommand = new BindPuCommand();
                    ble.write(connectedDevice, deviceVersionCommand.pack(), bleModelDeviceBleWriteCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return;
        }
        if (smartNotifyUartDataListener != null) {
            smartNotifyUartDataListener.onNotifyUartData(macCode, 100, "Device is not Connect");
        }
    }

    @Override
    public void disconnect(String macCode) throws Exception {
        BleModelDevice bleModelDevice = BluetoothDeviceStore.getInstance().getBleModelDevice(macCode);
        if (bleModelDevice != null) {
            BleModelDevice connectedDevice = ble.getConnectedDevice(bleModelDevice.getBleAddress());
            if (connectedDevice == null) {
                return;
            }
            ble.disconnect(connectedDevice);
        }
    }

    @Override
    public void onConnectionChanged(BleModelDevice bleModelDevice) {
        try {
            if (bleModelDevice.isConnected()) {
                if (bleDeviceConnectListener != null) {
                    bleDeviceConnectListener.onSmartNotifyConnectListener(bleModelDevice.getConnectionState(), "CONNECTED ", bleModelDevice.getMacAddress());
                }
            } else if (bleModelDevice.isDisconnected()) {
                if (bleDeviceConnectListener != null) {
                    bleDeviceConnectListener.onSmartNotifyConnectListener(bleModelDevice.getConnectionState(), "DISCONNECT", bleModelDevice.getMacAddress());
                }
            } else if (bleModelDevice.isConnecting()) {
                if (bleDeviceConnectListener != null) {
                    bleDeviceConnectListener.onSmartNotifyConnectListener(bleModelDevice.getConnectionState(), "CONNECTING", bleModelDevice.getMacAddress());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServicesDiscovered(BleModelDevice device, BluetoothGatt gatt) {
        super.onServicesDiscovered(device, gatt);
    }

    @Override
    public void onReady(BleModelDevice device) {
        super.onReady(device);
        ble.enableNotify(device, true, bleNotifyCallback);
    }

    BleNotifyCallback<BleModelDevice> bleNotifyCallback = new BleNotifyCallback<BleModelDevice>() {
        @Override
        public void onChanged(BleModelDevice device, BluetoothGattCharacteristic characteristic) {
            try {
                UUID uuid = characteristic.getUuid();
                if (uuid.equals(Ble.options().getUuidReadCha())) {
                    byte[] value = characteristic.getValue();
                    if (value == null) {
                        return;
                    }
                    IMessageProtocol iMessageProtocol = new IMessageProtocol();
                    CommmandBean command = iMessageProtocol.getCommand(value);
                    if (command != null) {
                        Map<Integer, byte[]> values = command.getValues();
                        switch (command.getCode()) {
                            case 0x06:
                                if (values != null) {
                                    smartNotifyUartDataListener.onSmartNotifyUartDataListener(device.getBleAddress(), HexUtil.encodeHexStr(values.get(0)));
                                }
                                break;
                            case 0x04:
                                if (values != null) {
                                    smartNotifyUartDataListener.onSmartNotifyBindListener(device.getBleAddress(), Integer.parseInt(HexUtil.encodeHexStr(values.get(0)), 16), "");
                                }
                                break;
                            case 0x09:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    BleWriteCallback<BleModelDevice> bleModelDeviceBleWriteCallback = new BleWriteCallback<BleModelDevice>() {
        @Override
        public void onWriteSuccess(BleModelDevice device, BluetoothGattCharacteristic characteristic) {
            if (smartNotifyUartDataListener != null) {
                smartNotifyUartDataListener.onNotifyUartData(device.getBleAddress(), 100, "Device is not Connect");
            }
        }

        @Override
        public void onWriteFailed(BleModelDevice device, int failedCode) {
            super.onWriteFailed(device, failedCode);
            BleLog.e("onWriteFailed", failedCode + "");
        }
    };
}
