package com.inovationware.toolkit.features.esp.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum LogLevel {
    DEBUG;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(LogLevel.values()).map(Enum::name).toArray(String[]::new);
    }
}
