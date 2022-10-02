package com.bbi.customalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bbi.customalarm.System.BaseActivity;

public class SettingActivity extends BaseActivity {
    private ImageView backBtn;
    private ConstraintLayout askAdminBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backBtn = findViewById(R.id.setting_backBtn);
        askAdminBtn = findViewById(R.id.setting_askAdminBtn);

        setMoveClass(AlarmListActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 설정
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // 문의
        askAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "customalarmqna@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "내맘대로알람 문의");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, ""));
            }
        });

    }
}
