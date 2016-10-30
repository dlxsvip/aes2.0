package com.yyl.client.utils;

import com.yyl.client.model.AesConf;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * AES 256位 加密需要获得无政策限制权限文件
 * 主要是为了突破AES算法只能支持到128位的限制。如果未替换报 Illegal key size 错误
 * <p/>
 * 替换%JAVE_HOME%\jre\lib\security下的local_policy.jar 和 US_export_policy.jar
 * <p/>
 * 权限文件 下载
 * jdk5: http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-java-plat-419418.html#jce_policy-1.5.0-oth-JPR
 * jdk6: http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
 * jdk7: http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
 * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * Created by yl on 2016/9/22.
 */
public class AESUtil {

    /**
     * 算法模式 AES
     */
    private static final String MODE = "AES";

    /**
     * 配置
     */
    private static AesConf conf;

    /**
     * AES 单例
     */
    private static AESUtil INSTANCE;

    /**
     * 私有化构造函数
     */
    private AESUtil() {
    }


    /**
     * 获得AES 实例
     *
     * @return AES 实例
     */
    public static synchronized AESUtil getInstance(AesConf conf) {
        if (null == INSTANCE) {
            INSTANCE = new AESUtil();
            AESUtil.conf = conf;
        }

        return INSTANCE;
    }


    /**
     * 加密
     *
     * @param txt 待加密的内容
     * @return 密文
     */
    public String encrypt(String txt) {
        return encryptByConf(txt);
    }

    /**
     * 加密
     *
     * @param txt 待加密的内容
     * @return 密文
     */
    private String encryptByConf(String txt) {
        return toStr(Cipher.ENCRYPT_MODE, encryptByte(txt));
    }

    /**
     * 解密
     *
     * @param txt 待解密的内容
     * @return 明文
     */
    public String decrypt(String txt) {
        return decryptByConf(txt);
    }

    /**
     * 解密
     *
     * @param txt 待解密的内容
     * @return 明文
     */
    private String decryptByConf(String txt) {
        return toStr(Cipher.DECRYPT_MODE, decryptByte(txt));
    }


    // 加密后的 byte[]
    private byte[] encryptByte(String txt) {
        // 在初始化 cipher
        initCipher(Cipher.ENCRYPT_MODE);

        return toByte(Cipher.ENCRYPT_MODE, txt);
    }

    // 解密后的 byte[]
    private byte[] decryptByte(String txt) {

        // 在初始化 cipher
        initCipher(Cipher.DECRYPT_MODE);

        return toByte(Cipher.DECRYPT_MODE, txt);
    }


    // 生成byte[]
    private byte[] toByte(int mode, String txt) {
        byte[] b = null;
        try {
            byte[] tmp = null;
            if (Cipher.ENCRYPT_MODE == mode) {
                tmp = txt.getBytes("utf-8");
            } else if (Cipher.DECRYPT_MODE == mode) {
                // 解密前字符逆转码
                tmp = conf.isHex() ? HexUtil.hex2byte(txt) : new BASE64Decoder().decodeBuffer(txt);
            }
            b = conf.getCipher().doFinal(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    // 生成字符串
    private String toStr(int mode, byte[] b) {
        String ss = "";
        if (Cipher.ENCRYPT_MODE == mode) {
            // 加密后 字符串转码
            ss = conf.isHex() ? HexUtil.byte2hex(b) : new BASE64Encoder().encode(b);
        } else if (Cipher.DECRYPT_MODE == mode) {
            ss = new String(b);
        }

        return ss;
    }


    // 初始化 Cipher
    private void initCipher(int cipherMode) {
        try {
            Key keyObj = keyObject();

            if (conf.getPadding().contains("ECB")) {
                conf.getCipher().init(cipherMode, keyObj);
            } else {
                conf.getCipher().init(cipherMode, keyObj, new IvParameterSpec(conf.getIv().getBytes()));
            }
        } catch (InvalidKeyException e) {
            System.out.println("请替换%JAVE_HOME%\\jre\\lib\\security下的local_policy.jar 和 US_export_policy.jar");
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    // 获得 密钥对象
    private Key keyObject() {
        if (StringUtil.isEmpty(conf.getSalt())) {
            return getSecretKeySpec();
        } else {
            return getSecretKey();
        }
    }

    private SecretKeySpec getSecretKeySpec() {
        SecretKeySpec skey = null;
        try {
            // 强加密随机数生成器
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            // 使用key作为种子
            secureRandom.setSeed(conf.getKey().getBytes());
            //（对称）密钥生成器  生成KEY
            KeyGenerator kgen = KeyGenerator.getInstance(MODE);
            kgen.init(conf.getKeySize(), secureRandom);

            // 生成秘密（对称）密钥
            skey = new SecretKeySpec(kgen.generateKey().getEncoded(), MODE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return skey;
    }

    private SecretKey getSecretKey() {
        SecretKey skey = null;
        try {
            //密钥工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            //组成密钥内容的（透明）规范。
            KeySpec keySpec = new PBEKeySpec(conf.getKey().toCharArray(), conf.getSalt().getBytes(), conf.getIterationCount(), conf.getKeySize());
            //生成秘密（对称）密钥
            skey = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), MODE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return skey;
    }


}
