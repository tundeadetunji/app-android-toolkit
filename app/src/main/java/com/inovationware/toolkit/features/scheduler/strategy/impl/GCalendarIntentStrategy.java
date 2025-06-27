package com.inovationware.toolkit.features.scheduler.strategy.impl;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.inovationware.toolkit.features.scheduler.model.Schedule;
import com.inovationware.toolkit.features.scheduler.strategy.SchedulerStrategy;

import java.util.Calendar;

public class GCalendarIntentStrategy implements SchedulerStrategy {

    private static GCalendarIntentStrategy instance;
    public static GCalendarIntentStrategy getInstance(Context context){
        if(instance == null) instance = new GCalendarIntentStrategy(context);
        return instance;
    }

    private GCalendarIntentStrategy(Context context){
        this.context = context;
    }

    private Context context;


    /*
    In this example, the recurrence rule is set to FREQ=YEARLY;BYMONTH=3;BYMONTHDAY=15, which means the event will repeat every year on March 15th.

    Note that the BYMONTH value is set to 3 because the Calendar class uses 0-based indexing for months, so March is represented by the value 2. However, the BYMONTH value in the recurrence rule uses 1-based indexing, so we need to add 1 to the month value.
     */

    @Override
    public void createSchedule(Schedule schedule) {
        if (schedule.getRecurrence() == Schedule.Recurrence.YEARLY) createYearlySchedule(schedule);
        else if (schedule.getRecurrence() == Schedule.Recurrence.WEEKLY) createWeeklySchedule(schedule);
    }
    private void createWeeklySchedule(Schedule schedule) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, schedule.getHour());
        startCalendar.set(Calendar.MINUTE, schedule.getMinute());
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.set(Calendar.DAY_OF_MONTH, schedule.getDay());
        startCalendar.set(Calendar.MONTH, schedule.getMonth() - 1);
        startCalendar.set(Calendar.YEAR, schedule.getYear());

        // Get the start time in milliseconds
        long startTimeInMillis = startCalendar.getTimeInMillis();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, schedule.getHour());
        endCalendar.set(Calendar.MINUTE, schedule.getMinute() + 10);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.set(Calendar.DAY_OF_MONTH, schedule.getDay());
        startCalendar.set(Calendar.MONTH, schedule.getMonth() - 1);
        startCalendar.set(Calendar.YEAR, schedule.getYear());

        // Get the start time in milliseconds
        long endTimeInMillis = endCalendar.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(Uri.parse("content://com.android.calendar/events"));
        intent.putExtra("title", schedule.getTitle());
        intent.putExtra("description", schedule.getDescription());
        intent.putExtra("beginTime", startTimeInMillis); // start time in milliseconds
        intent.putExtra("endTime", endTimeInMillis); // end time in milliseconds
        intent.putExtra("rrule", "FREQ=WEEKLY"); // recurring rule (every week)
        //intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        if (schedule.getLocation() != null) intent.putExtra("eventLocation", schedule.getLocation());
        // Start the intent
        startActivity(context, intent, null);
    }
    private void createYearlySchedule(Schedule schedule) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 7);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.set(Calendar.DAY_OF_MONTH, schedule.getDay());
        startCalendar.set(Calendar.MONTH, schedule.getMonth() - 1);
        startCalendar.set(Calendar.YEAR, schedule.getYear());

        // Get the start time in milliseconds
        long startTimeInMillis = startCalendar.getTimeInMillis();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 9);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.set(Calendar.DAY_OF_MONTH, schedule.getDay());
        startCalendar.set(Calendar.MONTH, schedule.getMonth() - 1);
        startCalendar.set(Calendar.YEAR, schedule.getYear());

        // Get the start time in milliseconds
        long endTimeInMillis = endCalendar.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(Uri.parse("content://com.android.calendar/events"));
        intent.putExtra("title", schedule.getTitle());
        intent.putExtra("description", schedule.getDescription());
        intent.putExtra("beginTime", startTimeInMillis); // start time in milliseconds
        intent.putExtra("endTime", endTimeInMillis); // end time in milliseconds
        intent.putExtra("rrule", "FREQ=YEARLY"); // recurring rule (every year)
        //intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        if (schedule.getLocation() != null) intent.putExtra("eventLocation", schedule.getLocation());
        // Start the intent
        startActivity(context, intent, null);
    }

}
