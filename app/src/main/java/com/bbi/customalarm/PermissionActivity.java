package com.bbi.customalarm;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import com.bbi.customalarm.System.BaseActivity;

/**
 * 권한
 */
public class PermissionActivity extends BaseActivity {
    private final String TAG = "Alarm";

    private ConstraintLayout okButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        getUiManager().setToastView(findViewById(R.id.activity_permission));

        okButtonLayout = findViewById(R.id.permission_okButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // '동의' 버튼 클릭.
        okButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDrawOverlays();
            }
        });
    }

    /**
     * 다른 앱 위에 표시하기를 체크합니다.
     */
    private void checkDrawOverlays() {
        if (!Settings.canDrawOverlays(this)) {
            getUiManager().showDialog(this, "권한 요청", "권한 체크 창으로 이동합니다.",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            moveNextScene();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent appDetail = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                            appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(appDetail);
                        }
                    }, "보류", "이동", false);
        } else {
            moveNextScene();
        }
    }

    private void moveNextScene() {
        Intent intent = new Intent (getApplicationContext(), AlarmListActivity.class);
        startActivity(intent);
        finish();
    }
}