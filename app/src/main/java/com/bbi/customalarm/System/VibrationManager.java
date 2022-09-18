package com.bbi.customalarm.System;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.CombinedVibration;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

import com.bbi.customalarm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 진동 기능을 조작합니다.
 */
public class VibrationManager {
    private Version deviceVersion;
    private Vibrator vibrator;
    private VibratorManager manager;
    private Context context;
    private VibrateType type;

    // 안드로이드 버전.
    private enum Version {
        Vibrator,   // 26 down - 진동만 됨.
        Effect,     // 26 ~ 30 - 이하 세기 조절 가능.
        Manager     // 31 up
    }

    public enum VibrateType{
        Knock,
        Whirlpool,
        Kicking;
    }

    public static String getKType(String type) {
        switch (type) {
            case "Knock": return "노크";
            case "Whirlpool": return "소용돌이";
            case "Kicking": return "발길질";
            default: return "선택 안함";
        }
    }

    public VibrationManager(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            deviceVersion = Version.Manager;
            manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            // 이 경우는 현재 앱스토어에 게시되지 않은 버전임.
            deviceVersion = Version.Vibrator;
        } else {
            deviceVersion = Version.Effect;
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    public void setType(VibrateType type) {
        this.type = type;
    }

    /**
     * 입력된 배열로 진동합니다.
     * @param timing 진동 패턴
     * @param power 진동 세기
     * @param isRepeat 반복 여부
     */
    public void vibrateToCustom(long[] timing, int[] power, int isRepeat) {
        VibrationEffect effect = null;
        if(power == null) {
            effect = VibrationEffect.createWaveform(timing, isRepeat);
        } else {
            effect = VibrationEffect.createWaveform(timing, power, isRepeat);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            CombinedVibration combined = CombinedVibration.createParallel(effect);
            manager.vibrate(combined);
        } else {
            vibrator.vibrate(effect);
        }
    }

    public void vibrate(int isRepeat) {
        VibrationEffect effect = null;
        if(getPower(type) == null) {
            Log.d("AlarmInfoActivity", "not Power");
            effect = VibrationEffect.createWaveform(getTiming(type), isRepeat);
        } else {
            Log.d("AlarmInfoActivity", "have Power");
            effect = VibrationEffect.createWaveform(getTiming(type), getPower(type), isRepeat);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            CombinedVibration combined = CombinedVibration.createParallel(effect);
            manager.vibrate(combined);
        } else {
            vibrator.vibrate(effect);
        }
    }

    /**
     * 진동 종료.
     */
    public void cancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            manager.cancel();
        } else {
            vibrator.cancel();
        }
    }

    public long[] getTiming(VibrateType type) {
        String[] timing = null;
        long[] result;

        String data = "";
        switch (type) {
            case Knock:
                data = context.getResources().getStringArray(R.array.vibrate_knock)[0]; break;
            case Whirlpool:
                data = context.getResources().getStringArray(R.array.vibrate_whirlpool)[0]; break;
            case Kicking:
                data = context.getResources().getStringArray(R.array.vibrate_kicking)[0]; break;
        }

        timing = data.split(",");

        if(timing.length == 1) return null;
        result = new long[timing.length];

        for (int i=0; i<timing.length; i++) {
            Log.d("AlarmInfoActivity", "timing: " + timing[i]);
            result[i] = Long.parseLong(timing[i].replace(" ", ""));
        }

        return result;
    }

    public int[] getPower(VibrateType type) {
        String[] power = null;
        int[] result;

        String data = "";
        switch (type) {
            case Knock:
                data = context.getResources().getStringArray(R.array.vibrate_knock)[1]; break;
            case Whirlpool:
                data = context.getResources().getStringArray(R.array.vibrate_whirlpool)[1]; break;
            case Kicking:
                data = context.getResources().getStringArray(R.array.vibrate_kicking)[1]; break;
        }

        power = data.split(",");

        if(power.length == 1) return null;
        result = new int[power.length];

        for (int i=0; i<power.length; i++) {
            Log.d("AlarmInfoActivity", "power: " + power[i]);
            result[i] = Integer.parseInt(power[i].replace(" ", ""));
        }

        return result;
    }
}
