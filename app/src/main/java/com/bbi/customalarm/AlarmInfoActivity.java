package com.bbi.customalarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbi.customalarm.Adapter.AlarmListAdapter;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.System.BaseActivity;
import com.bbi.customalarm.System.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 알람 아이템 액티비티
 */
public class AlarmInfoActivity extends BaseActivity {
    private final String TAG = "AlarmInfoActivity";

    private ImageView backBtn;
    private NumberPicker hourPicker, minutePicker;

    private AlarmItem alarmItem;

    private Button saveBtn, cancelBtn, openDateBtn, saveDateBtn;
    private ConstraintLayout subLayout;
    private DatePicker datePicker;

    private Button selectDate, selectDayOfWeek;
    private LinearLayout choseDate;
    private TableLayout choseDayOfWeek;
    private View.OnClickListener weekBtnListener;
    private Button week1Btn, week2Btn, week3Btn, week4Btn, week5Btn, week6Btn, week7Btn;

    // 데이터
    private EditText typeAlarmName;
    private TextView dateTxt;
    private TextView ringName;
    private TextView callType;
    private TextView reCallType;

    private Intent saveIntent;

    // 알람
    private final static int REQUESTCODE_RINGTONE_PICKER = 1000;
    private MediaPlayer mMediaPlayer;
    private ImageButton ringBtn, callBtn, reCallBtn;
    private String m_strRingToneUri;

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
        saveBtn = findViewById(R.id.alarmInfo_saveBtn);
        cancelBtn = findViewById(R.id.alarmInfo_cancelBtn);

