package com.miotlink.smart.bluetooth.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.miotlink.MLinkSmartBluetoothSDK;
import com.miotlink.bluetooth.listener.BleDeviceScanListener;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.bluetooth.utils.Utils;
import com.miotlink.smart.bluetooth.R;
import com.miotlink.smart.bluetooth.adapter.ScanDeviceAdapter;
import com.miotlink.smart.bluetooth.base.BaseActivity;
import com.miotlink.smart.bluetooth.view.RadarView;

import java.util.ArrayList;
import java.util.List;

public
class ScanDeviceActivity extends BaseActivity implements BleDeviceScanListener {

    private RecyclerView recyclerView;
    private ScanDeviceAdapter scanDeviceAdapter = null;


    private TextView textView;

    private List<BleModelDevice> list = new ArrayList<>();

    private RadarView radarView = null;

    @Override
    public void initView() throws Exception {

        radarView = findViewById(R.id.radar);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        scanDeviceAdapter = new ScanDeviceAdapter(R.layout.item_scan_device);
        recyclerView.getItemAnimator().setChangeDuration(300);
        recyclerView.getItemAnimator().setMoveDuration(300);
        recyclerView.setAdapter(scanDeviceAdapter);

        scanDeviceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BleModelDevice item = (BleModelDevice) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("macCode", item.getMacAddress());
//                bundle.putParcelable("device", item);
                DeviceInfoActivity.startIntent(mContext, bundle);
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_sacn_device;
    }


    @Override
    public void initData() throws Exception {

    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        if (scanDeviceAdapter != null) {
            scanDeviceAdapter.setNewInstance(list);
        }
        checkGpsStatus();
        if (radarView != null) {
            radarView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (radarView != null) {
            radarView.stop();
        }
        MLinkSmartBluetoothSDK.getInstance().onStopScan();
    }

    private void checkGpsStatus() {
        MLinkSmartBluetoothSDK.getInstance().startScan(this);
    }


    @Override
    public void onDiscoverDevices(BleModelDevice bleModelDevice) throws Exception {
        list.add(bleModelDevice);
        scanDeviceAdapter.setNewInstance(list);
        scanDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHasBindDiscoverDevices(BleModelDevice bleModelDevice) throws Exception {
        list.add(bleModelDevice);
        scanDeviceAdapter.setNewInstance(list);
        scanDeviceAdapter.notifyDataSetChanged();
    }
}
