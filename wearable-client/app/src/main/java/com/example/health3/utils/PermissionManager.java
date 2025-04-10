package com.example.health3.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private static final int PERMISSION_REQUEST_CODE=1000;

    public interface PermissionCallback {
        void onAllPermissionsGranted();
        void onPermissionsDenied(List<String> deniedPermissions);
    }

    public static void requestPermissions(Activity activity, String[] permissions, PermissionCallback callback) {
        List<String> permissionsToRequest = new ArrayList<>();

        // 필요한 권한 목록 리스트에 추가
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.isEmpty()) {
            callback.onAllPermissionsGranted();
            return;
        }

        // 권한 요청
        ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[0]),PERMISSION_REQUEST_CODE);
    }

    public static void handlePermissionResult(
            int requestCode,
            String[] permissions,
            int[] grantResults,
            PermissionCallback callback
    ){
        if (requestCode != PERMISSION_REQUEST_CODE) return;

        List<String> deniedPermissions = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (deniedPermissions.isEmpty()) {
            callback.onAllPermissionsGranted();
        } else {
            callback.onPermissionsDenied(deniedPermissions);
        }
    }
}
