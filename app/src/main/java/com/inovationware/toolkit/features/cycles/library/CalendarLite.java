package com.inovationware.toolkit.features.cycles.library;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class CalendarLite {
    private static CalendarLite instance;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static CalendarLite getInstance(){
        if(instance == null) instance = new CalendarLite();
        return instance;
    }

    private final Map<Month, Integer> integerMonthMap;
    public final String[] MONTH_LISTING; //0-based
    public final Integer[] DAY_LISTING;
    public final String[] WEEK_DAY_LISTING;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private CalendarLite(){
        integerMonthMap = createIntegerMonthMap();
        MONTH_LISTING = createMonthListing();
        DAY_LISTING = createDayListing();
        WEEK_DAY_LISTING = createWeekDayListing();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Map<Month, Integer > createIntegerMonthMap(){
        Map<Month, Integer> result = new HashMap<>();
        result.put(Month.JANUARY,1);
        result.put(Month.FEBRUARY,2);
        result.put(Month.MARCH,3);
        result.put(Month.APRIL,4);
        result.put( Month.MAY,5);
        result.put(Month.JUNE,6);
        result.put(Month.JULY, 7);
        result.put(Month.AUGUST, 8);
        result.put(Month.SEPTEMBER, 9);
        result.put(Month.OCTOBER, 10);
        result.put(Month.NOVEMBER, 11);
        result.put(Month.DECEMBER, 12);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String[] createMonthListing(){
        String[] result = new String[12];
        result[0] = Month.JANUARY.name();
        result[1] = Month.FEBRUARY.name();
        result[2] = Month.MARCH.name();
        result[3] = Month.APRIL.name();
        result[4] = Month.MAY.name();
        result[5] = Month.JUNE.name();
        result[6] = Month.JULY.name();
        result[7] = Month.AUGUST.name();
        result[8] = Month.SEPTEMBER.name();
        result[9] = Month.OCTOBER.name();
        result[10] = Month.NOVEMBER.name();
        result[11] = Month.DECEMBER.name();
        return result;
    }

    private Integer[] createDayListing(){
        Integer[] result = new Integer[31];
        for(int i = 0; i < 31; i++){
            result[i] = i + 1;
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String[] createWeekDayListing(){
        String[] result = new String[7];
        result[0] = DayOfWeek.MONDAY.name();
        result[1] = DayOfWeek.TUESDAY.name();
        result[2] = DayOfWeek.WEDNESDAY.name();
        result[3] = DayOfWeek.THURSDAY.name();
        result[4] = DayOfWeek.FRIDAY.name();
        result[5] = DayOfWeek.SATURDAY.name();
        result[6] = DayOfWeek.SUNDAY.name();
        return result;
    }
    public int getIntFromMonth(String month){
        return integerMonthMap.get(Month.valueOf(month));
    }

}
