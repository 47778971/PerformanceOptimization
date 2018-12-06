package com.jun.po.aspectjx;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class GlideTrack implements ITrack {
    public final String HttpUrlFetcherPath = "(com.bumptech.glide.load.data.HttpUrlFetcher)";
    public final String simpleTargetPath = "(com.bumptech.glide.request.target.SimpleTarget)";

    @Before("execution(* " + simpleTargetPath + ".onResourceReady(..))")
    public void onResourceReady(JoinPoint joinPoint) {
        Log.i(TAG, "onResourceReady()");
    }

    @Before("execution(* " + HttpUrlFetcherPath + ".loadData(..))")
    public void loadData(JoinPoint joinPoint) {
        Log.i(TAG, "loadData()");
    }

    @Before("execution(* " + HttpUrlFetcherPath + ".loadDataWithRedirects(..))")
    public void loadDataWithRedirects(JoinPoint joinPoint) {
        Log.i(TAG, "loadDataWithRedirects()");
    }
}
