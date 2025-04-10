package com.example.health3.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    // yyyy-MM-dd HH:mm:ss.SSSSSS
    public static String dateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime.now().format(formatter);
        return LocalDateTime.now().format(formatter);
    }
}
