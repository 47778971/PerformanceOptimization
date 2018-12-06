package com.jun.po.aspectjx;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class HttpTrack implements ITrack {
    public final String urlConnectionPath = "(java.net.URLConnection)";

    @Before("execution(* " + urlConnectionPath + ".getOutputStream(..))")
    public void getOutputStream(JoinPoint joinPoint) {
        Log.i(TAG, "getOutputStream()");
    }

    @Before("execution(* " + urlConnectionPath + ".getInputStream(..))")
    public void getInputStream(JoinPoint joinPoint) {
        Log.i(TAG, "getInputStream()");
    }

    @Before("execution(* " + urlConnectionPath + ".setRequestProperty(..))")
    public void setRequestProperty(JoinPoint joinPoint) {
        Log.i(TAG, "setRequestProperty()");
    }

    @Before("execution(* " + urlConnectionPath + ".connect(..))")
    public void connect(JoinPoint joinPoint) {
        Log.i(TAG, "connect()");
    }
}
