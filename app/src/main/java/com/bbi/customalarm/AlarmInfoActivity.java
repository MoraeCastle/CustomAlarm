package com.bbi.customalarm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbi.customalarm.Adapter.AlarmListAdapter;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.System.BaseActivity;
import com.bbi.customalarm.System.Type;

import java.util.ArrayList;

/**
 * 알람 아이템 액티비티
 */
public class AlarmInfoActivity extends BaseActivity {
    private ImageView backBtn;
    private NumberPicker hourPicker, minutePicker;

    private AlarmItem alarmItem;

    private Button openDateBtn, saveDateBtn;
    private ConstraintLayout subLayout;
    private DatePicker datePicker;

    private Button selectDate, selectDayOfWeek;
    private LinearLayout choseDate, choseDayOfWeek;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_info);
        setMoveClass(AlarmListActivity.class);

        backBtn = findViewById(R.id.alarmInfo_backBtn);
        hourPicker = findViewById(R.id.alarmInfo_hourPicker);
        minutePicker = findViewById(R.id.alarmInfo_minutePicker);
        openDateBtn = findViewById(R.id.alarmInfo_openDateBtn);
        saveDateBtn = findViewById(R.id.alarmInfo_saveDateBtn);
        subLayout = findViewById(R.id.alarmInfo_subLayout);
        datePicker = findViewById(R.id.alarmInfo_datePicker);

        selectDate = findViewById(R.id.alarmInfo_selectDate);
        selectDayOfWeek = findViewById(R.id.alarmInfo_selectDayOfWeek);
        choseDate = findViewById(R.id.alarmInfo_choseDate);
        choseDayOfWeek = findViewById(R.id.alarmInfo_choseDayOfWeek);

        alarmItem = new AlarmItem();


        hourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        minutePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });


        hourPicker.setMaxValue(24);
        minutePicker.setMaxValue(24);
    }

    @Override
    protected void onStart() {
        super.onStart();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 시간 변경
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int start, int end) {
                alarmItem.getTime()[0] = end;
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int start, int end) {
                alarmItem.getTime()[1] = end;
            }
        });

        openDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibleDatePicker(true);
            }
        });
        saveDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibleDatePicker(false);
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAlarmType(Type.AlarmType.Date);
            }
        });
        selectDayOfWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAlarmType(Type.AlarmType.DayOfTheWeek);
            }
        });
    }

    // 날짜 선택기
    private void setVisibleDatePicker(boolean active) {
        if(active) {
            subLayout.setVisibility(View.VISIBLE);
        } else {
            subLayout.setVisibility(View.GONE);
        }
    }

    // 알람 타입 설정 ... 날짜 or 요일
    private void selectAlarmType(Type.AlarmType type) {
        alarmItem.setType(type);

        switch (type) {
            case Date:
                selectDate.setBackground(getDrawable(R.drawable.button_on));
                selectDayOfWeek.setBackground(getDrawable(R.drawable.button_off));

                choseDate.setVisibility(View.VISIBLE);
                choseDayOfWeek.setVisibility(View.GONE);

                break;
            case DayOfTheWeek:
                selectDate.setBackground(getDrawable(R.drawable.button_off));
                selectDayOfWeek.setBackground(getDrawable(R.drawable.button_on));

                choseDate.setVisibility(View.GONE);
                choseDayOfWeek.setVisibility(View.VISIBLE);

                break;
        }
    }
}
