package com.example.danie.newsapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

@SuppressLint("Registered")
public class NewsApp extends Application{
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Resources getResourcesStatic() {
        return mContext.getResources();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
