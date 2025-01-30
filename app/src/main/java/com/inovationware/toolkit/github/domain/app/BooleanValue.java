package com.inovationware.toolkit.github.domain.app;

public enum BooleanValue {
    Yes,
    No;

    public static boolean to(BooleanValue value){
        return value == BooleanValue.Yes ? true : false;
    }

    public static BooleanValue from(boolean value){
        return value ? BooleanValue.Yes : BooleanValue.No;
    }

    public static boolean to(String value){
        return to(BooleanValue.valueOf(value));
    }

    public static String[] listing(){
        String[] result = new String[BooleanValue.values().length];
        for(int i = 0; i < BooleanValue.values().length; i++){
            result[i] = BooleanValue.values()[i].name();
        }
        return result;
    }


}
