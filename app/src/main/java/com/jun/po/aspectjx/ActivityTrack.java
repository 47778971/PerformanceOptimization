package com.jun.po.aspectjx;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class ActivityTrack implements ITrack {
    public static final String pagePkg = "(android.app.Activity)";

    @Before("execution(* " + pagePkg + ".onCreate(..))")
    public void onCreate(JoinPoint joinPoint) {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        if (methodSignature.getDeclaringTypeName().contains("com.jun.po")) {
//            Log.i(TAG, "切入Activity的onCreate()");
//        }
    }

    @After("execution(* " + pagePkg + ".onStart(..))")
    public void onStart(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onRestart(..))")
    public void onRestart(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onAttachedToWindow(..))")
    public void onAttachedToWindow(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onDetachedFromWindow(..))")
    public void onDetachedFromWindow(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onNewIntent(..))")
    public void onNewIntent(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onResume(..))")
    public void onResume(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onDestroy(..))")
    public void onDestroy(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onPause(..))")
    public void onPause(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onStop(..))")
    public void onStop(JoinPoint joinPoint) {
    }

    @After("execution(* " + pagePkg + ".onActivityResult(..))")
    public void onActivityResult(JoinPoint joinPoint) {
    }
}