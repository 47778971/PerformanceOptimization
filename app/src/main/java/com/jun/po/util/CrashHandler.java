package com.jun.po.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Process;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.jun.po.CrashCollectActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final String TAG = "CrashHandler";

    private static CrashHandler INSTANCE;

    private Thread.UncaughtExceptionHandler crashHandler = null;

    private Context context;

    private FileObserver fileObserver = null;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        synchronized (CrashHandler.class) {
            if (INSTANCE == null) {
                INSTANCE = new CrashHandler();
            }
            return INSTANCE;
        }
    }

    public void init(Context context, boolean observeANR) {
        this.context = context;
        crashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        if (observeANR) observeANR();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        try {
            String page = getPage(t.getStackTrace());
            handleException(t);
            crashHandler.uncaughtException(thread, t);
            t.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPage(StackTraceElement[] elements) {
        String page = "";
        for (StackTraceElement element : elements) {
            if (element.getClassName().contains("com.jun.po")) {
                page = element.getFileName();
                break;
            }
        }
        return page;
    }

    private boolean handleException(Throwable t) {
        if (null == t) return false;
        Intent intent = new Intent(context, MyIntentService.class);
        context.startService(intent);
//        saveCrashInfo(t);
        new Thread() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(context, CrashCollectActivity.class);
                    PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent);
                    Process.killProcess(android.os.Process.myPid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return true;
    }

    private File makeDir() throws Exception {
        String dcLog = "/po";
        File esd = Environment.getExternalStorageDirectory();
        File dir = new File(esd, dcLog);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    private void saveCrashInfo(Throwable t) {
        FileOutputStream os = null;
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        try {
            File dir = makeDir();
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            t.printStackTrace(printWriter);
            Throwable cause = t.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            String time = format.format(new Date());
            String fileName = "crash-" + time + ".txt";
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(sb.toString().getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void observeANR() {
        if (null != fileObserver) return;
        fileObserver = new FileObserver("/data/anr/", FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                if (!TextUtils.isEmpty(path) && path.contains("trace")) {
                    filterANRInfo();
                }
            }
        };
        try {
            fileObserver.startWatching();
        } catch (Exception e) {
            fileObserver = null;
        }
    }

    private synchronized void filterANRInfo() {
        Log.i(TAG, "filterANRInfo：" + Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.ProcessErrorStateInfo> stateInfoList = activityManager.getProcessesInErrorState();
        ActivityManager.ProcessErrorStateInfo stateInfo = null;
        if (stateInfoList != null) {
            Log.i(TAG, "stateInfoList != null");
            Iterator iterator = stateInfoList.iterator();
            while (iterator.hasNext()) {
                ActivityManager.ProcessErrorStateInfo si;
                if ((si = (ActivityManager.ProcessErrorStateInfo) iterator.next()).condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {
                    stateInfo = si;
                    break;
                }
            }
        }
        StringBuilder anrInfo = new StringBuilder();
        Log.i(TAG, "stateInfo.pid：" + (stateInfo.pid == Process.myPid()));
        if (stateInfo != null && stateInfo.pid == Process.myPid()) {
            anrInfo.append("process name:").append(stateInfo.processName).append("\n").append("message:").append(stateInfo.shortMsg).append("\n").append(stateInfo.longMsg).append("\n");
            if (!TextUtils.isEmpty(anrInfo.toString())) {
                saveANRInfo(anrInfo.toString());
            }
        }
    }

    private void saveANRInfo(String anrInfo) {
        FileOutputStream os = null;
        try {
            File dir = makeDir();
            long timestamp = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            String time = format.format(new Date(timestamp));
            File file = new File(dir, "anr-" + time + ".txt");
            os = new FileOutputStream(file, true);
            os.write(anrInfo.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}