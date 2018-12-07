package com.jun.po;

import android.app.Application;

import com.jun.po.util.CrashHandler;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), true);
    }
}
