package com.bbi.customalarm.Service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.bbi.customalarm.AlarmListActivity;
import com.bbi.customalarm.AlarmPrintActivity;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.R;
import com.bbi.customalarm.Room.AlarmDao;
import com.bbi.customalarm.Room.AlarmDatabase;
import com.bbi.customalarm.System.BaseActivity;
import com.bbi.customalarm.System.SystemManager;
import com.bbi.customalarm.System.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service {
    private final String TAG = "AlarmInfoActivity";

    // 알람 데이터
    static List<AlarmItem> alarmList = new ArrayList<>();
    static boolean isAlarmCalling = false;
    Intent reFreshIntent = new Intent(Type.RefreshTime);
    private AlarmItem callingAlarm;

    // 타이머
    private Timer timerCall;
    public static List<Activity> activityList;

    public static AlarmListActivity alarmListActivity;
    public static AlarmPrintActivity alarmPrintActivity;
    private SystemManager systemManager = new SystemManager();
    private AlarmDatabase alarmDatabase;
    private BroadcastReceiver broadcastReceiver;

    public AlarmService() {
        activityList = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
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

        alarmDatabase = AlarmDatabase.getAppDatabase(
                getApplicationContext()
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel =
                    new NotificationChannel(
                            Type.AlarmService, "알람을 울리기 위해 대기중입니다.", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }

        startForeground();

        checkAlarm();

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
     * 알람
     */
    private void checkAlarm() {
        // 타 액티비티에서 요청...
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();

                if (action.equals(Type.FinishAlarm)) {
                    Log.d(TAG, "서비스: broadcastReceiver > FinishAlarm");

                    if(callingAlarm != null) {
                        callingAlarm.setReCallDate("");
                        setDisableAlarmItem(callingAlarm);
                        callingAlarm = null;
                    }

                    isAlarmCalling = false;
                } else if (action.equals(Type.ReCallAlarm)) {
                    Log.d(TAG, "서비스: broadcastReceiver > ReCallAlarm");

                    if(alarmListActivity != null) {
                        alarmListActivity.getUiManager().printToast(callingAlarm.getRepeat() + "분 후에 다시 울립니다.");
                    }

                    String[] currentTime = systemManager.getCurrentTimeToString(true).split(" ");
                    String reCallString = systemManager.addAlarmMinute(
                            currentTime[0] + " " + currentTime[1], callingAlarm.getRepeat());
                    callingAlarm.setReCallDate(reCallString);

                    // 테스트
                    callingAlarm.setTime(reCallString.split(" ")[1]);

                    setAlarmData(callingAlarm);
                    isAlarmCalling = false;
                } else if(action.equals(Type.CheckAlarm)) {
                    Log.d(TAG, "서비스: broadcastReceiver > CheckAlarm");

                    // 알람이 울리지 않을 떄만.
                    if(!isAlarmCalling) {
                        // 알람 타이머 작동.
                        checkAlarm();
                    } else {
                        // 알람출력 창 값 최신화.
                        sendBroadcast(new Intent(Type.RefreshTime));
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Type.FinishAlarm));
        registerReceiver(broadcastReceiver, new IntentFilter(Type.ReCallAlarm));
        registerReceiver(broadcastReceiver, new IntentFilter(Type.CheckAlarm));

        timerCall = new Timer();
        timerCall.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isAlarmCalling) {
                    Log.d(TAG, "서비스: 알람 울리는중...");

                    // 알람출력 씬의 알람시간 최신화.
                    sendBroadcast(reFreshIntent);
                } else {
                    Log.d(TAG, "서비스: 알람 탐색...");
                    alarmCheck();
                }
            }
        }, 0, 3000);
    }

    /**
     * 알람 데이터 설정
     */
    public static void setAlarmList(List item) {
        alarmList = item;
    }

    /**
     * 알람 출력상태 설정
     */
    public static void setAlarmCalling(boolean isCalling) {
        isAlarmCalling = isCalling;
    }

    /**
     * 액티비티 추가 시, 기존 액티비티는 제거된다.
     */
    public static void addActivity(Activity activity) {
        Log.d("System" , "addActivity");
        for (Activity data : activityList) {
            Log.d("System" , "조회된 클래스 :: " + data.getLocalClassName());
            if(data.getLocalClassName().equals(activity.getLocalClassName())) {
                Log.d("System" , "끕니다...");
                data.finish();
                activityList.remove(data);
            }
        }

        activityList.add(activity);
    }

    /**
     * 알람 체크.
     * 울려야 할 알람을 확인하고, 울립니다.
     * 울리는 알람타입 우선순위는 날짜 > 요일 입니다.
     * 시간이 겹치는 경우 우선순위는 기존 > 나중에 울릴알람 입니다.
     */
    private void alarmCheck() {
        Log.d(TAG, "서비스: alarmCheck");
        if(alarmList.size() != 0) {
            List<AlarmItem> callingAlarmList = new ArrayList<>();

            for (AlarmItem dataItem : alarmList) {
                // 현재시간과 알람시간이 같은 알람만 저장.
                // 활성화 된 알람만.
                if(dataItem.isActive()) {
                    // 다시 울려야 할 알람인지?
                    if(!dataItem.getReCallDate().equals("")) {
                        if(systemManager.alarmDateCheck(systemManager.setReCallDate(dataItem.getReCallDate()))) {
                            callingAlarmList.add(dataItem);
                        }
                    } else {
                        // 요일인지 날짜인지?
                        // 삽입 우선순위는 날짜이다.
                        // 날짜알람은 무조건 앞에 쌓인다.
                        if(dataItem.getType().equals(Type.DayOfTheWeek)) {
                            if(systemManager.alarmDateCheckToWeek(
                                    dataItem.getDayOfWeek().toString(), dataItem.getDate(), dataItem.getTime())) {
                                callingAlarmList.add(dataItem);
                            }
                        } else {
                            if(systemManager.alarmDateCheck(dataItem.getDate(), dataItem.getTime())) {
                                callingAlarmList.add(0, dataItem);
                            }
                        }
                    }
                }
            }

            // 울릴 알람이 있는지?
            if(callingAlarmList.size() > 0) {
                // 현재 앱이 꺼져있으면 다시 깨웁니다.
                /*if(!getSystem().isAppActive()) {
                    Intent intent = new Intent("android.intent.category.LAUNCHER");
                    intent.setClassName("com.bbi.customalarm", "com.bbi.customalarm.AlarmListActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }*/

                callingAlarm = callingAlarmList.get(0);
                String printDate, printTime;
                if(callingAlarm.getReCallDate().equals("")) {
                    if(callingAlarm.getType().equals(Type.DayOfTheWeek)) {
                        printDate = systemManager.getCurrentTimeToString(false);
                    } else {
                        printDate = callingAlarm.getDate();
                    }
                    printTime = callingAlarm.getTime();
                } else {
                    printDate = callingAlarm.getReCallDate().split(" ")[0];
                    printTime = callingAlarm.getReCallDate().split(" ")[1];
                }

                String[] data = new String[]{
                        printDate,
                        printTime,
                        callingAlarm.getName(),
                        callingAlarm.getRingUri().toString(),
                        callingAlarm.getVibrationType(),
                        String.valueOf(callingAlarm.getRepeat())
                };

                // 첫 번째 알람을 울립니다.
                Intent intent = new Intent(getApplicationContext(), AlarmPrintActivity.class);
                intent.putExtra(Type.AlarmData, data);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 플래그 설정
                startActivity(intent);

                isAlarmCalling = true;

                // 첫 번째 알람 이외는 비활성 처리합니다.
                // 나중에 상단에 부재중 알람을 표시해야 합니다.
                for (AlarmItem item : callingAlarmList) {
                    if(callingAlarmList.indexOf(item) != 0) {
                        setDisableAlarmItem(item);
                    }
                }
            } else {
                Log.d(TAG, "타이머: 울릴 알람이 없습니다.");
            }
        } else {
            Log.d(TAG, "타이머: 알람리스트가 비었음...");
        }
    }

    /**
     * 알람아이템 업데이트.
     * 유효한 아이템이여야 실행됩니다.
     */
    private void setAlarmData(AlarmItem item) {
        for (AlarmItem data : alarmList) {
            if(item.getId() == data.getId()) {
                new AlarmService.UpdateAsyncTask(alarmDatabase.alarmDao()).execute(item);
                return;
            }
        }
    }

    /**
     * 알람 아이템의 상태를 비활성화 시킵니다.
     * 요일알람의 경우 갱신상태를 최신화 시킵니다.
     */
    private void setDisableAlarmItem(AlarmItem item) {
        for (AlarmItem data : alarmList) {
            if(item.getId() == data.getId()) {
                data.setReCallDate("");

                // 요일의 경우 울린날짜를 저장합니다.(갱신여부)
                if(data.getType().equals(Type.DayOfTheWeek)) {
                    data.setDate(systemManager.getCurrentTimeToString(false));
                    new UpdateAsyncTask(alarmDatabase.alarmDao()).execute(data);
                } else {
                    // 날짜알람인 경우 그냥 비활성화 시킵니다.(활성화 시 날짜체크함)
                    data.setActive(false);
                    new UpdateAsyncTask(alarmDatabase.alarmDao()).execute(data);
                }

                return;
            }
        }

        Log.d(TAG, "알람 비활성화: 활성화 될 아이템 찾지 못함.");
    }

    /**
     * DB
     */
    // 데이터베이스 접근
    // 백그라운드작업(메인스레드 X)
    // 추가
    public static class InsertAsyncTask extends AsyncTask<AlarmItem, Void, Void> {
        private AlarmDao alarmDao;

        public InsertAsyncTask(AlarmDao memoDao){
            this.alarmDao = memoDao;
        }

        @Override
        protected Void doInBackground(AlarmItem... memoItems) {
            alarmDao.insert(memoItems[0]);
            return null;
        }
    }

    // 삭제
    public static class DeleteAsyncTask extends AsyncTask<AlarmItem, Void, Void> {
        private AlarmDao alarmDao;

        public DeleteAsyncTask(AlarmDao memoDao){
            this.alarmDao = memoDao;
        }

        @Override
        protected Void doInBackground(AlarmItem... memoItems) {
            alarmDao.delete(memoItems[0]);
            return null;
        }
    }

    // 모두 삭제
    public static class DeleteAllAsyncTask extends AsyncTask<AlarmItem, Void, Void> {
        private AlarmDao alarmDao;

        public DeleteAllAsyncTask(AlarmDao memoDao){
            this.alarmDao = memoDao;
        }

        @Override
        protected Void doInBackground(AlarmItem... memoItems) {
            alarmDao.deleteAll();
            return null;
        }
    }

    // 업데이트
    public static class UpdateAsyncTask extends AsyncTask<AlarmItem, Void, Void> {
        private AlarmDao alarmDao;

        public UpdateAsyncTask(AlarmDao memoDao){
            this.alarmDao = memoDao;
        }

        @Override
        protected Void doInBackground(AlarmItem... memoItems) {
            alarmDao.update(memoItems[0]);
            return null;
        }
    }
}