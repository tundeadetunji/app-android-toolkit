package com.inovationware.toolkit.code.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.codec.language.bm.Lang;

import java.util.Arrays;

public enum Language {
    English,
    Yoruba;
    //Bulgarian;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Language from(String value){
        if(value.isEmpty()) return Language.English;
        return Arrays.stream(values())
                .filter(language -> language.name().equalsIgnoreCase(value))
                .count() > 1 ? Language.valueOf(value) : Language.English;
    }

}
