package com.bbi.customalarm.Service;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.bbi.customalarm.AlarmListActivity;
import com.bbi.customalarm.R;
import com.bbi.customalarm.System.Type;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service {
    // 타이머
    private Timer timerCall;

    public AlarmService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent == null){
            return START_STICKY;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel =
                    new NotificationChannel(
                            Type.AlarmService, "알람을 울리기 위해 대기중입니다.", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }

        startForeground();

        //checkAlarm();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent();

        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);*/

        startForeground(1, new NotificationCompat.Builder(this,
                Type.AlarmService) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("알람을 울리기 위해 대기중입니다.")
                .setContentIntent(null)
                .build());
    }

    /**
     * 원래 여기서 타이머로 일정초간 메인 액티비티(알람리스트)로 신호를 보내주는게 좋지만.
     * 이 서비스 자체를 Application 클래스 내에 선언해서 앱 실행과 동시에 서비스가 실행된다.
     */
    private void checkAlarm() {
        timerCall = new Timer();
        timerCall.schedule(new TimerTask() {
            @Override
            public void run() {
                /*Intent intent = new Intent(Type.CheckAlarm);
                sendBroadcast(intent);*/

                /*if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.CREATED) {
                    Log.d("AlarmInfoActivity", "백그라운드");

                    Intent intent = new Intent("android.intent.category.LAUNCHER");
                    intent.setClassName("com.bbi.customalarm", "com.bbi.customalarm.AlarmListActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }*/
                Log.d("AlarmInfoActivity", "DFDFDFFDF");
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

                for (ActivityManager.RunningTaskInfo task : tasks) {
                    Log.d("AlarmInfoActivity", task.baseActivity.getClassName());
                }
                /*ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

                boolean isAlarmListActive = false;
                for (ActivityManager.RunningTaskInfo task : tasks) {
                    Log.d("AlarmInfoActivity", task.baseActivity.getClassName());
                    if (getApplicationContext().getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName())) {
                        Log.d("AlarmInfoActivity", "YES!!!");
                    }

                    if(task.baseActivity.getClassName().equals("com.bbi.customalarm.AlarmListActivity")) {
                        isAlarmListActive = true;
                    }
                }

                Log.d("AlarmInfoActivity", String.valueOf(isAlarmListActive));
                if(!isAlarmListActive) {
                    *//*Intent myIntent = new Intent();
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(myIntent);*//*
                }*/
            }
        }, 0, 3000);

        /*if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.CREATED) {
            Log.d("AlarmInfoActivity", "백그라운드");

            Intent intent = new Intent("android.intent.category.LAUNCHER");
            intent.setClassName("com.your.package", "com.your.package.AlarmListActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Log.d("AlarmInfoActivity", "포그라운드");
        }*/
    }
}