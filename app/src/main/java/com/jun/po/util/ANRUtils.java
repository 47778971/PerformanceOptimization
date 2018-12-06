package com.jun.po.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.FileObserver;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.bugly.proguard.x;

import java.util.Iterator;
import java.util.List;

public class ANRUtils {

    private static String getAnrInfo(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.ProcessErrorStateInfo> processErrorStateInfos = activityManager.getProcessesInErrorState();
        ActivityManager.ProcessErrorStateInfo pesi = null;

        if (processErrorStateInfos != null) {
            for (int i = 0; i < processErrorStateInfos.size(); i++) {
                ActivityManager.ProcessErrorStateInfo p;
                if ((p = processErrorStateInfos.get(i)) != null && p.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {
                    pesi = p;
                    break;
                }
            }
        }
        StringBuilder anrInfo = new StringBuilder();
        if (pesi != null && pesi.pid == Process.myPid()) {
            anrInfo.append("process id:").append(pesi.pid).append("\n").append("message:").append(pesi.shortMsg).append("\n").append(pesi.longMsg).append("\n").append("stackTrace:").append(pesi.stackTrace).append("\n");
        }
        return anrInfo.toString();
    }

    public static void catchAnr(final Context context) {
        FileObserver fileObserver = new FileObserver("/data/anr/", FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                if (path != null) {
                    String tracePath;
                    if (!(tracePath = "/data/anr/" + path).contains("trace")) {
                        Log.d("not anr file %s", tracePath);
                    } else {
                        Log.i("ANRUtils", getAnrInfo(context));
                    }
                }
            }
        };
        fileObserver.startWatching();
    }

}
