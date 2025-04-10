package com.example.health3.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WearOSWebsocketClient {
    private String TAG = "[HDH] Websocket";
    private OkHttpClient client;
    private WebSocket webSocket;

    private ObjectMapper objectMapper;

    public WearOSWebsocketClient() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Websocket 연결 시작
     */

    public void start() {
        Request request = new Request.Builder()
                .url("ws://192.168.10.218:8080/ws/echo")
                .build();

        webSocket = client.newWebSocket(request, new MyWebSocketListener());
    }

    /**
     * 데이터를 WebSocket으로 전송
     *
     * @param message 전송할 메시지
     */
    public void sendData(Object message) {
        try {
            if (webSocket != null) {
                webSocket.send(objectMapper.writeValueAsString(message));
            }
        }catch (JsonProcessingException e){
            Log.d(TAG,"json parsing error",e);
        }
    }

    public void stop() {
        if (webSocket != null) {
            Log.d(TAG,"Websocket 을 종료합니다. ");
            webSocket.close(1000,"종료합니다.");
        }
        if (client !=null) {
            client.dispatcher().executorService().shutdown();
        }
    }

    /**
     * Websocket 이벤트 리스너 구현
     */

    private final class MyWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            Log.d(TAG, "WebSocket 연결이 열렸습니다.");
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            Log.d(TAG, "서버로부터 받은 메시지: " + text);
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            Log.d(TAG, "서버로부터 받은 바이너리 메시지: " + bytes.hex());
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            Log.d(TAG,"WebSocket 연결 종료 중: " + code + " / " + reason);
            webSocket.close(1000,null);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            Log.d(TAG, "WebSocket 연결 실패",t);
        }

    }
}

