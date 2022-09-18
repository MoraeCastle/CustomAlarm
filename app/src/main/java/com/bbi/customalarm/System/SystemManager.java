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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
     * 날짜를 입력받고, 시간이 지났는지 여부를 판단합니다.
     */
    public boolean travelDateCheck(String date, String time) {
        long now = System.currentTimeMillis();
        Date todayDate = new Date(now);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm");

        try {
            Date alarmDate = format.parse(date + " " + time);
            //Date currentDate = format.parse(getCurrentTimeToString(false));

            return alarmDate.compareTo(todayDate) > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
