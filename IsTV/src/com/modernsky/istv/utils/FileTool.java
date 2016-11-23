package com.modernsky.istv.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.text.TextUtils;

public class FileTool {

    // 写入敏感词，该文件会被保存在/data/data/应用名称/files/check.txt
    public static void writeSensetive(Context context, String fileContent) {
        byte[] data = fileContent.getBytes();
        // getFolder(context);
        File file = new File(context.getApplicationContext().getFilesDir(), "check.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getFolder(Context context) {
        File folder = new File(context.getFilesDir(), "check.txt");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.isDirectory()) {
            folder.delete();
            folder.mkdirs();
        }
        try {
            Runtime.getRuntime().exec("chmod 777 " + folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读敏感词
    public static String readSensetive(Context context) {
        return FileTool.readSDCard("check.txt", context.getApplicationContext().getFilesDir().toString());
    }

    // 读文件
    public static String readSDCard(String fileName, String folderPath) {
        StringBuilder text = new StringBuilder();
        File file = new File(folderPath, fileName);
        try {
            File path = new File(folderPath);
            if (!path.exists()) {
                path.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return TextUtils.isEmpty(text) ? "[]" : text.toString();
    }
}
