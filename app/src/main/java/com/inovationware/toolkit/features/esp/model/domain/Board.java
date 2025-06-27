package com.inovationware.toolkit.features.esp.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum Board {
    esp32dev;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(Board.values()).map(Enum::name).toArray(String[]::new);
    }

}
