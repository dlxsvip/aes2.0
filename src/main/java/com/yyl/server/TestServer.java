package com.yyl.server;

import com.yyl.client.utils.LogUtil;
import com.yyl.server.utils.AESUtil;
import com.yyl.server.utils.HexUtil;
import org.apache.log4j.Logger;

import java.security.SecureRandom;

/**
 * Created by yl on 2016/9/29.
 */
public class TestServer {

    private static final Logger log = Logger.getLogger(TestServer.class);

    public static void main(String[] args) {

        LogUtil.initLog();

        String txt = "ylyan";
        String key = "1234567890asdq@#";

        String salt = getRandom();
        log.info("salt:"+salt);
        String iv = getRandom();
        log.info("iv:"+iv);

        aes128(salt, iv, key, txt);
        //aes256(salt, iv, key, txt);
        //aes256(salt, "", key, txt);
        //aes256("", iv, key, txt);
        //aes256("", "", key, txt);

        //String salt ="c23cefabd7f8d7bfbe65c95e7c6490d9";
        //String iv ="9ab2380116a3022f5c68eabf0b40e988";
        //String ss = "Y9a5FVdCBIlptfqAVPgzmg==";
        //decrypt(128,salt,iv,key,ss);
    }




    private static void aes128(String salt, String iv, String key, String txt) {
        AESUtil aes = AESUtil.getInstance();


        System.out.println("盐值：");
        System.out.println("向量：" + iv);
        System.out.println("密钥：" + key);

        String encrypt = aes.encrypt(txt);
        System.out.println("加密：" + encrypt + "   "+ encrypt.length());
        log.info("加密: "+txt +" --> " +encrypt);
        String decrypt = aes.decrypt(encrypt);
        System.out.println("解密：" + decrypt);
        log.info("解密: "+encrypt +" --> " +decrypt);

    }

    private static String getRandom() {
        SecureRandom random = new SecureRandom();
        // 16 位长度
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        // 转 hex
        String salt = HexUtil.byte2hex(saltBytes);

        return salt;
    }


}
