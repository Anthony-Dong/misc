package com.http.test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.stream.IntStream;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public class DESUtil {

    private static final String KEY_ALGORITHM = "DES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";//默认的加密算法

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param key     加密密钥
     * @return 返回Base64转码后的加密数据
     */
    private static byte[] encrypt(byte[] content, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
        return cipher.doFinal(content);//通过Base64转码返回
    }

    /**
     * AES 解密操作
     *
     * @param result
     * @param key
     * @return
     */
    private static byte[] decrypt(byte[] result, SecretKeySpec key) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(result);
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key) throws NoSuchAlgorithmException {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        //AES 要求密钥长度为 128
        kg.init(56, new SecureRandom(key.getBytes()));
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
    }

    public static void main(String[] args) throws Exception {
        String key = "password";
        SecretKeySpec secretKey = getSecretKey(key);
        byte[] bytes = String.format("hello%d", 1).getBytes();

        long start = System.currentTimeMillis();
        IntStream.range(0, 1).forEach(value -> {
            try {
                byte[] s1 = DESUtil.encrypt(bytes, secretKey);
                byte[] encrypt = DESUtil.decrypt(s1, secretKey);
                System.out.println(new String(s1) + "  , " + new String(encrypt));
            } catch (Exception e) {
                //
            }
        });
        System.out.println(System.currentTimeMillis() - start);
    }

}