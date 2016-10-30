package com.yyl.server.model;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by yl on 2016/10/28.
 */
public class AesConf {
    /**
     * 密钥配置文件
     */
    public static final String CONFIG = "/conf/aes_key.ini";



    /**
     * 密钥长度 128,192,256  默认: 128
     * <p/>
     * AES 256位 加密需要获得无政策限制权限文件,主要是为了突破AES算法只能支持到128位的限制。如果未替换报 Illegal key size 错误
     * <p/>
     * 替换%JAVE_HOME%\jre\lib\security下的local_policy.jar 和 US_export_policy.jar
     * <p/>
     * 权限文件 下载
     * jdk5: http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-java-plat-419418.html#jce_policy-1.5.0-oth-JPR
     * jdk6: http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
     * jdk7: http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     */
    private int keySize = 128;

    /**
     * 迭代次数
     */
    private int iterationCount = 1000;

    /**
     * true:hex转码 or false:base64转码
     */
    private boolean hex;



    /**
     * 算法/模式/填充 :
     * <p/>
     * AES/CBC/NoPadding        不支持
     * AES/CBC/PKCS5Padding
     * AES/CBC/ISO10126Padding
     * <p/>
     * AES/CFB/NoPadding
     * AES/CFB/PKCS5Padding
     * AES/CFB/ISO10126Padding
     * <p/>
     * AES/ECB/NoPadding         不支持
     * AES/ECB/PKCS5Padding
     * <p/>
     * AES/OFB/NoPadding
     * AES/OFB/PKCS5Padding
     * AES/OFB/ISO10126Padding
     * <p/>
     * AES/PCBC/NoPadding        不支持
     * AES/PCBC/PKCS5Padding
     * AES/PCBC/ISO10126Padding
     */
    private String padding = "AES/CBC/PKCS5Padding";


    /**
     * 加密解密
     */
    private Cipher cipher = initCipher();


    /**
     * 默认密钥
     */
    private String key = "1234567890asdq@#";

    /**
     * 默认盐值
     */
    private String salt = "0123456789abcdef";

    /**
     * 默认向量
     */
    private String iv = "0123456789abcdef";

    /**
     * 配置刷新时间
     */
    private Date cacheDate;

    /**
     * 刷新间隔
     */
    private String interval;

    private Cipher initCipher() {
        try {
            return Cipher.getInstance(padding);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public boolean isHex() {
        return hex;
    }

    public void setHex(boolean hex) {
        this.hex = hex;
    }



    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        try {
            if (null == cipher) {
                this.cipher = Cipher.getInstance(padding);
            } else {
                this.cipher = cipher;
            }
        } catch (Exception e) {
            System.out.println("padding 模式错误");
            e.printStackTrace();
        }
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public Date getCacheDate() {
        return cacheDate;
    }

    public void setCacheDate(Date cacheDate) {
        this.cacheDate = cacheDate;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
