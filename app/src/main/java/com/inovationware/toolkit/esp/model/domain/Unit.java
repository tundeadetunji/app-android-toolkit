package com.inovationware.toolkit.esp.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum Unit {
    Celsius,
    Percentage;

    public static String to(Unit unit){
        return unit == Celsius ? "°C" : "%";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(Unit.values()).map(Enum::name).toArray(String[]::new);
    }

}
