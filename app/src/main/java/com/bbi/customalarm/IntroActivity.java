package com.bbi.customalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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

        if(getSystem().isDisagreePermission(getApplicationContext(), getResources().getStringArray(R.array.permission_intro))) {
            moveNextScene = PermissionActivity.class;
        } else {
            moveNextScene = MainActivity.class;
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