package com.example.health3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.health3.R;
import com.example.health3.utils.LogUtils;
import com.example.health3.utils.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {
    private EditText serialInput;
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile);

        serialInput = findViewById(R.id.serial_input);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {
            String serial = serialInput.getText().toString();

            verifySerialWithServer(serial);
        });

        // a.1. sharedPreferences 에서 사용자 시리얼 넘버 확인
        String serial = shp.getString(KEY_SERIAL,null);
        LogUtils.d("shared serial : "+serial,this);

        if (serial != null) {
            verifySerialWithServer(serial);
        }
    }

    private void verifySerialWithServer(String serial) {
        try {
            LogUtils.d(String.valueOf("verifySerialWithServer"),RegisterActivity.this);

            String apiUrl = "http://192.168.10.218:8080/api/verifyUserDevice";

            // OkHttp 사용 예시
            JSONObject data = new JSONObject();
            data.put("serial",serial);

            networkManager.post(apiUrl,data, new NetworkManager.VerifyCallback() {

                @Override
                public void onSuccess(JSONObject result) throws JSONException {
                    SharedPreferences.Editor editor = shp.edit();
                    String userName = result.getString("name");
                    boolean isAdmin = result.getBoolean("admin");
                    editor.putString("userName", userName);
                    editor.putBoolean("isAdmin", isAdmin);
                    editor.putString(KEY_SERIAL, serial);
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this,
                                "환영합니다, " + userName + "님",
                                Toast.LENGTH_SHORT).show();
                    });

                    // 필요한 추가 작업 수행
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
