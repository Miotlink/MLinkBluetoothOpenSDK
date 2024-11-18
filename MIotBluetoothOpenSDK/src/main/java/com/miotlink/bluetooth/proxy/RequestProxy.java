package com.miotlink.bluetooth.proxy;

import android.content.Context;


import com.miotlink.bluetooth.request.ConnectRequest;
import com.miotlink.bluetooth.request.DescriptorRequest;
import com.miotlink.bluetooth.request.MtuRequest;
import com.miotlink.bluetooth.request.NotifyRequest;
import com.miotlink.bluetooth.request.ReadRequest;
import com.miotlink.bluetooth.request.ReadRssiRequest;
import com.miotlink.bluetooth.request.Rproxy;
import com.miotlink.bluetooth.request.ScanRequest;
import com.miotlink.bluetooth.request.WriteRequest;
import com.miotlink.bluetooth.service.BleLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * Created by LiuLei on 2017/9/1.
 */

public class RequestProxy implements InvocationHandler{
    private static final String TAG = "RequestProxy";

    private RequestProxy(){}

    private Object receiver;

    public static RequestProxy newProxy(){
        return new RequestProxy();
    }

    //Bind the delegate object and return the proxy class
    public Object bindProxy(Context context, Object tar){
        this.receiver = tar;
        //绑定委托对象，并返回代理类
        BleLog.d(TAG, "bindProxy: "+"Binding agent successfully");
        Rproxy.init(ConnectRequest.class, MtuRequest.class,
                NotifyRequest.class, ReadRequest.class,
                ReadRssiRequest.class, ScanRequest.class,
                WriteRequest.class, DescriptorRequest.class);
        return Proxy.newProxyInstance(
                tar.getClass().getClassLoader(),
                tar.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(receiver, args);
    }
}
