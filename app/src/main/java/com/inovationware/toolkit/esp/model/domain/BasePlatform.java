package com.inovationware.toolkit.esp.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum BasePlatform {
    ESP32;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(BasePlatform.values()).map(Enum::name).toArray(String[]::new);
    }

}

