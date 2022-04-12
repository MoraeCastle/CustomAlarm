package com.bbi.customalarm.System;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bbi.customalarm.PermissionActivity;

/**
 * 액티비티 담당 클래스
 */
public class BaseActivity extends AppCompatActivity {
    private SystemManager system;
    private Class<?> moveClass;

    public SystemManager getSystem() {
        return system;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        system = new SystemManager();

        system.refreshDeviceSavaData(getApplicationContext());
    }

    public void setMoveClass(Class<?> moveClass) {
        this.moveClass = moveClass;
    }

    @Override
    public void onBackPressed() {
        if(moveClass != null) {
            Intent intent = new Intent (getApplicationContext(), moveClass);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}
