package com.inovationware.toolkit.common.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringFunctions {
    private static StringFunctions instance;
    public static StringFunctions getInstance(){
        if(instance == null) instance = new StringFunctions();
        return instance;
    }

    public String firstWord(String s){
        return com.inovationware.generalmodule.General.firstWord(s);
    }
}
