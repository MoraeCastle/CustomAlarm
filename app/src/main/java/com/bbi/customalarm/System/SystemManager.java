package com.bbi.customalarm.System;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bbi.customalarm.Object.AlarmItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 시스템 내부에 접근하는 기능을 모아놓은 클래스
 */
public class SystemManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private List<Integer> requestCodeList;

    public List<Integer> getRequestCodeList() {
        return requestCodeList;
    }

    /**
     * 데이터 넣기
     */
    public void refreshDeviceSavaData(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
    }
    public void removeAllDeviceData() {
        editor.clear();
        editor.commit();
    }
    public Object getDeviceSaveData(String key, Type.SearchType type) {
        switch (type) {
            case Int:
                return prefs.getInt(key, 0);
            case String:
                return prefs.getString(key, "");
            case Boolean:
                return prefs.getBoolean(key, false);
            case Float:
                return prefs.getFloat(key, 0f);
            default:
                return prefs.getInt(key, -1);
        }
    }
    public void saveDataInDevice(String key, Type.SearchType type, Object data) {
        switch (type) {
            case String:
                editor.putString(key, data.toString()); break;
            case Boolean:
                editor.putBoolean(key, Boolean.parseBoolean(data.toString())); break;
            case Float:
                editor.putFloat(key, Float.parseFloat(data.toString())); break;
            default:
                editor.putInt(key, 0); break;
        }
        editor.apply();
    }

    /**
     * 퍼미션
     */
    public void requestPermission(Activity activity, String[] permissionArray, int requestCode) {
        if(requestCodeList == null) {
            requestCodeList = new ArrayList<>();
        }

        if(!requestCodeList.contains(requestCode)) {
            requestCodeList.add(requestCode);
        }

        ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
    }

    // 퍼미션 하나라도 비동의했는지?
    public boolean isDisagreePermission(Context context, String[] permissionArray) {
        boolean answer = false;

        for (String permission : permissionArray) {
            int checkCode = ContextCompat.checkSelfPermission(context, permission);

            if(checkCode == PackageManager.PERMISSION_DENIED) {
                answer = true;
                break;
            }
        }

        return answer;
    }

    // 비동의한 퍼미션 구하기
    public String[] findDisagreePermission(Context context, String[] permissionArray) {
        List<String> answer = new ArrayList<>();

        for (String permission : permissionArray) {
            int checkCode = ContextCompat.checkSelfPermission(context, permission);

            if(checkCode == PackageManager.PERMISSION_DENIED) {
                answer.add(permission);
            }
        }

        return (String[])answer.toArray();
    }

    /**
     * 현재 시간 가져오기
     */
    public String getCurrentTimeToString(boolean isIncludeSec) {
        // 테스트
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf;

        if(isIncludeSec) {
            sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }

        String getTime = sdf.format(date);

        return getTime;
    }

    /**
     * 날짜를 입력받고, 현재 시간인지 여부를 판단합니다.
     */
    public boolean alarmDateCheck(String date, String time) {
        long now = System.currentTimeMillis();
        Date todayDate = new Date(now);
        todayDate.setSeconds(0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm");

        try {
            Date alarmDate = format.parse(date + " " + time);

            return format.format(alarmDate).equals(format.format(todayDate));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 요일로 알람시간을 체크합니다.
     */
    public boolean alarmDateCheckToWeek(String week, String date, String time) {
        // 시간 먼저 체크.
        long now = System.currentTimeMillis();
        Date todayDate = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm");

        try {
            Date alarmDate = format.parse(date + " " + time);

            String[] todayArray = format.format(todayDate).split(" ");
            String[] alarmArray = format.format(alarmDate).split(" ");

            // 년,월,일이 같으면 알람이 갱신된 것이므로 false.
            if(todayArray[0].equals(alarmArray[0])) {
                Log.d("Timer", "날짜가 같습니다. false");
                return false;
            }

            // 이제 시간을 비교.
            if(todayArray[1].equals(alarmArray[1])) {
                Log.d("Timer", "날짜가 다르나 시간은 같습니다.");
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);

                int todayWeek = cal.get(Calendar.DAY_OF_WEEK);

                // 겹치는 요일을 확인.
                String data = week.replaceAll("\\[|\\]", "");
                for (String weekString : data.replaceAll(" ", "").split(",")) {
                    if(weekString.equals(AlarmItem.convertDayOfWeek(todayWeek - 1))) {
                        Log.d("Timer", "요일입니다.");
                        return true;
                    }
                }
            }

            Log.d("Timer", "날짜와 시간 모두 다릅니다. false");
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 해당 알람시간이 지났는지?
     */
    public boolean isDatePass(String date, String time) {
        long now = System.currentTimeMillis();
        Date todayDate = new Date(now);
        todayDate.setSeconds(0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm");

        try {
            Date alarmDate = format.parse(date + " " + time);

            return todayDate.compareTo(alarmDate) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    // 해당 일만큼 날짜를 더해서 문자열로 반환합니다.
    public String addAlarmDate(String date , int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date alarmDate = format.parse(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(alarmDate);

            cal.add(Calendar.DAY_OF_MONTH, day);

            return format.format(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }
}
