package com.modernsky.istv.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 本地缓存处理工具类
 *
 * @author fengqingyun2008
 */
public class LocalCacheUtil {
    private static String ROOT_NAME = "MordernSky";
    /**
     * 根目录
     **/
    public static File rootFilePath;
    /**
     * 语音存储
     **/
    public static File voiceFilePath;
    /**
     * 图片储存
     **/
    public static File pictureFilePath;
    /**
     * 用户头像
     **/
    public static File userIconFilePath;
    /**
     * 软件下载
     **/
    public static File apkDownLoadFilePath;
    /**
     * 本地临时储存
     **/
    public static File cacheFilePath;

    static {
        File rootFile = new File(Environment.getExternalStorageDirectory(),
                ROOT_NAME);
        if (!rootFile.exists()) {
            rootFile.mkdir();
        }
        rootFilePath = rootFile;
        initLocalCacheDir();
    }

    /**
     * 初始化本地缓存目录
     *
     * @param rootFileName 根目录文件名
     */
    public static void initLocalCacheDir() {
        // 根目录创建
        File rootFile = new File(Environment.getExternalStorageDirectory(),
                ROOT_NAME);

        if (!rootFile.exists()) {
            rootFile.mkdir();
        }
        LogUtils.t("initLocalCacheDir()", rootFile.getAbsoluteFile()
                .toString());
        rootFilePath = rootFile;
        // 创建缓存子目录
        File voice = new File(rootFile, "voice");
        voice.mkdirs();
        voiceFilePath = voice;
        File picture = new File(rootFile, "picture");
        picture.mkdirs();
        pictureFilePath = picture;
        File images = new File(rootFile, "userIcon");
        images.mkdirs();
        userIconFilePath = images;
        File downloaded = new File(rootFile, "downloaded");
        downloaded.mkdirs();
        apkDownLoadFilePath = downloaded;

        File cahceFile = new File(rootFile, "cache");
        cahceFile.mkdirs();
        cacheFilePath = cahceFile;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

}
