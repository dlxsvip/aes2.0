package com.yyl.server.utils;

import com.yyl.client.utils.FileUtil;
import com.yyl.server.model.AesConf;
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

    private AesConf conf = new AesConf();

    /**
     * AES 单例
     */
    private static AESUtil INSTANCE;


    /**
     * 获得AES 实例
     *
     * @return AES 实例
     */
    public static synchronized AESUtil getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new AESUtil();
        }

        return INSTANCE;
    }

    /**
     * 私有化构造函数
     */
    private AESUtil() {
        //读取配置文件,更新默认密钥
        readConf();
    }


    /**
     * 加密
     *
     * @param txt 待加密的内容
     * @return 密文
     */
    public String encrypt(String txt) {
        return encrypt(txt, null);
    }

    /**
     * 加密
     *
     * @param txt 待加密的内容
     * @param key 密钥
     * @return 密文
     */
    public String encrypt(String txt, String key) {
        return encrypt(txt, key, null);
    }

    /**
     * 加密
     *
     * @param txt  待加密的内容
     * @param key  密钥
     * @param salt 盐值
     * @return 密文
     */
    public String encrypt(String txt, String key, String salt) {
        return encrypt(txt, key, salt, null, null, null, null, null, null);
    }

    /**
     * 加密
     *
     * @param txt            待加密的内容
     * @param key            密钥
     * @param salt           盐值
     * @param iv             向量
     * @param hex            true：hex转码 false：base64转码
     * @param keySize        128 , 192 or 256
     * @param padding        模式
     * @param iterationCount 迭代次数
     * @return 密文
     */
    public String encrypt(String txt, String key, String salt, String iv, Boolean hex, Integer keySize, String padding, Integer iterationCount, Boolean refresh) {
        return encryptByConf(txt, aesConfig(key, salt, iv, hex, keySize, padding, iterationCount, refresh));
    }

    /**
     * 加密
     *
     * @param txt  待加密的内容
     * @param conf 加密配置项
     * @return 密文
     */
    public String encryptByConf(String txt, Map<String, String> conf) {
        return toStr(Cipher.ENCRYPT_MODE, encryptByte(txt, conf));
    }

    /**
     * 解密
     *
     * @param txt 待解密的内容
     * @return 明文
     */
    public String decrypt(String txt) {
        return decrypt(txt, null);
    }

    /**
     * 解密
     *
     * @param txt 待解密的内容
     * @param key 密钥
     * @return 明文
     */
    public String decrypt(String txt, String key) {
        return decrypt(txt, key, null);
    }

    /**
     * 解密
     *
     * @param txt  待解密的内容
     * @param key  密钥
     * @param salt 盐值
     * @return 明文
     */
    public String decrypt(String txt, String key, String salt) {
        return decrypt(txt, key, salt, null, null, null, null, null, null);
    }

    /**
     * 解密
     *
     * @param txt            待解密的内容
     * @param key            密钥
     * @param salt           盐值
     * @param iv             向量
     * @param hex            true：hex转码 false：base64转码
     * @param keySize        128 , 192 or 256
     * @param padding        模式
     * @param iterationCount 迭代次数
     * @return 明文
     */
    public String decrypt(String txt, String key, String salt, String iv, Boolean hex, Integer keySize, String padding, Integer iterationCount, Boolean refresh) {
        return decryptByConf(txt, aesConfig(key, salt, iv, hex, keySize, padding, iterationCount, refresh));
    }

    /**
     * 解密
     *
     * @param txt  待解密的内容
     * @param conf 解密配置项
     * @return 明文
     */
    public String decryptByConf(String txt, Map<String, String> conf) {
        return toStr(Cipher.DECRYPT_MODE, decryptByte(txt, conf));
    }

    /**
     * 封装 加密 or 解密配置项
     *
     * @param key            密钥
     * @param salt           盐值
     * @param iv             向量
     * @param hex            true：hex转码 false：base64转码
     * @param keySize        128 , 192 or 256
     * @param padding        模式
     * @param iterationCount 迭代次数
     * @return aes 参数
     */
    public Map<String, String> aesConfig(String key, String salt, String iv, Boolean hex, Integer keySize, String padding, Integer iterationCount, Boolean refresh) {
        Map<String, String> map = new HashMap<String, String>();
        if (!StringUtil.isEmpty(key)) {
            map.put("key", key);
        }

        if (!StringUtil.isEmpty(salt)) {
            map.put("salt", salt);
        }

        if (!StringUtil.isEmpty(iv)) {
            map.put("iv", iv);
        }

        if (null != hex) {
            map.put("hex", String.valueOf(hex));
        }

        if (null != keySize) {
            map.put("keySize", keySize.toString());
        }
        if (!StringUtil.isEmpty(padding)) {
            map.put("padding", padding);
        }

        if (null != iterationCount) {
            map.put("iterationCount", iterationCount.toString());
        }

        if (null != refresh) {
            map.put("refresh", String.valueOf(refresh));
        }

        return map;
    }

    // 加密后的 byte[]
    private byte[] encryptByte(String txt, Map<String, String> config) {
        if (config.containsKey("refresh") && Boolean.valueOf(config.get("refresh"))) {
            readConf();
        } else {
            // 先初始化 aesConf
            initAesConf(config);
        }

        // 在初始化 cipher
        initCipher(Cipher.ENCRYPT_MODE);

        return toByte(Cipher.ENCRYPT_MODE, txt);
    }

    // 解密后的 byte[]
    private byte[] decryptByte(String txt, Map<String, String> config) {
        if (config.containsKey("refresh")) {
            readConf();
        } else {
            // 先初始化 aesConf
            initAesConf(config);
        }

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

    // 读取配置文件
    private void readConf() {
        try {
            if (isCache()) {
                //String filePath = AESUtil.class.getResource(AesConf.CONFIG).getPath();

                String workPath = System.getProperty("user.dir");
                String confPath = workPath + "/build" + conf.CONFIG;
                confPath = FileUtil.switchPath(confPath);

                File keyFile = new File(confPath);
                Map<String, String> prop = ReadUtil.read(keyFile, "UTF-8");
                if (prop.size() > 0) {
                    initAesConf(prop);
                }

                System.out.println("---------------------------刷新配置");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化 AesConf
    private void initAesConf(Map<String, String> config) {
        try {

            if (valueNotNull(config, "keySize")) {
                Integer keySize = Integer.parseInt(config.get("keySize"));
                if ((128 == keySize || 192 == keySize || 256 == keySize) && keySize != conf.getKeySize()) {
                    conf.setKeySize(keySize);
                }
            }

            if (valueNotNull(config, "key")) {
                String key = config.get("key");
                if (!key.equals(conf.getKey())) {
                    conf.setKey(key);
                }
            }

            if (valueNotNull(config, "salt")) {
                String salt = config.get("salt");
                if (!salt.equals(conf.getSalt())) {
                    conf.setSalt(salt);
                }
            }

            if (valueNotNull(config, "iv")) {
                String iv = config.get("iv");
                if (!iv.equals(conf.getIv())) {
                    conf.setIv(iv);
                }
            }

            if (valueNotNull(config, "hex")) {
                Boolean hex = Boolean.valueOf(config.get("hex"));
                if (hex != conf.isHex()) {
                    conf.setHex(hex);
                }
            }

            if (valueNotNull(config, "padding")) {
                String padding = config.get("padding");
                if (!padding.equals(conf.getPadding())) {
                    conf.setPadding(padding);
                    conf.setCipher(Cipher.getInstance(padding));
                }
            }

            if (valueNotNull(config, "iterationCount")) {
                Integer iterationCount = Integer.parseInt(config.get("iterationCount"));
                if (iterationCount != conf.getIterationCount()) {
                    conf.setIterationCount(iterationCount);
                }
            }


            if (valueNotNull(config, "cacheTime")) {
                conf.setCacheDate(new Date());
                String interval = config.get("cacheTime");
                if (!interval.equals(conf.getInterval())) {
                    conf.setInterval(interval);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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


    // map判断key是否存在并且此key对应的value是否为null
    private boolean valueNotNull(Map map, String key) {
        return map.containsKey(key) && null != map.get(key);
    }


    private boolean isCache() {
        try {
            // 上次刷新缓存的时间
            if (null == conf.getCacheDate()) {
                return true;
            }

            long cacheTime = 0;
            String granularity = "0";
            String tmp = conf.getInterval();
            if ("0".equals(tmp)) {
                return false;
            } else if (tmp.contains("s")) {
                granularity = tmp.replace("s", "");
                cacheTime = Long.parseLong(granularity) * 1000;
            } else if (tmp.contains("m")) {
                granularity = tmp.replace("m", "");
                cacheTime = Long.parseLong(granularity) * 1000 * 60;
            } else if (tmp.contains("h")) {
                granularity = tmp.replace("h", "");
                cacheTime = Long.parseLong(granularity) * 1000 * 60 * 60;
            } else if (tmp.contains("d")) {
                granularity = tmp.replace("d", "");
                cacheTime = Long.parseLong(granularity) * 1000 * 60 * 60 * 24;
            } else {
                return false;
            }
            // 现在时间和上次刷新缓存的时间差
            long diff = System.currentTimeMillis() - conf.getCacheDate().getTime();
            if (diff > cacheTime) {
                // 重新读配置文件
                return true;
            }


        } catch (Exception e) {
            // 异常，则重新读取配置文件
            return true;
        }


        return false;
    }

    public static void main(String[] args) {

        AESUtil aes = AESUtil.getInstance();
        System.out.println("密钥：" + aes.conf.getKey());
        System.out.println("盐值：" + aes.conf.getSalt());
        System.out.println("向量：" + aes.conf.getIv());


        String txt = "aaaaaaa";

        String encrypt = aes.encrypt(txt);
        System.out.println("加密：" + encrypt + "      " + encrypt.length());

        String decrypt = aes.decrypt(encrypt);
        System.out.println("解密：" + decrypt);

    }

}
