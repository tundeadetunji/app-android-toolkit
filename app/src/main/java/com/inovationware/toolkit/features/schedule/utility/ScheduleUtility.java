package com.inovationware.toolkit.features.schedule.utility;

public class ScheduleUtility {
    private static ScheduleUtility instance;

    public static ScheduleUtility getInstance() {
        if (instance == null) instance = new ScheduleUtility();
        return instance;
    }

    private ScheduleUtility() {
    }

    public String[] hours() {
        String[] result = new String[12];
        for (int i = 0; i < 12; i++) {
            result[i] = i + 1 + "";
        }
        return result;
    }

    public String[] minutes() {
        String[] result = new String[60];
        for (int i = 0; i < 60; i++) {
            result[i] = i + 1 < 10 ? "0" + (i + 1) : "" + (i + 1);
        }
        return result;
    }

    public String[] meridian() {
        return new String[]{"am", "pm"};
    }
}
