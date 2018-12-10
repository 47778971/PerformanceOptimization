package com.jun.po.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class LogUtil {

    public static void getStackTraceInfo() {
        stackInfoAppend2File(Log.getStackTraceString(new Throwable()));
    }

    private static File makeDir() {
        String dcLog = "/dclog";
        File esd = Environment.getExternalStorageDirectory();
        File dir = new File(esd, dcLog);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    public static void contactsInfo2File(String contactsInfo) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File dir = makeDir();
            File file = new File(dir, "uuid" + UUID.randomUUID().toString() + ".txt");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(contactsInfo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != writer) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void stackInfo2File(String stackInfo) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File dir = makeDir();
            File file = new File(dir, "stackInfo.txt");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(stackInfo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != writer) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void stackInfoAppend2File(String stackInfo) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File dir = makeDir();
            File file = new File(dir, "stackInfo.txt");
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(file.length());
                randomAccessFile.writeBytes(stackInfo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != randomAccessFile) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean createDumpFile() {
        File dir = makeDir();
        boolean bool = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ssss");
        String createTime = sdf.format(new Date(System.currentTimeMillis()));
        String state = android.os.Environment.getExternalStorageState();
        // 判断SdCard是否存在并且是可用的
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
            String hprofPath = dir.getAbsolutePath();
            if (!hprofPath.endsWith("/")) {
                hprofPath += "/";
            }
            hprofPath += createTime + ".hprof";
            try {
                android.os.Debug.dumpHprofData(hprofPath);
                bool = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bool = false;
        }
        return bool;
    }
}