        selectDate = findViewById(R.id.alarmInfo_selectDate);
        selectDayOfWeek = findViewById(R.id.alarmInfo_selectDayOfWeek);
        choseDate = findViewById(R.id.alarmInfo_choseDate);
        choseDayOfWeek = findViewById(R.id.alarmInfo_choseDayOfWeek);
        weekBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getBackground().getConstantState() == getResources().getDrawable(R.drawable.day_btn).getConstantState()) {
                    view.setBackground(getResources().getDrawable(R.drawable.day_btn_off));
                } else {
                    view.setBackground(getResources().getDrawable(R.drawable.day_btn));
                }
            }
        };
        week1Btn = findViewById(R.id.alarmInfo_day01);
        week2Btn = findViewById(R.id.alarmInfo_day02);
        week3Btn = findViewById(R.id.alarmInfo_day03);
        week4Btn = findViewById(R.id.alarmInfo_day04);
        week5Btn = findViewById(R.id.alarmInfo_day05);
        week6Btn = findViewById(R.id.alarmInfo_day06);
        week7Btn = findViewById(R.id.alarmInfo_day07);

        week1Btn.setOnClickListener(weekBtnListener);
        week2Btn.setOnClickListener(weekBtnListener);
        week3Btn.setOnClickListener(weekBtnListener);
        week4Btn.setOnClickListener(weekBtnListener);
        week5Btn.setOnClickListener(weekBtnListener);
        week6Btn.setOnClickListener(weekBtnListener);
        week7Btn.setOnClickListener(weekBtnListener);

        typeAlarmName = findViewById(R.id.alarmInfo_alarmNameEdit);
        dateTxt = findViewById(R.id.alarmInfo_dateTxt);
        ringName = findViewById(R.id.alarmInfo_alarmRingTxt);
        callType = findViewById(R.id.alarmInfo_alarmCallTxt);
        reCallType = findViewById(R.id.alarmInfo_alarmReCallTxt);

        ringBtn = findViewById(R.id.alarmInfo_alarmRingBtn);
        callBtn = findViewById(R.id.alarmInfo_alarmCallBtn);
        reCallBtn = findViewById(R.id.alarmInfo_alarmReCallBtn);

        getUiManager().setToastView(findViewById(R.id.activity_alarm_info));

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

        saveIntent = getIntent();

        if (saveIntent.hasExtra(Type.AlarmId)) {
            alarmItem.setId(saveIntent.getIntExtra(Type.AlarmId, 0));
        }

        getAlarmDatabase().alarmDao().getAll().observe(this, new Observer<List<AlarmItem>>() {
            @Override
            public void onChanged(List<AlarmItem> alarmItems) {
                Log.e(TAG, alarmItems.toString());

                // 새로 생성한 알람...
                if (alarmItem.getId() == 0) {
                    alarmItem.setStringToDate(
                            datePicker.getYear(),
                            datePicker.getMonth() + 1,
                            datePicker.getDayOfMonth());
                } else {
                    for (AlarmItem item : alarmItems) {
                        if (item.getId() == alarmItem.getId()) {
                            alarmItem = item;
                            break;
                        }
                    }
                }

                dateTxt.setText(alarmItem.getDateToSet());
            }
        });
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
                int[] saveArray = alarmItem.getIntegerTime(alarmItem.getTime());
                saveArray[0] = end;

                alarmItem.setTime(alarmItem.getStringTime(saveArray));
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int start, int end) {
                int[] saveArray = alarmItem.getIntegerTime(alarmItem.getTime());
                saveArray[1] = end;

                alarmItem.setTime(alarmItem.getStringTime(saveArray));
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

                if (alarmItem.getDate().equals("")) {
                    alarmItem.setStringToDate(
                            datePicker.getYear(),
                            datePicker.getMonth() + 1,
                            datePicker.getDayOfMonth());
                }

                dateTxt.setText(alarmItem.getDateToSet());
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAlarmType(Type.Date);
            }
        });
        selectDayOfWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAlarmType(Type.DayOfTheWeek);
            }
        });

        // 저장 및 취소
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllDataEdit()) {
                    new InsertAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItem);
                    onBackPressed();
                } else {
                    Log.d(TAG, "ddd");
                    getUiManager().printToast("모든 내용을 입력해주세요");
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmListActivity.class);
                startActivity(intent);
            }
        });

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                alarmItem.setStringToDate(year, month + 1, day);
            }
        });

        typeAlarmName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                alarmItem.setName(editable.toString());
            }
        });

        ringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRingtoneChooser();
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> array = Arrays.asList(getResources().getStringArray(R.array.alarm_vibration_type));

                //다이얼로그에 리스트 담기
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmInfoActivity.this);
                builder.setTitle("패턴 유형 선택");
                builder.setItems(getResources().getStringArray(R.array.alarm_vibration_type), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        callType.setText(array.get(position));
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        reCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> array = Arrays.asList(getResources().getStringArray(R.array.alarm_vibration_count));

                //다이얼로그에 리스트 담기
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmInfoActivity.this);
                builder.setTitle("반복 설정");
                builder.setItems(getResources().getStringArray(R.array.alarm_vibration_count), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        reCallType.setText(array.get(position));
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    // 날짜 선택기
    private void setVisibleDatePicker(boolean active) {
        if (active) {
            subLayout.setVisibility(View.VISIBLE);

            String[] dayArray = new String[]{};
            dayArray = alarmItem.getDate().split("-");

            datePicker.updateDate(
                    Integer.parseInt(dayArray[0]),
                    Integer.parseInt(dayArray[1]) - 1,
                    Integer.parseInt(dayArray[2]));
        } else {
            subLayout.setVisibility(View.GONE);
        }
    }

    // 알람 타입 설정 ... 날짜 or 요일
    private void selectAlarmType(String type) {
        alarmItem.setType(type);

        switch (type) {
            case "Date":
                selectDate.setBackground(getDrawable(R.drawable.button_on));
                selectDayOfWeek.setBackground(getDrawable(R.drawable.button_off));

                choseDate.setVisibility(View.VISIBLE);
                choseDayOfWeek.setVisibility(View.GONE);

                break;
            case "DayOfTheWeek":
                selectDate.setBackground(getDrawable(R.drawable.button_off));
                selectDayOfWeek.setBackground(getDrawable(R.drawable.button_on));

                choseDate.setVisibility(View.GONE);
                choseDayOfWeek.setVisibility(View.VISIBLE);

                break;
        }
    }

    // 모두 입력 체크
    // 하나라도 안되면 false;
    private boolean isAllDataEdit() {
        if (typeAlarmName.getHint().toString().equals(getString(R.string.default_typeAlarmName))
                || ringName.getText().toString().equals(getString(R.string.default_ringType))
                || callType.getText().toString().equals(getString(R.string.default_callType))
                || reCallType.getText().toString().equals(getString(R.string.default_reCallType))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 알람음 울리기.
     */
    private void startRingtone(Uri uriRingtone) {
        this.releaseRingtone();

        try {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uriRingtone );
            if( mMediaPlayer == null ) {
                throw new Exception( "Can't create player" ); }
            // STREAM_VOICE_CALL, STREAM_SYSTEM, STREAM_RING, STREAM_MUSIC, STREAM_ALARM
            // STREAM_NOTIFICATION, STREAM_DTMF
            // mMediaPlayer.setAudioStreamType( AudioManager.STREAM_ALARM );
            mMediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
            //mMediaPlayer.setAudioAttributes();
            mMediaPlayer.start();
        } catch( Exception e ) {
            Toast.makeText( this, e.getMessage(), Toast.LENGTH_SHORT ).show();
            Log.e(TAG, e.getMessage() );
            e.printStackTrace();
        }
    }

    private void releaseRingtone() {
        if( mMediaPlayer != null ) {
            if( mMediaPlayer.isPlaying() ) {
                mMediaPlayer.stop();
            }

            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void showRingtoneChooser() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose Ringtone!" );
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);

        //-- 알림 선택창이 떴을 때, 기본값으로 선택되어질 ringtone설정
        if( m_strRingToneUri != null && m_strRingToneUri.isEmpty() ) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(m_strRingToneUri));
        }

        this.startActivityForResult( intent, REQUESTCODE_RINGTONE_PICKER );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE_RINGTONE_PICKER) {
            //-- 선택된 링톤을 재생하도록 한다.
            if (resultCode == RESULT_OK) {
                Uri ring = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                String fileName = DocumentFile.fromSingleUri(this, ring).getName().replace(".ogg", "");

                if (ring != null) {
                    ringName.setText(fileName);
                    //this.startRingtone( ring );
                } else {
                    ringName.setText( getResources().getString(R.string.default_ringType));
                }
            }
        }
    }
}
