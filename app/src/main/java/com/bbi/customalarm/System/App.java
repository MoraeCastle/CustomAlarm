package com.bbi.customalarm.System;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.bbi.customalarm.Service.AlarmService;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("AlarmInfoActivity", "Service is Active");
        startService(new Intent(this, AlarmService.class));
    }
}