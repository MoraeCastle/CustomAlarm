package com.bbi.customalarm;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
                getSystem().requestPermission(PermissionActivity.this
                        ,getResources().getStringArray(R.array.permission_intro)
                        ,1000);

            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
        boolean isCheckPermission = true;

        // 모든 퍼미션을 허용했는지 체크합니다.
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                isCheckPermission = false;
                break;
            }
        }

        if ( isCheckPermission ) {
            Intent intent = new Intent (getApplicationContext(), AlarmListActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "권한 거부로 인해 적립 서비스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
            return;
        }
    }
}