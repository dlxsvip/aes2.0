package com.yyl.client.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * <br>
 * Created byyyll on 2016/10/19.
 */
public class FileUtil {


    /**
     * 获取 默认路径
     * xxx/xxx/xxx/classes/dir/default/
     *
     * @return 路径
     */
    public static String getDefaultPath() {
        String path = FileUtil.class.getResource("/").getPath();
        //return path.substring(0, path.indexOf("WEB-INF/classes")) + "dir/default/";
        return path.substring(0, path.indexOf("/classes")) + "dir/default/";
    }

    /**
     * 获取clazz类路径 项目路径
     *
     * @return clazz路径
     */
    public static String getClassPath(Class clazz) {
        String classPath = clazz.getResource("").getPath();
        classPath = classPath.substring(1);

        return classPath;
    }


    /**
     * 获取类根路径
     *
     * @return class目录路径
     */
    public static String getClassRootPath() {
        //FileUtil类所在的路径
        //String classPath = FileUtil.class.getResource("").getPath();

        String classPath = FileUtil.class.getClassLoader().getResource("").getPath();
        if (null == classPath) {
            classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        }
        classPath = classPath.substring(1);

        return classPath;
    }


    /**
     * excel 目录
     *
     * @return 路径
     */
    public static String getExcelPath() {
        return getClassRootPath() + "excel/";
    }

    /**
     * excel 目录
     *
     * @return excel/suPath/
     */
    public static String getExcelPath(String suPath) {
        return getExcelPath() + suPath + "/";
    }

    /**
     * 获取文件URL
     *
     * @param file 文件
     * @return 路径
     */
    public static URL getURL(String file) {
        return FileUtil.class.getClassLoader().getResource(file);
    }

    /**
     * 获取文件路径
     *
     * @param file 文件
     * @return 路径
     */
    public static String getFilePath(String file) {
        if (0 == file.indexOf("/")) {
            return FileUtil.class.getResource(file).getPath().substring(1);
        } else {
            return getURL(file).getPath().substring(1);
        }
    }

    /**
     * 获取文件路径
     *
     * @param file 文件
     * @return 路径
     */
    public static String getFilePath2(String file) {
        if (0 == file.indexOf("/")) {
            return FileUtil.class.getResource(file).getPath().substring(1);
        } else {
            return getURL(file).getFile().substring(1);
        }
    }


    /**
     * 获取父级路径
     *
     * @return 父级路径
     */
    public static String getParentPath(String path) {
        return new File(path).getParent();
    }


    /**
     * 获取 jar包路径
     *
     * @return jar包的路径
     */
    public static String getJarPath() {
        String jarFilePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            return URLDecoder.decode(jarFilePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return jarFilePath;
    }

    /**
     * 获取 jar包上级路径
     *
     * @return jar包的路径
     */
    public static String getJarParentPath() {
        // jar包路径
        String jarFilePath = getJarPath();

        return getParentPath(jarFilePath);
    }

    /**
     * 文件名称获取
     *
     * @param file 文件全路径
     * @return 文件名
     */
    public static String getFileName(String file) {
        String fileName = file;
        try {
            if (null != file && file.length() > 0) {
                if (file.contains(File.separator)) {
                    fileName = file.substring(file.lastIndexOf(File.separator) + 1);
                } else if (file.contains("/")) {
                    fileName = file.substring(file.lastIndexOf("/") + 1);
                } else if (file.contains("\\")) {
                    fileName = file.substring(file.lastIndexOf("\\") + 1);
                }
            }

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    // 获取文件后缀名
    private static String postfix(String file) {
        String suffix = "";
        if (null != file && file.length() > 0 && file.contains(".")) {
            // 文件后缀名
            suffix = file.substring(file.lastIndexOf("."));
        }

        return suffix;
    }

    /**
     * 创建目录结构
     *
     * @param dirPath
     */
    public static void mkDir(String dirPath) {
        File fileDir = new File(dirPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
    }


    public static String switchPath(String path) {
        if (path.contains("\\\\") || path.contains("\\")) {
            path = path.replaceAll("\\\\", "/");
        }

        return path;
    }

}
