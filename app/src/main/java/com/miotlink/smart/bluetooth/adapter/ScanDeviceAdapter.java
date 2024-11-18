package com.miotlink.smart.bluetooth.adapter;



import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.miotlink.bluetooth.model.BleModelDevice;
import com.miotlink.smart.bluetooth.R;

import org.jetbrains.annotations.NotNull;


public class ScanDeviceAdapter extends BaseQuickAdapter<BleModelDevice, BaseViewHolder> {

    public ScanDeviceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder viewHolder, BleModelDevice bleMLDevice) {

        viewHolder.setText(R.id.bluetooth_tv,bleMLDevice.getBleName());
    }
}
