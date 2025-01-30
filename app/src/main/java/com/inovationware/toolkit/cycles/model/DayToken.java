package com.inovationware.toolkit.cycles.model;

import com.inovationware.toolkit.cycles.model.domain.DayPeriod;

import java.util.List;

import lombok.Getter;

public final class DayToken {
    @Getter
    private final String headline;
    @Getter
    private final String detail;
    @Getter
    private final DayPeriod period;
    @Getter
    private final List<String> periodTimes;
    @Getter
    private final String summary;

    private DayToken(String headline, String detail, DayPeriod period, List<String> periodTimes, String summary) {
        this.headline = headline;
        this.detail = detail;
        this.period = period;
        this.periodTimes = periodTimes;
        this.summary = summary;
    }

    public static DayToken create(String headline, String detail, DayPeriod period, List<String> periodTimes, String summary) {
        return new DayToken(headline, detail, period, periodTimes, summary);
    }
}
