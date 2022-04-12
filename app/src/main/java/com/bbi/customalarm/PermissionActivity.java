package com.bbi.customalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bbi.customalarm.System.BaseActivity;

/**
 * 권한
 */
public class PermissionActivity extends BaseActivity {
    private ConstraintLayout okButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        okButtonLayout = findViewById(R.id.permission_okButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        okButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSystem().requestPermission(
                        PermissionActivity.this
                        ,getResources().getStringArray(R.array.permission_intro)
                        , 1);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case 1:
                for(int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] != 0) {
                        Toast.makeText(this, "권한 거부로 인해 적립 서비스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        this.onBackPressed();
                        return;
                    }
                }

                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            default:
        }

    }
}