package com.miotlink.bluetooth.command;


/**
 * USER：create by qiaozhuang on 2024/11/15 14:28
 * EMAIL:qiaozhuang@miotlink.com
 */
public interface IReaderProtocol {

    CommmandBean getCommand(byte[] bytes)throws Exception;

}
