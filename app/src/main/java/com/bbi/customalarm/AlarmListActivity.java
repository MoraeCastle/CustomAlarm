package com.bbi.customalarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bbi.customalarm.Adapter.AlarmListAdapter;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.System.BaseActivity;
import com.bbi.customalarm.System.VerticalSpaceItemDecoration;
import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<AlarmItem> alarmItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        settingBtn = findViewById(R.id.alarmList_settingBtn);
        alarmCount = findViewById(R.id.alarmList_alarmCount);
        alarmListView = findViewById(R.id.alarmList_recycleView);
        addAlarmBtn = findViewById(R.id.alarmList_addAlarmBtn);

        alarmItemList = new ArrayList<>();

        alarmListView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        alarmListView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        alarmListAdapter = new AlarmListAdapter(this, alarmItemList);
        alarmListView.setAdapter(alarmListAdapter);

        getUiManager().setToastView(findViewById(R.id.activity_alarm_list));

        getAlarmDatabase().alarmDao().getAll().observe(this, new Observer<List<AlarmItem>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<AlarmItem> alarmItems) {
                alarmItemList.clear();
                alarmListAdapter.notifyDataSetChanged();

                for (AlarmItem alarmItem : alarmItems) {
                    alarmItemList.add(alarmItem);
                    alarmListAdapter.notifyDataSetChanged();
                }
            }
        });
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
