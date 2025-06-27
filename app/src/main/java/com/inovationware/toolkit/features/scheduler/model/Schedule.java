package com.inovationware.toolkit.features.scheduler.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Schedule {
    private String title;
    private String description;
    private String location;
    private int year;
    private int month;
    private int day;
    private Recurrence recurrence;
    private int hour;
    private int minute;

    public static Schedule create(String title, String description, String location, int year, int month, int day, Recurrence recurrence, int hour, int minute) {
        return Schedule.builder()
                .title(title)
                .description(description)
                .location(location)
                .year(year)
                .month(month)
                .day(day)
                .recurrence(recurrence)
                .hour(hour)
                .minute(minute)
                .build();
    }

    public static Schedule create(String title, String description, int year, int month, int day, Recurrence recurrence, int hour, int minute) {
        return create(title, description, "", year, month, day, recurrence, hour, minute);
    }

    public enum Recurrence{
        WEEKLY,
        YEARLY,
        MONTHLY,
        DAILY
    }

}
