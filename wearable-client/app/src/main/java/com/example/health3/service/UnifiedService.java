package com.example.health3.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.health3.utils.BeaconManager;
import com.example.health3.utils.DateUtils;
import com.example.health3.utils.LogUtils;
import com.example.health3.utils.WearOSWebsocketClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.minew.beaconset.BluetoothState;
import com.minew.beaconset.MinewBeacon;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnifiedService extends Service implements SensorEventListener {
    // 상수 정의
    private final String TAG = "[HDH] UnifiedService";
    private static final String CHANNEL_ID = "UnifiedSensorChannel";
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final long HEART_RATE_SAMPLING_US = 9000000000L; // 5초
    private static final int STEP_COUNTER_SAMPLING_US = SensorManager.SENSOR_DELAY_NORMAL;

    // 의존성 주입
    private SensorManager sensorManager; //SensorManager 및 센서 객체
    private Sensor heartRateSensor; //SensorManager 및 센서 객체
    private Sensor stepCounterSensor; //SensorManager 및 센서 객체
    private FusedLocationProviderClient fusedLocationClient;    // 위치 관련
    private LocationCallback locationCallback;    // 위치 관련
    private LocationRequest locationRequest;    // 위치 관련

    // Beacon 관련
    private BeaconManager beaconManager;
    private BeaconManager.BeaconScanListener beaconScanListener;
    

    private WearOSWebsocketClient client;

    private SharedPreferences prefs;

    private NotificationManager notificationManager;

    private String userName;
    private String serial;

    private float initialStepValue = -1;

    // 데이터 관리
    private final Map<String, Object> sensorData = new HashMap<>();
    private long lastUpdateTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("onCreate",this);

        // initialize Components
        initializeSensorManager();
        initializeLocationClient();
        initializeBeaconManager();
        initializePreferences();
        initializeWebSocket();
        initializeNotificationManager();

        // setup data collection
        setupHeartRateSensor();
        setupStepCounter();
        setupLocationUpdates();

        // start foreground service
        startForegroundService();

//        // 여기서는 권한이 이미 부여되었다고 가정합니다.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        //        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return;
//        }
    }

    private void initializeSensorManager() {
        // SensorManager 초기화 및 센서 객체 획득
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            LogUtils.d("SensorManager initialization failed",this);
            stopSelf();
        }
    }

    private void initializeLocationClient() {
        // 위치 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // 위치 요청 설정 (예: 3초 간격)
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                .setMinUpdateIntervalMillis(1000L)
                .setMaxUpdateDelayMillis(3000L)
                .build();
    }

    private void initializeBeaconManager() {
        LogUtils.d("initializeBeaconManager",this);
        beaconManager = new BeaconManager(this);
        beaconScanListener = new BeaconManager.BeaconScanListener() {
            @Override
            public void onBeaconFound(List<MinewBeacon> beacons) {
                // 비콘 데이터 처리
                processBeaconData(beacons);
            }

            @Override
            public void onBluetoothStateChanged(BluetoothState state) {
                // 블루투스 상태 변경 처리
                handleBluetoothStateChange(state);
            }

            @Override
            public void onAppearBeacons(List<MinewBeacon> beacons) {
                // 새로 발견된 비콘 처리
                handleNewBeacons(beacons);
            }

            @Override
            public void onDisappearBeacons(List<MinewBeacon> beacons) {
                // 사라진 비콘 처리
                handleDisappearedBeacons(beacons);
            }
        };
        beaconManager.setBeaconScanListener(beaconScanListener);
        startBeaconScanning();  // 서비스 시작 시 스캔 시작
    }

    private void handleBluetoothStateChange(BluetoothState state) {
        // 블루투스 상태 변경 처리 로직
        switch (state) {
            case BluetoothStatePowerOff:
                Log.d(TAG, "Bluetooth turned off");
                break;
            case BluetoothStatePowerOn:
                Log.d(TAG, "Bluetooth turned on");
                startBeaconScanning();
                break;
            case BluetoothStateNotSupported:
                Log.d(TAG, "Bluetooth not supported");
                break;
        }
    }

    private void processBeaconData(List<MinewBeacon> beacons) {
        // 비콘 데이터 처리 로직
        beacons.stream()
                .filter(i->i.getRssi() > -50)
                .forEach(beacon-> {
//                        Log.d(TAG, "Beacon found: " + beacon.getName() +
//                                " Major: " + beacon.getMajor() +
//                                " Minor: " + beacon.getMinor() +
//                                " RSSI: " + beacon.getRssi() +
//                                " Battery: " + beacon.getBattery());
                    sensorData.put("type","message");
                    sensorData.put("sensor","beacon");
                    Map beaconData = new HashMap();
                    beaconData.put("deviceName",beacon.getName());
                    beaconData.put("major",beacon.getMajor());
                    beaconData.put("minor",beacon.getMinor());
                    beaconData.put("rssi",beacon.getRssi());
                    sensorData.put("value",beaconData);
                    sensorData.put("time", DateUtils.dateTime());
                    sensorData.put("sender",userName);
                    sensorData.put("serial",serial);
                    client.sendData(sensorData);
                });


    }

    private void handleNewBeacons(List<MinewBeacon> beacons) {
        // 새로 발견된 비콘 처리 로직
        beacons.stream()
                .filter(i->i.getRssi() > -50)
                .forEach(beacon->
                        Log.d(TAG, "Beacon new found: " + beacon.getName() +
                                " Major: " + beacon.getMajor() +
                                " Minor: " + beacon.getMinor() +
                                " RSSI: " + beacon.getRssi() +
                                " Battery: " + beacon.getBattery()));
    }

    private void handleDisappearedBeacons(List<MinewBeacon> beacons) {
        // 사라진 비콘 처리 로직
        beacons.stream()
                .filter(i->i.getRssi() > -50)
                .forEach(beacon->
                        Log.d(TAG, "Beacon disappeared: " + beacon.getName() +
                                " Major: " + beacon.getMajor() +
                                " Minor: " + beacon.getMinor() +
                                " RSSI: " + beacon.getRssi() +
                                " Battery: " + beacon.getBattery()));
    }

    public void startBeaconScanning() {
        if (beaconManager != null) {
            beaconManager.startScanning();
        }
    }
    public void stopBeaconScanning() {
        if (beaconManager != null) {
            beaconManager.stopScanning();
        }
    }


    private void initializePreferences() {
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userName = prefs.getString("userName","non-value");
        serial = prefs.getString("SERIAL","XXX");
    }

    private void initializeWebSocket() {
        client = new WearOSWebsocketClient();
        client.start();
    }

    private void initializeNotificationManager() {
        // create notification manager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "통합 센서 서비스 채널",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupHeartRateSensor() {
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor,
                    (int)HEART_RATE_SAMPLING_US);
        } else {
            Log.e(TAG, "Heart rate sensor not available");
        }
    }

    private void setupStepCounter() {
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor,
                    SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.e(TAG, "Step counter sensor not available");
        }
    }

    private void setupLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    handleLocationData(location);
                }
            }
        };


        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(TAG, "Location updates failed: " + e.getMessage());
        }
    }



    // 포그라운드 서비스 알림 설정
    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("통합 센서 서비스")
                .setContentText("GPS, 심박수, 걸음 수 데이터를 수집 중입니다.")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 강제 종료되었을 때 자동 재시작
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        fusedLocationClient.removeLocationUpdates(locationCallback);
        stopBeaconScanning();
        LogUtils.d("서비스 종료 및 센서/위치 업데이트 중지",this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < 5000) {
            return;
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                handleHeartRateData(event);
                break;
            case Sensor.TYPE_STEP_COUNTER:
                handleStepCounterData(event);
                break;
        }
        lastUpdateTime = currentTime;
    }

    private void handleHeartRateData(SensorEvent event) {
        float heartRate = event.values[0];
        sensorData.put("type","message");
        sensorData.put("sensor","bpm");
        sensorData.put("value",heartRate);
        sensorData.put("time", DateUtils.dateTime());
        sensorData.put("sender",userName);
        sensorData.put("serial",serial);
        client.sendData(sensorData);
        //LogUtils.d("Heart Rate: " + heartRate + " bpm", this);
        //sendDataToServer();
    }

    //private float stepsToday = 0;
    private void handleStepCounterData(SensorEvent event) {
        float steps = event.values[0];
        long timestamp = event.timestamp;

        if (initialStepValue < 0) {
            initialStepValue = steps;
        }
        float stepsToday = steps - initialStepValue;
        //stepsToday +=1;
        sensorData.put("type","message");
        sensorData.put("sensor","step");
        sensorData.put("value",stepsToday);
        sensorData.put("time", DateUtils.dateTime());
        sensorData.put("sender",userName);
        sensorData.put("serial",serial);
        client.sendData(sensorData);
        LogUtils.d("Step Counter: " + stepsToday, this);
        initialStepValue = steps;
    }

    private void handleLocationData(Location location) {
        Map locationMap = new HashMap();
        locationMap.put("lat",location.getLatitude());
        locationMap.put("lng",location.getLongitude());
        locationMap.put("acc",location.getAccuracy());
        sensorData.put("type","message");
        sensorData.put("sensor","gps");
        sensorData.put("value",locationMap);
        sensorData.put("time", DateUtils.dateTime());
        sensorData.put("sender",userName);
        sensorData.put("serial",serial);
        client.sendData(sensorData);
        //LogUtils.d("Location - Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude(),UnifiedService.this);
        //sendDataToServer();
    }

    private void sendDataToServer() {
        try {
            JSONObject data = new JSONObject(sensorData);
            data.put("timestamp", DateUtils.dateTime());
            client.sendData(data.toString());
        } catch (Exception e) {
            Log.e(TAG,"Failed to send Data", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        LogUtils.d("onAccuracyChanged ",this);
    }
}
