package com.bbi.customalarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CombinedVibration;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bbi.customalarm.Adapter.AlarmListAdapter;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.System.BaseActivity;
import com.bbi.customalarm.System.Type;
import com.bbi.customalarm.System.VerticalSpaceItemDecoration;
import com.bbi.customalarm.System.VibrationManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 알람 리스트(메인 화면)
 */
public class AlarmListActivity extends BaseActivity {
    private final String TAG = "AlarmInfoActivity";
    private ImageView settingBtn;
    private TextView alarmCount;
    private RecyclerView alarmListView;
    private Button addAlarmBtn;
    private boolean isFirstEnter = true;

    // 알람 리스트
    private AlarmListAdapter alarmListAdapter;
    private boolean isAdapterItemClick = false;
    private boolean isAddAlarmClick = false;
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
                if(alarmItems.size() != 0) {
                    if(isFirstEnter) {
                        getUiManager().printToast("알람을 불러왔습니다.");
                    } else {
                        if(alarmItemList.size() < alarmItems.size()) {
                            getUiManager().printToast("알람이 설정됐습니다.");
                            // 추가된 알람이 어느정도 흘러야 울리는지도 출력해야...
                        }
                    }
                    alarmCount.setText(alarmItems.size() + "개의 알람");
                } else {
                    alarmCount.setText("알람을 추가하세요");
                }

                alarmItemList.clear();
                //alarmListAdapter.notifyDataSetChanged();

                Log.d(TAG, "조회된 알람 갯수: " + alarmItems.size());
                for (AlarmItem item : alarmItems) {
                    alarmItemList.add(item);

                    Log.d(TAG, "[" + alarmItems.indexOf(item) + "] 번째 아이템");
                    Log.d(TAG, "- ID : " + item.getId());
                    Log.d(TAG, "- Date : " + item.getDate());
                    Log.d(TAG, "- Time : " + item.getTime());
                    Log.d(TAG, "- List : " + item.getDayOfWeek());
                    Log.d(TAG, "- Name : " + item.getName());
                    Log.d(TAG, "- Type : " + item.getType());
                    Log.d(TAG, "- Uri : " + item.getRingUri());
                    Log.d(TAG, "- VibrateType : " + item.getVibrationType());
                    Log.d(TAG, "- Repeat : " + item.getRepeat());
                    Log.d(TAG, "- Active : " + item.isActive());
                }
                alarmListAdapter.notifyDataSetChanged();
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
                /*Intent intent = new Intent(getApplicationContext(), AlarmPrintActivity.class);

                String[] data = new String[]{
                        alarmItemList.get(0).getDate(),
                        alarmItemList.get(0).getTime(),
                        alarmItemList.get(0).getName(),
                        alarmItemList.get(0).getRingUri().toString(),
                        alarmItemList.get(0).getVibrationType()
                };

                intent.putExtra(Type.AlarmData, data);*/
                startActivity(intent);
            }
        });

        // 알람 추가
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAddAlarmClick) {
                    isAddAlarmClick = true;
                    Intent intent = new Intent(getApplicationContext(), AlarmInfoActivity.class);
                    startActivity(intent);
                    isFirstEnter = false;

                    // 중복 클릭 방지.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isAddAlarmClick = false;
                        }
                    }, 1000);
                }
            }
        });

        // 아이템 클릭, 롱클릭.
        alarmListAdapter.setOnItemLongClickListener(new AlarmListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!isAdapterItemClick) {
                    isAdapterItemClick = true;
                    Intent intent = new Intent(getApplicationContext(), AlarmInfoActivity.class);
                    Log.e(TAG, " -1 아이디는 " + alarmItemList.get(position).getId());
                    intent.putExtra(Type.AlarmId, alarmItemList.get(position).getId());
                    startActivity(intent);
                    isFirstEnter = false;

                    // 중복 클릭 방지.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isAdapterItemClick = false;
                        }
                    }, 1000);
                }

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

        alarmListAdapter.setOnCheckedChangeListener(new AlarmListAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(int position, boolean isActive) {
                Log.d(TAG, "으악 " + isActive);

                if(isActive || !alarmItemList.get(position).getType().equals("Date")) {
                    alarmListAdapter.switchCompatMap.get(position).setChecked(!isActive);
                    alarmItemList.get(position).setActive(!isActive);
                    new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                } else {
                    // 시간 검증.
                    if(getSystem().travelDateCheck(alarmItemList.get(position).getDate(), alarmItemList.get(position).getTime())) {
                        alarmListAdapter.switchCompatMap.get(position).setChecked(!isActive);
                        alarmItemList.get(position).setActive(!isActive);
                        new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                    } else {
                        getUiManager().printToast("알람 시간이 지났습니다. 다시 설정하세요.");
                    }
                }
            }
        });

    }
}
