package com.example.health3.utils;

import android.content.Context;
import android.util.Log;

public class LogUtils {

    public static void d(String msg, Context context) {
        Log.d("[HDH] "+context.getClass().getSimpleName(),msg);
    }
}
