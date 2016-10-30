package com.yyl.client;

import com.yyl.client.model.AesConf;
import com.yyl.client.utils.*;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import java.io.File;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;

/**
 * AES 加密工具 5.0 <br>
 * Created by y0507 on 2016/9/22.
 * <p/>
 */
public class RunAes {

    private static final Logger log = Logger.getLogger(RunAes.class);

    /**
     * 本地调试开关
     */
    private static boolean localDebugger = false;

    /**
     * 加密 or 解密
     */
    private static String type;

    /**
     * 加密  or 解密 内容
     */
    private static String txt;

    private static AesConf conf = new AesConf();


    private static int random_length = 32;

    public static void main(String[] args) {
        LogUtil.initLog(localDebugger);

        log.info("参数:" + JsonUtil.obj2str(args));

        // 初始化
        readConf();


        // 本地调试
        if (localDebugger) {
            // 加密
            //args = "-e 416110 -k 123".split(" ");

            // 解密
            //args = "-d a29b9ba5bf025c154c05d8206b5b2230 -k 123 ".split(" ");

            log.info("本地调试" + args);
        }

        // 参数解析
        parms(args);

        /*
        // 打印参数
        for (String str:args){
            System.out.print(str+",");
        }
        System.out.println();
        */


        if (StringUtil.isEmpty(txt)) {
            return;
        }

        // 执行
        execute();
    }

    private static void execute() {

        // 初始化AES
        AESUtil aes = AESUtil.getInstance(conf);

        String mm = "";
        if ("-e".equals(type)) {
            mm = aes.encrypt(txt);
            log.info("加密 " + txt + " --> " + mm);
        } else if ("-d".equals(type)) {
            mm = aes.decrypt(txt);
            log.info("解密 " + txt + " --> " + mm);
        } else if ("-r".equals(type)) {
            // 随机数长度
            random_length = Integer.parseInt(txt);
            mm = getRandom(random_length);
            log.info("随机数 " + mm + "   " + random_length);
        }

        System.out.println(mm);
        System.out.println();
    }

    // 参数解析
    private static void parms(String[] args) {
        for (int i = 0; i < args.length; i++) {
            type = args[0];

            if ("-keysize".equals(args[i])) {
                conf.setKeySize(Integer.parseInt(args[++i]));
            }

            if ("-p".equals(args[i])) {
                conf.setPadding(args[++i]);
            }

            if ("-s".equals(args[i])) {
                conf.setSalt(args[++i]);
            }

            if ("-i".equals(args[i])) {
                conf.setIv(args[++i]);
            }

            if ("-k".equals(args[i])) {
                conf.setKey(args[++i]);
            }

            if ("-t".equals(args[i])) {
                txt = args[++i];
            }
        }


        if (StringUtil.isEmpty(txt)) {
            if (args.length == 1) {

            } else if (args.length == 2) {
                // 不带前缀的 最后一位参数
                txt = args[1];
            } else if (args.length / 2 != 0) {// 奇数
                if (args[1].contains("-")) {
                    // 不带前缀的 最后一位参数
                    if (StringUtil.isEmpty(txt) && args.length >= 2) {
                        txt = args[args.length - 1];
                    }
                } else {
                    // 不带前缀的 第二位参数 (第一位 -e|-d|-r)
                    if (StringUtil.isEmpty(txt) && args.length >= 2) {
                        txt = args[1];
                    }
                }
            }

        }
    }

    private static String getRandom(int len) {
        SecureRandom random = new SecureRandom();

        // 数组位长度
        byte[] saltBytes = new byte[len / 2];
        random.nextBytes(saltBytes);

        // 转 hex
        String salt = HexUtil.byte2hex(saltBytes);

        return salt;
    }


    // 读取配置文件
    private static void readConf() {
        try {
            String confPath = "";
            if (localDebugger) {
                String workPath = System.getProperty("user.dir");
                confPath = workPath + "/build" + conf.CONFIG;
            } else {
                // jar上级路径
                String path = ReadUtil.getJarParentPath();
                // jar上上级路径
                path = ReadUtil.getParentPath(path);
                confPath = path + File.separator + conf.CONFIG;

            }
            confPath = FileUtil.switchPath(confPath);

            File keyFile = new File(confPath);
            Map<String, String> prop = ReadUtil.read(keyFile, "UTF-8");
            if (prop.size() > 0) {
                initAesConf(prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化 AesConf
    private static void initAesConf(Map<String, String> config) {
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

    // map判断key是否存在并且此key对应的value是否为null
    private static boolean valueNotNull(Map map, String key) {
        return map.containsKey(key) && null != map.get(key);
    }

    private static boolean isCache() {
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
}
