package com.yyl.server.utils;

import com.yyl.client.utils.FileUtil;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by yl on 2016/10/29.
 */
public class LogUtil {

    private static final String CONFIG = "/conf/log4j.properties";

    public static void initLog(boolean debuge) {
        try {
            String log4jConfPath = "";
            if (debuge) {
                String workPath = System.getProperty("user.dir");
                log4jConfPath = workPath + "/build" + CONFIG;
            } else {
                // jar上级路径
                String path = FileUtil.getJarParentPath();

                // jar上上级路径
                path = FileUtil.getParentPath(path);
                log4jConfPath = path + CONFIG;
            }

            log4jConfPath = FileUtil.switchPath(log4jConfPath);
            File log4jConfFile = new File(log4jConfPath);

            if ((!log4jConfFile.exists()) || (!log4jConfFile.isFile()))
                System.out.println("不存在:" + CONFIG);

            Properties prop = new Properties();
            prop.load(new FileInputStream(log4jConfFile));
            PropertyConfigurator.configure(prop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
