package com.miotlink.bluetooth.utils;

import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {

    private static final String DEFAULT_KEY = "abcdefg123456789";

    private static final String IV = "0000000000000000";


    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @param key  加密key
     * @param iv   加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static byte[] encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv   解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(byte[] data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] original = cipher.doFinal(data);
        return new String(original, StandardCharsets.UTF_8);
    }

    /**
     * 使用默认的key和iv加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(String macCode,String data) throws Exception {
        return encrypt(data, macCode, IV);
    }

    /**
     * 使用默认的key和iv解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(byte[] data) throws Exception {
        return desEncrypt(data, DEFAULT_KEY, IV);
    }


}
