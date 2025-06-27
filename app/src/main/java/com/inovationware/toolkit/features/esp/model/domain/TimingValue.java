package com.inovationware.toolkit.features.esp.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum TimingValue {
    One,
    Two,
    Three;

    public static String to(TimingValue value) {
        return value == One ?
                "1s" :
                value == Two ?
                        "2s" :
                        "3s";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(TimingValue.values()).map(Enum::name).toArray(String[]::new);
    }

}
