package com.example.health3.activity;

import android.content.Intent;
import android.health.connect.HealthConnectManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import com.example.health3.R;
import com.example.health3.service.UnifiedService;
import com.example.health3.utils.LogUtils;

public class MainActivity extends BaseActivity {
    private ToggleButton toggleButton;
    private TextView txtvUser, txtvSerial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isWearOS()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_mobile);
        }
        txtvUser = findViewById(R.id.text_user_name);
        txtvSerial = findViewById(R.id.text_user_serial);

        txtvUser.setText(getString(R.string.user_name_prefix,shp.getString("userName","")));
        txtvSerial.setText(getString(R.string.user_serial_prefix,shp.getString(KEY_SERIAL,"")));

        initializeActivity();
    }


    private void initializeActivity() {
        toggleButton = findViewById(R.id.toggle_button);
        toggleButton.setEnabled(true);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 토글 On: 데이터 수집 시작 (예: 위치 서비스 시작)
                    LogUtils.d("데이터 수집 시작",MainActivity.this);
                    startDataCollection();
                }else {
                    // 토글 Off: 데이터 수집 중지 (예: 위치 서비스 중지)
                    LogUtils.d("데이터 수집 중지",MainActivity.this);
                    stopDataCollection();
                }
            }
        });
    }


    private void startDataCollection() {
        Intent intent = new Intent(this, UnifiedService.class);
        startService(intent);
    }

    // 예제: 데이터 수집(위치 서비스) 중지
    private void stopDataCollection() {
        Intent intent = new Intent(this, UnifiedService.class);
        stopService(intent);
    }

//    private void redirectToRegister() {
//        // a.2 로컬에 값이 없다면 시리얼 넘버 체크 화면으로 이동
//        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
//        startActivity(intent);
//        finish();
//    }

}
