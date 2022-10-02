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
    private boolean enterPermission = false;

    private ActivityResultLauncher<String> permissionLC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        getUiManager().setToastView(findViewById(R.id.activity_permission));

        okButtonLayout = findViewById(R.id.permission_okButton);

        permissionLC
                = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(result) {
                        enterPermission = true;
                        checkDrawOverlays();
                    } else {
                        checkPermission();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        okButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionLC.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });


        if(enterPermission) {
            moveNextScene();
        }
    }

    /*public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            moveNextScene();
        }
        else {
            Toast.makeText(this, "권한 거부로 인해 서비스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
            return;
        }
    }*/

    private void checkPermission() {
        // false 면 처음보거나 다시 묻지 않음을 선택한 경우.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getUiManager().printToast("권한이 허용되어야 앱을 이용할 수 있습니다.");
            //permissionLC.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            getUiManager().showDialog(this, "필수권한 재요청", "권한이 여러번 거부되었습니다.\n직접 설정에서 권한을 허용해주세요.\n('파일 및 미디어' 혹은 '저장공간'을 허용해주세요.)",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getUiManager().printToast("권한이 허용되어야 앱을 이용할 수 있습니다.");
                            //finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                            appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                            appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(appDetail);
                            //finish();
                        }
                    }, "거부", "허용", false);
        }
    }

    /**
     * 다른 앱 위에 표시하기를 체크합니다.
     */
    private void checkDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                getUiManager().showDialog(this, "선택권한 요청", "권한 체크 창으로 이동합니다.",
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
    }

    private void moveNextScene() {
        Intent intent = new Intent (getApplicationContext(), AlarmListActivity.class);
        startActivity(intent);
        finish();
    }
}