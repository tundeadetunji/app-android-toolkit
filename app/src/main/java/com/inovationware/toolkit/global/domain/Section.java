package com.inovationware.toolkit.global.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum Section {
    Summary,
    Detail,
    Yes,
    No,
    Maybe;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String[] listing(){
        return Arrays.stream(Section.values()).map(Enum::name).toArray(String[]::new);
    }


}
