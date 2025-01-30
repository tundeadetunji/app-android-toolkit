package com.inovationware.toolkit.cycles.model.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DayPeriod {
    A,
    B,
    C,
    D,
    E,
    F,
    G;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String[] listingSorted(){
        List<DayPeriod> temp = new ArrayList<>();
        switch (LocalDate.now().getDayOfWeek()){
            case SUNDAY:
                temp = List.of(G, A, B, C, D, E, F);
                break;
            case MONDAY:
                temp = List.of(C, D, E, F, G, A, B);
                break;
            case TUESDAY:
                temp = List.of(F, G, A, B, C, D, E);
                break;
            case WEDNESDAY:
                temp = List.of(B, C, D, E, F, G, A);
                break;
            case THURSDAY:
                temp = List.of(E, F, G, A, B, C, D);
                break;
            case FRIDAY:
                temp = List.of(A, B, C, D, E, F, G);
                break;
            case SATURDAY:
                temp = List.of(D, E, F, G, A, B, C);
                break;
        }

        return temp.stream().map(Enum::name).toArray(String[]::new);
    }
    public static String[] listingSortedByDay(DayOfWeek day){
        List<DayPeriod> temp = new ArrayList<>();
        switch (day){
            case SUNDAY:
                temp = List.of(G, A, B, C, D, E, F);
                break;
            case MONDAY:
                temp = List.of(C, D, E, F, G, A, B);
                break;
            case TUESDAY:
                temp = List.of(F, G, A, B, C, D, E);
                break;
            case WEDNESDAY:
                temp = List.of(B, C, D, E, F, G, A);
                break;
            case THURSDAY:
                temp = List.of(E, F, G, A, B, C, D);
                break;
            case FRIDAY:
                temp = List.of(A, B, C, D, E, F, G);
                break;
            case SATURDAY:
                temp = List.of(D, E, F, G, A, B, C);
                break;
        }

        return temp.stream().map(Enum::name).toArray(String[]::new);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String[] listing(){
        return Arrays.stream(DayPeriod.values()).map(Enum::name).toArray(String[]::new);
    }

    public static String createRefreshHeadline(DayPeriod period){
        return "Currently in the " + period.name() + " period.";
    }

    public static String createSearchHeadline(DayPeriod period){
        return HEADLINE_PREFIX + period.name() + HEADLINE_SUFFIX;
    }

    public static String to(DayPeriod period){
        return period.name() + " period";
    }

    public static final String HEADLINE_PREFIX = "The ";
    public static final String HEADLINE_SUFFIX = " period.";
}
