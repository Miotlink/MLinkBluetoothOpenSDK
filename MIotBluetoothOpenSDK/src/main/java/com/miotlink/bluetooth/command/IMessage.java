package com.miotlink.bluetooth.command;

import java.util.List;

/**
 * USER：create by qiaozhuang on 2024/11/14 15:07
 * EMAIL:qiaozhuang@miotlink.com
 */
public interface IMessage {
    /**
     * 根据自己的协议打包消息
     */
    byte[] pack()throws Exception;

    List<byte[]> packs()throws Exception;


}
