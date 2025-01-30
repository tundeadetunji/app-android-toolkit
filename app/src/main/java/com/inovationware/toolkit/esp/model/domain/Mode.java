package com.inovationware.toolkit.esp.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum Mode {
    INPUT_PULLUP;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(Mode.values()).map(Enum::name).toArray(String[]::new);
    }

}
