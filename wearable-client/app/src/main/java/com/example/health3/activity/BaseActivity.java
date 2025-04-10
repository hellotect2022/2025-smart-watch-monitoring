package com.example.health3.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health3.utils.NetworkManager;
import com.example.health3.utils.PermissionManager;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    public static final String KEY_SERIAL = "SERIAL";
    public SharedPreferences shp;
    public NetworkManager networkManager = NetworkManager.getInstance();

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        networkManager.init();
        // 권한체크
        requestRequiredPermissions();
    }
    private void initializeApp() {}


    private void requestRequiredPermissions() {
        PermissionManager.requestPermissions(this, REQUIRED_PERMISSIONS,
                new PermissionManager.PermissionCallback() {
                    @Override
                    public void onAllPermissionsGranted() {
                        // 모든 권한이 승인된 경우
                        initializeApp();
                    }

                    @Override
                    public void onPermissionsDenied(List<String> deniedPermissions) {
                        // 일부 권한이 거부된 경우
                        handleDeniedPermissions(deniedPermissions);
                    }
                }
        );
    }

    // 권한 요청에 대한 activity callback 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        PermissionManager.handlePermissionResult(requestCode,permissions,grantResults,new PermissionManager.PermissionCallback() {

            @Override
            public void onAllPermissionsGranted() {
                initializeApp();
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                handleDeniedPermissions(deniedPermissions);
            }
        });
    }

    // 권한 거절에 대한 Alert message 처리
    private void handleDeniedPermissions(List<String> deniedPermissions) {
        StringBuilder message = new StringBuilder("다음 권한이 필요합니다:\n");

        for (String permission : deniedPermissions) {
            switch (permission) {
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    message.append("- 위치 정보\n");
                    break;
                case Manifest.permission.BODY_SENSORS:
                    message.append("- 센서 데이터\n");
                    break;
                case Manifest.permission.ACTIVITY_RECOGNITION:
                    message.append("- 활동 인식\n");
                    break;
                case Manifest.permission.BLUETOOTH_CONNECT:
                    message.append("- BLE 연결\n");
                case Manifest.permission.BLUETOOTH_SCAN:
                    message.append("- BLE 스캔\n");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("권한 필요")
                .setMessage(message.toString())
                .setPositiveButton("설정", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    // 권한 없이도 사용 가능한 기본 기능 제공
                    Toast.makeText(this, "일부 기능이 제한됩니다.", Toast.LENGTH_LONG).show();
                })
                .show();
    }


    public boolean isWearOS() {
        PackageManager packageManager = getPackageManager();
        return packageManager.hasSystemFeature("android.hardware.type.watch");
    }
}
