package com.inovationware.toolkit.nettimer.model;

import lombok.Getter;

public enum NetTimerToDo {
    READ_OUT_LOUD("Read Out Loud"),
    START_THE_APP("Start The App");

    @Getter
    private final String label;

    NetTimerToDo(String s) {
        this.label = s;
    }

    public static String[] listing(){
        String[] result = new String[NetTimerToDo.values().length];
        for(int i = 0; i < NetTimerToDo.values().length; i++){
            result[i] = NetTimerToDo.values()[i].getLabel();
        }
        return result;
    }
    public static NetTimerToDo fromLabel(String label) {
        for (NetTimerToDo value : NetTimerToDo.values()) {
            if (value.getLabel().equalsIgnoreCase(label)) {
                return value;
            }
        }
        return null; // or throw exception, depending on your use case
    }
}
