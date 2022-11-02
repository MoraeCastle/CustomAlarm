package com.bbi.customalarm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbi.customalarm.Adapter.AlarmListAdapter;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.System.BaseActivity;
import com.bbi.customalarm.System.MediaManager;
import com.bbi.customalarm.System.Type;
import com.bbi.customalarm.System.VerticalSpaceItemDecoration;
import com.bbi.customalarm.System.VibrationManager;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 알람 리스트(메인 화면)
 */
public class AlarmPrintActivity extends BaseActivity {
    private final String TAG = "AlarmInfoActivity";

    private LinearLayout contentLayout, closeLayout;
    private TextView dateTxt, timeTxt, contentTxt;
    private SlideToActView slideBar;

    private VibrationManager vibratorManager;
    private MediaManager mediaManager;

    private boolean isDataOk;
    private boolean isRepeat;
    /**
     * 데이터 규칙
     * 0 : 날짜
     * 1 : 시간
     * 2 : 이름
     * 3 : 알람음
     * 4 : 진동유형
     * 5 : 반복
     */
    private List<String> alarmData;

    private Intent closeTypeIntent = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "종료...");

        // 알람음 및 진동 종료.
        vibratorManager.cancel();
        mediaManager.releaseRingtone();

        // 강제 종료 시, 무조건 알람을 비활성화 한다.
        if(closeTypeIntent == null) {
            Log.d(TAG, "알람이 강제로 종료됨...");

            if(!getSystem().checkActivity(getApplicationContext(), "AlarmListActivity")) {
                closeTypeIntent = new Intent("android.intent.category.LAUNCHER");
                closeTypeIntent.setClassName("com.bbi.customalarm", "com.bbi.customalarm.AlarmListActivity");
                closeTypeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(closeTypeIntent);

                Log.d(TAG, "메인이 닫혀서 이제 열었습니다.");
            } else {
                Log.d(TAG, "메인이 열려있습니다.");
            }

            if(isRepeat) {
                closeTypeIntent = new Intent(Type.ReCallAlarm);
            } else {
                closeTypeIntent = new Intent(Type.FinishAlarm);
            }

            sendBroadcast(closeTypeIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_print);
        getUiManager().setToastView(findViewById(R.id.activity_alarm_print));
        vibratorManager = new VibrationManager(this);
        mediaManager = new MediaManager(this);

        Log.d(TAG, "시작..");
        
        alarmData = new ArrayList<>();
        if(getIntent() != null) {
            if(getIntent().hasExtra("alarmDataList")) {
                alarmData = Arrays.asList(getIntent().getStringArrayExtra("alarmDataList"));
            }
        }

        dateTxt = findViewById(R.id.alarmPrint_date);
        timeTxt = findViewById(R.id.alarmPrint_time);
        contentLayout = findViewById(R.id.alarmPrint_contentLayout);
        contentTxt = findViewById(R.id.alarmPrint_content);
        slideBar = findViewById(R.id.alarmPrint_seekbar);
        closeLayout = findViewById(R.id.alarmPrint_closeAlarmBtn);

        if(alarmData.size() != 0) {
            dateTxt.setText(alarmData.get(0));
            timeTxt.setText(alarmData.get(1));
            if(!alarmData.get(2).equals("")) {
                contentLayout.setVisibility(View.VISIBLE);
                contentTxt.setText(alarmData.get(2));
            }

            // 반복 여부.
            if(!alarmData.get(5).equals("0")) {
                slideBar.setText(alarmData.get(5) + "분 뒤에 다시 울림");
                closeLayout.setVisibility(View.VISIBLE);

                isRepeat = true;

                Log.d(TAG, "다시 울려야 합니다. aaaaa");
            }

            isDataOk = true;
        } else {
            finish();
        }

        // 알람음과 진동 울리기.
        if(isDataOk) {
            mediaManager.startRingtone(Uri.parse(alarmData.get(3)), true);

            if(!alarmData.get(4).equals("")) {
                vibratorManager.setType(VibrationManager.VibrateType.valueOf(alarmData.get(4)));
                vibratorManager.vibrate(0);
            }
        }

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();

                if (action.equals(Type.RefreshTime)) {
                    timeTxt.setText(getSystem().getCurrentTimeToString(true).split(" ")[1]);
                }
            }
        }, new IntentFilter(Type.RefreshTime));

        // 화면 깨우기.
        if(getSystemService(POWER_SERVICE) != null) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");

            //acquire will turn on the display
            wakeLock.acquire();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Log.d(TAG, "onCreate: set window flags for API level > 27");

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            Log.d(TAG, "onCreate: onCreate:set window flags for API level < 27");

            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 완료 여부.
        // 종료 및 다시울림이 될 수 있다.
        slideBar.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                mediaManager.releaseRingtone();
                vibratorManager.cancel();

                if(!getSystem().checkActivity(getApplicationContext(), "AlarmListActivity")) {
                    closeTypeIntent = new Intent("android.intent.category.LAUNCHER");
                    closeTypeIntent.setClassName("com.bbi.customalarm", "com.bbi.customalarm.AlarmListActivity");
                    closeTypeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(closeTypeIntent);
                }

                if(isRepeat) {
                    closeTypeIntent = new Intent(Type.ReCallAlarm);
                } else {
                    closeTypeIntent = new Intent(Type.FinishAlarm);
                }
                sendBroadcast(closeTypeIntent);
                finish();
            }
        });

        // 아예 끄기.
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaManager.releaseRingtone();
                vibratorManager.cancel();

                if(!getSystem().checkActivity(getApplicationContext(), "AlarmListActivity")) {
                    closeTypeIntent = new Intent("android.intent.category.LAUNCHER");
                    closeTypeIntent.setClassName("com.bbi.customalarm", "com.bbi.customalarm.AlarmListActivity");
                    closeTypeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(closeTypeIntent);
                }

                closeTypeIntent = new Intent(Type.FinishAlarm);
                sendBroadcast(closeTypeIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
