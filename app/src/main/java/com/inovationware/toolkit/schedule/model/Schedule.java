package com.inovationware.toolkit.schedule.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inovationware.toolkit.global.library.external.Json;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule {
    @JsonProperty
    private List<String> apps;
    @JsonProperty
    private String announce;
    @JsonProperty
    private String time;
    @JsonProperty
    private Map<DayOfWeek, Boolean> days = new HashMap<>();
    @JsonProperty
    private int timeoutValue;
    @JsonProperty
    private boolean timeout;

    private Schedule(List<String> apps, String announce, String time, Map<DayOfWeek, Boolean> days, int timeoutValue, boolean timeout){
        this.apps = apps;
        this.announce = announce;
        this.time = time;
        this.days = days;
        this.timeoutValue = timeoutValue;
        this.timeout = timeout;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Schedule create(ScheduleViewSource src){
        Map<DayOfWeek, Boolean> days = new HashMap<>();
        days.put(DayOfWeek.MONDAY, src.getMonCheckbox().isChecked());
        days.put(DayOfWeek.TUESDAY, src.getTueCheckbox().isChecked());
        days.put(DayOfWeek.WEDNESDAY, src.getWedCheckbox().isChecked());
        days.put(DayOfWeek.THURSDAY, src.getThuCheckbox().isChecked());
        days.put(DayOfWeek.FRIDAY, src.getFriCheckbox().isChecked());
        days.put(DayOfWeek.SATURDAY, src.getSatCheckbox().isChecked());
        days.put(DayOfWeek.SUNDAY, src.getSunCheckbox().isChecked());

        return new Schedule(
                src.getApps(),
                src.getAnnounceTextView().getText().toString(),
                src.getHourDropDown().getText().toString() + ":" + src.getMinuteDropDown().getText().toString() + " " + src.getMeridianDropDown().getText().toString().toLowerCase(),
                days,
                !src.getTimeoutTextView().getText().toString().isEmpty() ? Integer.parseInt(src.getTimeoutTextView().getText().toString()) : 0,
                src.getTimeoutCheckbox().isChecked()
        );
    }
    public static String to(Schedule schedule) throws IOException {
        return Json.from(schedule);
    }

    private Schedule(){}

}
