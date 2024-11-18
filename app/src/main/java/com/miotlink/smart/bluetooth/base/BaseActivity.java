package com.miotlink.smart.bluetooth.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.miotlink.smart.bluetooth.R;
import com.miotlink.smart.bluetooth.view.StatusBarUtils;


public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;

    /** The dlg. */
    protected ProgressDialog dlg;

    /**
     * Show progress dialog.
     */
    public void showProgressDialog() {
        if (dlg == null) {
            dlg = new ProgressDialog(mContext);
            dlg.setMessage("获取数据中,请耐心等待");
            dlg.setCanceledOnTouchOutside(false);
        }
        if (!dlg.isShowing())
            dlg.show();
    }

    /**
     * Cancel progress dialog.
     */
    public void cancelProgressDialog() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(getContentView());
            StatusBarUtils.setColor(mContext, getResources().getColor(R.color.white));
            StatusBarUtils.setTextDark(mContext,true);
            mContext=this;
            initView();

            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void initView()throws Exception;

    public abstract int getContentView();

    public abstract void initData()throws Exception;
}
