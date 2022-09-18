package com.bbi.customalarm;

import static com.bbi.customalarm.Object.AlarmItem.convertDayOfWeek;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.TableRow;
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
import com.bbi.customalarm.System.VibrationManager;

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
    private boolean isEditMode = false;

    private Button saveBtn, cancelBtn, openDateBtn, saveDateBtn;
    private ConstraintLayout subLayout;
    private DatePicker datePicker;

    private Button selectDate, selectDayOfWeek;
    private LinearLayout choseDate;
    private TableLayout choseDayOfWeek;
    private TableRow choseDayOfWeekRow;
    private View.OnClickListener weekBtnListener;
    List<View> views = new ArrayList<View>();
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
    private VibrationManager vibratorManager;

    private Drawable dayOff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_info);
        //setMoveClass(AlarmListActivity.class);
        setMoveClassToastMsg("한번 더 터치시 저장내용이 사라집니다.");
        vibratorManager = new VibrationManager(this);

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
        choseDayOfWeekRow = findViewById(R.id.alarmInfo_choseDayOfWeekRow);

        dayOff = getResources().getDrawable(R.drawable.day_btn_off);
        weekBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int position = -1;
                int position = -1;

                for (View button : views) {
                    if(button.equals(view)) {
                        position = views.indexOf(button);
                        break;
                    }
                }

                // 안눌렸는지?
                if(view.getBackground().getConstantState() == getResources().getDrawable(R.drawable.day_btn).getConstantState()) {
                    view.setBackground(getResources().getDrawable(R.drawable.day_btn_off));

                    String dayString = convertDayOfWeek(position);
                    Log.d(TAG, dayString + " 넣기");
                    if(!alarmItem.getDayOfWeek().contains(dayString)) {
                        alarmItem.getDayOfWeek().add(dayString);
                    }
                } else {
                    view.setBackground(getResources().getDrawable(R.drawable.day_btn));
                    alarmItem.getDayOfWeek().remove(convertDayOfWeek(position));
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
        views.add(week1Btn);
        views.add(week2Btn);
        views.add(week3Btn);
        views.add(week4Btn);
        views.add(week5Btn);
        views.add(week6Btn);
        views.add(week7Btn);

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
            alarmItem.setId(saveIntent.getIntExtra(Type.AlarmId, -1));
            Log.e(TAG, " 0 아이디는 " + saveIntent.getIntExtra(Type.AlarmId, -1));
            isEditMode = true;
        }

        getAlarmDatabase().alarmDao().getAll().observe(this, new Observer<List<AlarmItem>>() {
            @Override
            public void onChanged(List<AlarmItem> alarmItems) {
                // 새로 생성한 알람...
                // 초기 생성시 id는 0임.
                if (alarmItem.getId() == 0) {
                    Log.e(TAG, " 1 아이디는 " + alarmItem.getId());
                    // 현재 시간으로 세팅.
                    alarmItem.setStringToDate(
                            datePicker.getYear(),
                            datePicker.getMonth() + 1,
                            datePicker.getDayOfMonth());
                } else {
                    for (AlarmItem item : alarmItems) {
                        if (item.getId() == alarmItem.getId()) {
                            alarmItem = item;

                            setUIFromData(alarmItem);
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
                    try {
                        // 요일 재정렬.
                        if(!alarmItem.getType().equals("Date")) {
                            alarmItem.setDayOfWeek(AlarmItem.resetDayOfWeek(alarmItem.getDayOfWeek()));
                        }
                        if(isEditMode) {
                            new UpdateAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItem);
                        } else {
                            new InsertAsyncTask(getAlarmDatabase().alarmDao()).execute(alarmItem);
                        }
                        finish();
                    } catch (Exception e) {
                        getUiManager().printToast("저장에 실패했습니다. 다시 시도해주세요.");
                    }
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                        VibrationManager.VibrateType type = null;

                        switch (position) {
                            case 0: break;
                            case 1: type = VibrationManager.VibrateType.Knock; break;
                            case 2: type = VibrationManager.VibrateType.Whirlpool; break;
                            case 3: type = VibrationManager.VibrateType.Kicking; break;
                        }

                        if(type != null) {
                            vibratorManager.setType(type);
                            vibratorManager.vibrate(-1);
                            callType.setText(array.get(position));
                            alarmItem.setVibrationType(String.valueOf(VibrationManager.VibrateType.values()[position - 1]));
                        } else {
                            callType.setText(getString(R.string.default_callType));
                            alarmItem.setVibrationType("");
                        }

                        Log.d(TAG, alarmItem.getVibrationType() + " 으로 저장");
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
                        if(position != 0) {
                            reCallType.setText(array.get(position));
                            int repeat = Integer.parseInt(array.get(position).replace("분", ""));
                            alarmItem.setRepeat(repeat);
                        } else {
                            reCallType.setText(getResources().getString(R.string.default_reCallType));
                            alarmItem.setRepeat(0);
                        }
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
    // 진동, 반복 여부는 X.
    private boolean isAllDataEdit() {
        if(!alarmItem.getType().equals("Date")) {
            if(alarmItem.getDayOfWeek().size() == 1 && alarmItem.getDayOfWeek().contains("")) {
                getUiManager().printToast("요일을 하나 이상 선택해주세요.");
                return false;
            }
        }

        if (typeAlarmName.getText().toString().equals("")){
            getUiManager().printToast("알람 이름을 입력해주세요.");
            return false;
        } else if (ringName.getText().toString().equals(getString(R.string.default_ringType))) {
            getUiManager().printToast("알람음을 선택해주세요.");
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
                alarmItem.setRingUri(ring);

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

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    /**
     * 해당 데이터에 맞게 UI를 최신화합니다.
     */
    private void setUIFromData(AlarmItem item) {
        Log.e(TAG, " 2 아이디는 " + item.getId());
        if(!item.getTime().equals("00:00")) {
            String[] time = item.getTime().split(":");
            hourPicker.setValue(Integer.parseInt(time[0]));
            minutePicker.setValue(Integer.parseInt(time[1]));
        }

        Log.e(TAG, "선택된 아이템 : " + item.getType());
        if(item.getType().equals("Date")) {
            selectAlarmType(Type.Date);
        } else {
            selectAlarmType(Type.DayOfTheWeek);

            for (String dayString : item.getDayOfWeek()) {
                int position = AlarmItem.getDayOfWeekPosition(dayString);
                choseDayOfWeekRow.getChildAt(position).setBackground(dayOff);
            }
            //week1Btn.setBackground(getResources().getDrawable(R.drawable.day_btn_off));
        }

        typeAlarmName.setText(item.getName());
        ringName.setText(DocumentFile.fromSingleUri(AlarmInfoActivity.this, item.getRingUri())
                .getName().replace(".ogg", ""));

        if(!item.getVibrationType().equals("")) {
            String type = VibrationManager.getKType(item.getVibrationType());
            callType.setText(type);
        }
        if(item.getRepeat() != 0) {
            reCallType.setText(item.getRepeat() + "분");
        }
    }
}
