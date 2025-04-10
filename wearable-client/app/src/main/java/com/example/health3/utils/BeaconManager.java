package com.example.health3.utils;

import android.content.Context;

import com.minew.beaconset.BluetoothState;
import com.minew.beaconset.MinewBeacon;
import com.minew.beaconset.MinewBeaconManager;
import com.minew.beaconset.MinewBeaconManagerListener;

import java.util.List;

public class BeaconManager {
    private static final String TAG = "[HDH] BeaconManager";
    private MinewBeaconManager minewBeaconManager;

    private BeaconScanListener scanListener;

    public interface BeaconScanListener {
        void onBeaconFound(List<MinewBeacon> beacons);
        void onBluetoothStateChanged(BluetoothState state);
        void onAppearBeacons(List<MinewBeacon> beacons);
        void onDisappearBeacons(List<MinewBeacon> beacons);
    }

    public BeaconManager(Context context){
        minewBeaconManager = MinewBeaconManager.getInstance(context);
        minewBeaconManager.setRangeInterval(5*1000); //10초
    }

    public void setBeaconScanListener(BeaconScanListener listener) {
        this.scanListener = listener;
        minewBeaconManager.setMinewbeaconManagerListener(new MinewBeaconManagerListener() {
            @Override
            public void onUpdateBluetoothState(BluetoothState bluetoothState) {
                if (scanListener !=null) {
                    scanListener.onBluetoothStateChanged(bluetoothState);
                }
            }

            @Override
            public void onRangeBeacons(List<MinewBeacon> list) {
                if (scanListener !=null) {
                    scanListener.onBeaconFound(list);
                }
            }

            @Override
            public void onAppearBeacons(List<MinewBeacon> list) {
                if (scanListener !=null) {
                    scanListener.onAppearBeacons(list);
                }
            }

            @Override
            public void onDisappearBeacons(List<MinewBeacon> list) {
                if (scanListener !=null) {
                    scanListener.onDisappearBeacons(list);
                }
            }
        });
    }

    public void startScanning() {
        if (minewBeaconManager != null) {
            minewBeaconManager.startService();
            minewBeaconManager.registerBleChangeBroadcast();
            minewBeaconManager.startScan();
        }
    }

    public void stopScanning() {
        if (minewBeaconManager != null) {
            minewBeaconManager.stopScan();
            minewBeaconManager.stopService();
            minewBeaconManager.unRegisterBleChangeBroadcast();
        }
    }

    public BluetoothState checkBluetoothState() {
        return minewBeaconManager.checkBluetoothState();
    }



}
