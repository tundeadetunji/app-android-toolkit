package com.inovationware.toolkit.scheduler.service.impl;

import com.inovationware.toolkit.scheduler.model.Schedule;
import com.inovationware.toolkit.scheduler.service.SchedulerService;
import com.inovationware.toolkit.scheduler.strategy.SchedulerStrategy;

public class GCalendarIntentService implements SchedulerService {
    private static GCalendarIntentService instance;
    private final SchedulerStrategy strategy;
    public static GCalendarIntentService getInstance(SchedulerStrategy strategy){
        if(instance == null) instance = new GCalendarIntentService(strategy);
        return instance;
    }
    private GCalendarIntentService(SchedulerStrategy strategy){
        this.strategy = strategy;
    }

    @Override
    public void createSchedule(Schedule schedule) {
        strategy.createSchedule(schedule);
    }
}
