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
    private AlarmListAdapter alarmListAdapter;
    private boolean isAdapterItemClick = false;
    private boolean isAddAlarmClick = false;
    private ArrayList<AlarmItem> alarmItemList;

    // 타이머
    private static Timer timerCall;
    private boolean isActiveAlarm = false;
    private BroadcastReceiver broadcastReceiver;

    // 다시 울리는 시간.
    private AlarmItem targetAlarm = null;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "AlarmListActivity : onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AlarmListActivity : onDestroy");
        //timerCall.cancel();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        Log.d(TAG, "AlarmListActivity : onCreate");

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
            }
        });

        timerCall = new Timer();
        timerCall.schedule(new TimerTask() {
            @Override
            public void run() {
                // 알람이 울리지 않을 떄만.
                if(!isActiveAlarm) {
                    Log.d(TAG, "알람 탐색...");

                    checkAlarmTime();
                } else {
                    Log.d(TAG, "알람이 울리고 있습니다.");
                    Intent intent = new Intent(Type.RefreshTime);
                    sendBroadcast(intent);
                }
            }
        }, 0, 3000);

        // 돌아왔을 떄.
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();

                if (action.equals(Type.FinishAlarm)) {
                    Log.d(TAG, "알람이 꺼졌습니다...");

                    if(targetAlarm != null) {
                        targetAlarm.setReCallDate("");
                        setDisableAlarmItem(targetAlarm);
                        targetAlarm = null;
                    }

                    isActiveAlarm = false;
                } else if (action.equals(Type.ReCallAlarm)) {
                    Log.d(TAG, "다시 울립니다.");
                    getUiManager().printToast(targetAlarm.getRepeat() + "분 후에 다시 울립니다.");

                    String[] currentTime = getSystem().getCurrentTimeToString(true).split(" ");
                    String reCallString = getSystem().addAlarmMinute(
                            currentTime[0] + " " + currentTime[1], targetAlarm.getRepeat());
                    targetAlarm.setReCallDate(reCallString);

                    // 테스트
                    targetAlarm.setTime(reCallString.split(" ")[1]);

                    setAlarmData(targetAlarm);
                    isActiveAlarm = false;
                }/* else if(action.equals(Type.CheckAlarm)) {
                    Log.d(TAG, "알람 체크....0");

                    if(!isActiveAlarm) {
                        checkAlarmTime();
                    } else {
                        Log.d(TAG, "알람이 울리고 있습니다.");
                        Intent broadCast = new Intent(Type.RefreshTime);
                        sendBroadcast(broadCast);
                    }
                }*/
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Type.FinishAlarm));
        registerReceiver(broadcastReceiver, new IntentFilter(Type.ReCallAlarm));
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
                // 체크되있었거나 요일인 경우 ==> 체크조건 없음.
                if(isActive || alarmItemList.get(position).getType().equals(Type.DayOfTheWeek)) {
                    alarmListAdapter.switchCompatMap.get(position).setChecked(!isActive);
                    alarmItemList.get(position).setActive(!isActive);
                    new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                } else {
                    // 시간 검증.
                    // 만약 시간이 지났으면 내일로 재설정 합니다.
                    if(getSystem().isDatePass(alarmItemList.get(position).getDate(), alarmItemList.get(position).getTime())) {
                        alarmItemList.get(position).setDate(getSystem().addAlarmDate(alarmItemList.get(position).getDate(), 1));
                        getUiManager().printToast("알람 시간이 지나서 내일로 다시 설정했습니다.");
                    }

                    alarmListAdapter.switchCompatMap.get(position).setChecked(!isActive);
                    alarmItemList.get(position).setActive(!isActive);
                    new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItemList.get(position));
                }
            }
        });
    }

    /**
     * 울려야 할 알람을 확인하고, 울립니다.
     * 울리는 알람타입 우선순위는 날짜 > 요일 입니다.
     * 시간이 겹치는 경우 우선순위는 기존 > 나중에 울릴알람 입니다.
     */
    private void checkAlarmTime() {
        if(alarmItemList.size() != 0) {
            List<AlarmItem> itemList = new ArrayList<>();

            // 현재시간과 알람시간이 같은 알람만 저장.
            for (AlarmItem dataItem : alarmItemList) {
                // 활성화 된 알람만.
                if(dataItem.isActive()) {
                    // 다시 울려야 할 알람인지?
                    if(!dataItem.getReCallDate().equals("")) {
                        if(getSystem().alarmDateCheck(getSystem().setReCallDate(dataItem.getReCallDate()))) {
                            itemList.add(dataItem);
                        }
                    } else {
                        // 요일인지 날짜인지?
                        // 삽입 우선순위는 날짜이다.
                        // 날짜알람은 무조건 앞에 쌓인다.
                        if(dataItem.getType().equals(Type.DayOfTheWeek)) {
                            if(getSystem().alarmDateCheckToWeek(
                                    dataItem.getDayOfWeek().toString(), dataItem.getDate(), dataItem.getTime())) {
                                itemList.add(dataItem);
                            }
                        } else {
                            if(getSystem().alarmDateCheck(dataItem.getDate(), dataItem.getTime())) {
                                itemList.add(0, dataItem);
                            }
                        }
                    }
                }
            }

            // 울릴 알람이 있는지?
            if(itemList.size() > 0) {
                // 현재 앱이 꺼져있으면 다시 깨웁니다.
                /*if(!getSystem().isAppActive()) {
                    Intent intent = new Intent("android.intent.category.LAUNCHER");
                    intent.setClassName("com.bbi.customalarm", "com.bbi.customalarm.AlarmListActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }*/

                targetAlarm = itemList.get(0);
                String printDate, printTime;
                if(targetAlarm.getReCallDate().equals("")) {
                    if(targetAlarm.getType().equals(Type.DayOfTheWeek)) {
                        printDate = getSystem().getCurrentTimeToString(false);
                    } else {
                        printDate = targetAlarm.getDate();
                    }
                    printTime = targetAlarm.getTime();
                } else {
                    printDate = targetAlarm.getReCallDate().split(" ")[0];
                    printTime = targetAlarm.getReCallDate().split(" ")[1];
                }

                String[] data = new String[]{
                        printDate,
                        printTime,
                        targetAlarm.getName(),
                        targetAlarm.getRingUri().toString(),
                        targetAlarm.getVibrationType(),
                        String.valueOf(targetAlarm.getRepeat())
                };

                // 첫 번째 알람을 울립니다.
                Intent intent = new Intent(getApplicationContext(), AlarmPrintActivity.class);
                intent.putExtra(Type.AlarmData, data);
                startActivity(intent);

                isActiveAlarm = true;

                // 첫 번째 알람 이외는 비활성 처리합니다.
                // 나중에 상단에 부재중 알람을 표시해야 합니다.
                for (AlarmItem item : itemList) {
                    if(itemList.indexOf(item) != 0) {
                        setDisableAlarmItem(item);
                    }
                }
            } else {
                Log.d(TAG, "타이머: 울릴 알람이 없습니다.");
            }
        }
    }

    /**
     * 알람 아이템의 상태를 비활성화 시킵니다.
     * 요일알람의 경우 갱신상태를 최신화 시킵니다.
     */
    private void setDisableAlarmItem(AlarmItem item) {
        for (AlarmItem data : alarmItemList) {
            if(item.getId() == data.getId()) {
                data.setReCallDate("");

                // 요일의 경우 울린날짜를 저장합니다.(갱신여부)
                if(data.getType().equals(Type.DayOfTheWeek)) {
                    data.setDate(getSystem().getCurrentTimeToString(false));
                    new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(data);
                } else {
                    // 날짜알람인 경우 그냥 비활성화 시킵니다.(활성화 시 날짜체크함)
                    data.setActive(false);
                    new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(data);
                }

                return;
            }
        }

        Log.d(TAG, "알람 비활성화: 활성화 될 아이템 찾지 못함.");
    }

    /**
     * 알람아이템을 업데이트합니다.
     * 유효한 아이템이여야 실행됩니다.
     */
    private void setAlarmData(AlarmItem item) {
        for (AlarmItem data : alarmItemList) {
            if(item.getId() == data.getId()) {
                new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(item);
                return;
            }
        }
    }

    /**
     * 다른 앱 위에 표시하기를 체크합니다.
     */
    private void checkDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

}
