package com.inovationware.toolkit.cycles.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.global.domain.Section;

import java.util.Arrays;

public enum Cycle {
    Personal,
    Health,
    Business,
    Soul;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(Cycle.values()).map(Enum::name).toArray(String[]::new);
    }

}
