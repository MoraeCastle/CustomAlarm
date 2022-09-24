package com.bbi.customalarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_print);
        getUiManager().setToastView(findViewById(R.id.activity_alarm_print));
        vibratorManager = new VibrationManager(this);
        mediaManager = new MediaManager(this);

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

                Intent intent;
                if(isRepeat) {
                    intent = new Intent(Type.ReCallAlarm);
                } else {
                    intent = new Intent(Type.FinishAlarm);
                }
                sendBroadcast(intent);
                finish();
            }
        });

        // 아예 끄기.
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaManager.releaseRingtone();
                vibratorManager.cancel();

                Intent intent = new Intent(Type.FinishAlarm);
                sendBroadcast(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
