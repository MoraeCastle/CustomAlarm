package com.bbi.customalarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    private final String TAG = "AlarmInfoActivity";
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

                Log.d(TAG, "조회된 알람 갯수: " + alarmItems.size());
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

        alarmListAdapter.setOnItemLongClickListener(new AlarmListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                //다이얼로그에 리스트 담기
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmListActivity.this);
                builder.setTitle("알람 삭제");
                builder.setMessage("알람을 삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DeleteAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                        getUiManager().printToast("삭제되었습니다.");
                    }
                });
                builder.setNegativeButton("아니오", null);
                builder.show();
            }
        });
    }
}
