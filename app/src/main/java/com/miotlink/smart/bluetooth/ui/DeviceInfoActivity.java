package com.miotlink.smart.bluetooth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.miotlink.MLinkSmartBluetoothSDK;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.utils.HexUtil;
import com.miotlink.smart.bluetooth.R;
import com.miotlink.smart.bluetooth.base.BaseActivity;

import java.util.Formatter;

public class DeviceInfoActivity extends BaseActivity implements View.OnClickListener {

    private BleModelDevice bleModelDevice=null;
    private ImageView device_update_title_tv_login;
    private TextView ble_state_tv,device_update_title_tv_title,ble_version,ble_mac_version,ble_name_version,ble_rssi_version;

    private Button send_ble_btn;

    private Button ota_ble_btn;

    private Button rz_ble_btn;

    private boolean isconnect=false;

    private EditText send_ble_hex;

    String macCode="";

    public static void startIntent(Context mContext, Bundle bundle){
        Intent intent=new Intent(mContext,DeviceInfoActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }



    //    private LoadingViewDialog loadingViewDialog=null;
    @Override
    public void initView() throws Exception {
        device_update_title_tv_login=findViewById(R.id.device_update_title_tv_login);
        device_update_title_tv_title=findViewById(R.id.device_update_title_tv_title);
        ble_rssi_version=findViewById(R.id.ble_rssi_version);

        ble_mac_version=findViewById(R.id.ble_mac_version);
        ble_name_version=findViewById(R.id.ble_name_version);

        ble_version=findViewById(R.id.ble_version);
        ble_state_tv=findViewById(R.id.ble_state_tv);
        send_ble_btn=findViewById(R.id.send_ble_btn);
        ota_ble_btn=findViewById(R.id.ota_ble_btn);
        send_ble_hex=findViewById(R.id.send_ble_hex);
        rz_ble_btn=findViewById(R.id.rz_ble_btn);
        rz_ble_btn.setOnClickListener(this);
        send_ble_btn.setOnClickListener(this);
        ota_ble_btn.setOnClickListener(this);
        ble_state_tv.setOnClickListener(this);
        device_update_title_tv_login.setOnClickListener(this);
    }

    @Override
    public void initData() throws Exception {
        macCode=getIntent().getStringExtra("macCode");
//        bleModelDevice=getIntent().getParcelableExtra("device");
        bleModelDevice= MLinkSmartBluetoothSDK.getInstance().getScanDevice(macCode);
        if (bleModelDevice!=null){
            byte[] bytes = bleModelDevice.getScanRecord().getBytes();
            String s = HexUtil.encodeHexStr(bytes);
            send_ble_hex.setText(s);
            ble_mac_version.setText(bleModelDevice.getMacAddress());
            ble_name_version.setText(bleModelDevice.getBleName());
            int iRssi = Math.abs(bleModelDevice.getRssi());
            double power = (iRssi - 59) / 25.0;
            String mm = new Formatter().format("%.2f", Math.pow(10.0, power)).toString();
            Log.e("BLE","蓝牙距离"+mm);
            ble_rssi_version.setText(bleModelDevice.getRssi()+"dbm");
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_ble_details;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle=new Bundle();
        switch (v.getId()){
            case R.id.send_ble_btn:
//                bundle.putParcelable("device",bleModelDevice);
                bundle.putString("macCode",bleModelDevice.getMacAddress());
                DeviceControlActivity.startIntent(mContext,bundle);
                break;
            case R.id.device_update_title_tv_login:
                finish();
                break;
//            case R.id.ota_ble_btn:
//                bundle.putInt("configType",0);
//                bundle.putParcelable("device",bleModelDevice);
//                bundle.putString("macCode",bleModelDevice.getMacAddress());
//                DeviceSmartConfigActivity.startIntent(mContext,bundle);
//                break;
            case R.id.ble_state_tv:

                break;
            case R.id.rz_ble_btn:
                bundle.putParcelable("device",bleModelDevice);
                bundle.putString("macCode",bleModelDevice.getMacAddress());
                bundle.putInt("configType",1);
                DeviceSmartConfigActivity.startIntent(mContext,bundle);
                break;
        }
    }
}
