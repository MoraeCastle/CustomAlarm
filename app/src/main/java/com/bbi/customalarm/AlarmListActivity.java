package com.bbi.customalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbi.customalarm.Adapter.AlarmListAdapter;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.System.BaseActivity;

import java.util.ArrayList;

/**
 * 알람 리스트
 */
public class AlarmListActivity extends BaseActivity {
    private ImageView settingBtn;
    private TextView alarmCount;
    private RecyclerView alarmListView;
    private Button addAlarmBtn;

    // 알람 리스트
    private AlarmListAdapter alarmListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        settingBtn = findViewById(R.id.alarmList_settingBtn);
        alarmCount = findViewById(R.id.alarmList_alarmCount);
        alarmListView = findViewById(R.id.alarmList_recycleView);
        addAlarmBtn = findViewById(R.id.alarmList_addAlarmBtn);

        alarmListView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        alarmListAdapter = new AlarmListAdapter(new ArrayList<>());
        alarmListView.setAdapter(alarmListAdapter);

        getUiManager().setToastView(findViewById(R.id.activity_alarm_list));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 설정
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        // 알람 추가
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
