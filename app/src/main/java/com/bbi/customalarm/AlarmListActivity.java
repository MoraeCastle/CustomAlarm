package com.bbi.customalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bbi.customalarm.System.BaseActivity;

public class AlarmListActivity extends BaseActivity {
    private ImageView settingBtn;
    private TextView alarmCount;
    private ListView alarmListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        settingBtn = findViewById(R.id.alarmList_settingBtn);
        alarmCount = findViewById(R.id.alarmList_alarmCount);
        alarmListView = findViewById(R.id.alarmList_listView);

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
    }
}
