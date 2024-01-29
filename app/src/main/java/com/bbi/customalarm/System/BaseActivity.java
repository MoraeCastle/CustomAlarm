package com.bbi.customalarm.System;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.PermissionActivity;
import com.bbi.customalarm.Room.AlarmDao;
import com.bbi.customalarm.Room.AlarmDatabase;

/**
 * 액티비티 담당 클래스
 */
public class BaseActivity extends AppCompatActivity {
    private SystemManager system;
    private UIManager uiManager;
    private Class<?> moveClass;
    private String moveClassToastMsg;
    private AlarmDatabase alarmDatabase;

    public AlarmDatabase getAlarmDatabase() {
        return alarmDatabase;
    }

    public SystemManager getSystem() {
        return system;
    }
    public UIManager getUiManager() {
        return uiManager;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        system = new SystemManager();
        uiManager = new UIManager();
        uiManager.setContext(this);

        system.refreshDeviceSavaData(getApplicationContext());
        alarmDatabase = AlarmDatabase.getAppDatabase(this);
    }

    public void setMoveClass(Class<?> moveClass) {
        this.moveClass = moveClass;
    }
    public void setMoveClassToastMsg(String msg) {
        this.moveClassToastMsg = msg;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (moveClass != null) {
            Intent intent = new Intent(getApplicationContext(), moveClass);
            startActivity(intent);
            finish();
        } else {
            if (getUiManager().isToastActive()) {
                finish();
            } else {
                getUiManager().printToast(moveClassToastMsg);
            }
        }
    }
}
