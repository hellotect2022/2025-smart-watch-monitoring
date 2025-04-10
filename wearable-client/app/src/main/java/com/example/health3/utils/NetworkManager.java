package com.example.health3.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private String TAG="[HDH] NetworkManager";
    private OkHttpClient client;


    public void init() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .build();
    }

    public interface VerifyCallback {
        void onSuccess(JSONObject result) throws JSONException;
        void onFailure(String error);
    }

    public void post(String url, JSONObject data, VerifyCallback callback) {
        JSONObject json = data;

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                callback.onFailure("서버 연결 실패");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server error: " + response.code());
                    callback.onFailure("서버에러 응답코드 ("+response.code()+")");
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String status = jsonResponse.getString("status");

                    if (!"SUCCESS".equals(status)) {
                        Log.d(TAG, "Response failed with status: " + status);
                        callback.onFailure("Response failed with status: " + status);
                        return;
                    }

                    JSONObject data = jsonResponse.getJSONObject("data");
                    callback.onSuccess(data);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    callback.onFailure("서버 응답 처리 실패");
                }
            }
        });
    }

    public static NetworkManager getInstance() {
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        public static final NetworkManager INSTANCE = new NetworkManager();
    }

}
