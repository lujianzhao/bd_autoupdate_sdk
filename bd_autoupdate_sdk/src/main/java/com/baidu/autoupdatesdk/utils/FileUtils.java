//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.utils;

import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileUtils {
    private String sdpath = Environment.getExternalStorageDirectory() + "/";
    private int filesize = 4096;

    public String getSDPATH() {
        return this.sdpath;
    }

    public FileUtils() {
    }

    public File createSDFile(String fileName) throws IOException {
        File file = new File(this.sdpath + fileName);
        file.createNewFile();
        return file;
    }

    public File createSDDir(String dirName) {
        File dir = new File(this.sdpath + dirName);
        dir.mkdir();
        return dir;
    }

    public boolean isFileExist(String fileName) {
        File file = new File(this.sdpath + fileName);
        return file.exists();
    }

    public File writeToSDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        FileOutputStream output = null;

        try {
            this.createSDDir(path);
            file = this.createSDFile(path + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[this.filesize];

            int length;
            while((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
        } catch (Exception var16) {
//            LogUtils.printE(var16.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException var15) {
//                LogUtils.printE(var15.getMessage());
            }

        }

        return file;
    }

    public static String getFileMD5(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return null;
        } else {
            MessageDigest digest = null;
            FileInputStream in = null;
            byte[] buffer = new byte[1024];

            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);

                while(true) {
                    int len;
                    if ((len = in.read(buffer, 0, 1024)) == -1) {
                        in.close();
                        break;
                    }

                    digest.update(buffer, 0, len);
                }
            } catch (Exception var9) {
//                LogUtils.printE(var9.getMessage());
                return null;
            }

            BigInteger bigInt = new BigInteger(1, digest.digest());
            String md5 = bigInt.toString(16);
            int delta = 16 - md5.length();
            if (delta > 0) {
                md5 = "00000000000".substring(0, delta) + md5;
            }

            return md5;
        }
    }

    public static String renameFileExpName(File file, String expName) {
        String filePath = file.getParent();
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + expName;
        fileName = fileName.replaceAll(" ", "");
        File newFile = new File(filePath + "/" + fileName);
        if (!newFile.exists() && file.exists()) {
            file.renameTo(newFile);
            return newFile.getAbsolutePath();
        } else {
            return file.getAbsolutePath();
        }
    }

    public static String getFileNameFromUrl(String urlStr) {
        String fileName = "";
        if (!TextUtils.isEmpty(urlStr)) {
            fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
        }

        return fileName;
    }
}
