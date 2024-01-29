package com.bbi.customalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.bbi.customalarm.System.BaseActivity;

/**
 * 인트로
 */
public class IntroActivity extends BaseActivity {
    private Class<?> moveNextScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 다른 앱 위에 그리기 권한 확인.
        if (!Settings.canDrawOverlays(this)) {
            moveNextScene = PermissionActivity.class;
        } else {
            moveNextScene = AlarmListActivity.class;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent (getApplicationContext(), moveNextScene);
                startActivity(intent);
                finish();
            }
        },1000);
    }
}