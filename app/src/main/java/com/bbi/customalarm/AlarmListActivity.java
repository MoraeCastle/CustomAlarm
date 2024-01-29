package com.bbi.customalarm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bbi.customalarm.Adapter.AlarmListAdapter;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.Room.AlarmDao;
import com.bbi.customalarm.Service.AlarmService;
import com.bbi.customalarm.System.BaseActivity;
import com.bbi.customalarm.System.Type;
import com.bbi.customalarm.System.VerticalSpaceItemDecoration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private ArrayList<AlarmItem> alarmItemList;

    private AlarmListAdapter alarmListAdapter;
    private boolean isAdapterItemClick = false;
    private boolean isAddAlarmClick = false;

    @Override
    protected void onStop() {
        Log.d(TAG, "AlarmListActivity : onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "AlarmListActivity : onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "AlarmListActivity : onCreate");
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
                        getUiManager().printToast("알람을 최신화했습니다.");
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

                    /*Log.d(TAG, "[" + alarmItems.indexOf(item) + "] 번째 아이템");
                    Log.d(TAG, "- ID : " + item.getId());
                    Log.d(TAG, "- Date : " + item.getDate());
                    Log.d(TAG, "- Time : " + item.getTime());
                    Log.d(TAG, "- List : " + item.getDayOfWeek());
                    Log.d(TAG, "- Name : " + item.getName());
                    Log.d(TAG, "- Type : " + item.getType());
                    Log.d(TAG, "- Uri : " + item.getRingUri());
                    Log.d(TAG, "- VibrateType : " + item.getVibrationType());
                    Log.d(TAG, "- Repeat : " + item.getRepeat());
                    Log.d(TAG, "- ReCallDate : " + item.getReCallDate());
                    Log.d(TAG, "- Active : " + item.isActive());*/
                }
                alarmListAdapter.notifyDataSetChanged();

                // 타이머를 위해 데이터 최신화.
                AlarmService.setAlarmList(alarmItemList);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "AlarmListActivity : onStart");
        checkDrawOverlays();

        // 설정
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                // finish();
            }
        });

        // 알람 추가
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAddAlarmClick) {
                    isAddAlarmClick = true;

                    isFirstEnter = false;
                    Intent intent = new Intent(getApplicationContext(), AlarmInfoActivity.class);
                    startActivity(intent);

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

        // 리스트 아이템 클릭, 롱클릭.
        alarmListAdapter.setOnItemClickListener(new AlarmListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!isAdapterItemClick) {
                    isAdapterItemClick = true;
                    Log.e(TAG, " -1 아이디는 " + alarmItemList.get(position).getId());

                    Intent intent = new Intent(getApplicationContext(), AlarmInfoActivity.class);
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
                        // 삭제
                        new AlarmService.DeleteAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                        getUiManager().printToast("삭제되었습니다.");
                    }
                });
                builder.setNegativeButton("아니오", null);
                builder.show();
            }
        });

        // 활성/비활성
        alarmListAdapter.setOnCheckedChangeListener(new AlarmListAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(int position, boolean isActive) {
                // 체크되있었거나 요일인 경우 ==> 체크조건 없음.
                if(isActive || alarmItemList.get(position).getType().equals(Type.DayOfTheWeek)) {
                    alarmListAdapter.switchCompatMap.get(position).setChecked(!isActive);
                    alarmItemList.get(position).setActive(!isActive);
                    new AlarmService.UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                } else {
                    // 시간 검증.
                    // 만약 시간이 지났으면 내일로 재설정 합니다.
                    if(getSystem().isDatePass(alarmItemList.get(position).getDate(), alarmItemList.get(position).getTime())) {
                        alarmItemList.get(position).setDate(getSystem().addAlarmDate(alarmItemList.get(position).getDate(), 1));
                        getUiManager().printToast("알람 시간이 지나서 내일로 다시 설정했습니다.");
                    }

                    alarmListAdapter.switchCompatMap.get(position).setChecked(!isActive);
                    alarmItemList.get(position).setActive(!isActive);
                    new AlarmService.UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                }
            }
        });
    }

    /**
     * 다른 앱 위에 표시하기를 체크합니다.
     */
    private void checkDrawOverlays() {
        if (!Settings.canDrawOverlays(this)) {
            Log.d(TAG, "다른 앱 위에 그리기.");
            getUiManager().showDialog(this, "안내", "앱이 꺼져있어도 알람이 울리려면 다른 앱 위에 표시하는 권한을 체크해야 합니다.",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getUiManager().printToast("앱이 종료되면 알림이 울리지 않습니다.");
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "2");
                            Intent appDetail = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                            appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(appDetail);
                        }
                    }, "거절", "이동", false);
        }
    }

}
