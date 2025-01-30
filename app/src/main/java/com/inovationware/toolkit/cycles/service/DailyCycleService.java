package com.inovationware.toolkit.cycles.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.cycles.model.domain.DayPeriod;
import com.inovationware.toolkit.cycles.model.DayToken;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DailyCycleService {
    private static DailyCycleService instance;

    public static DailyCycleService getInstance() {
        if (instance == null) instance = new DailyCycleService();
        return instance;
    }

    private DailyCycleService() {
        initializePeriodDetailMap();
        initializePeriodTimes();
        initializePeriodSummaryMap();
    }

    private final String space = " ";
    private final String sunday = "Sunday";
    private final String monday = "Monday";
    private final String tuesday = "Tuesday";
    private final String wednesday = "Wednesday";
    private final String thursday = "Thursday";
    private final String friday = "Friday";
    private final String saturday = "Saturday";
    private List<String> keywords;
    private List<String> aPeriods = new ArrayList<>();
    private List<String> bPeriods = new ArrayList<>();
    private List<String> cPeriods = new ArrayList<>();
    private List<String> dPeriods = new ArrayList<>();
    private List<String> ePeriods = new ArrayList<>();
    private List<String> fPeriods = new ArrayList<>();
    private List<String> gPeriods = new ArrayList<>();
    private Map<DayPeriod, String> periodDetailMap = new HashMap<>();
    private Map<DayPeriod, String> periodSummaryMap = new HashMap<>();


    /**
     * Called by the Search button.
     *
     * @param keyword
     * @return List of DayPeriod values
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> periodsFromKeyword(String keyword) {
        return periodDetailMap.keySet().stream().filter(key -> periodDetailMap.get(key).contains(keyword))
                .map(match -> match.name())
                .collect(Collectors.toList());
    }

    public List<String> timesFromPeriod(DayPeriod period){
        switch (period){
            case A:
                return aPeriods;
            case B:
                return bPeriods;
            case C:
                return cPeriods;
            case D:
                return dPeriods;
            case E:
                return ePeriods;
            case F:
                return fPeriods;
            default:
                return gPeriods;
        }
    }

    /**
     * Easily succeeds findByKeyword(), whose return value is used directly. Called by the Load button.
     *
     * @param period
     * @return
     */
    public DayToken findByPeriod(DayPeriod period) {
        return DayToken.create(DayPeriod.createSearchHeadline(period), periodDetailMap.get(period), period, timesFromPeriod(period), periodSummaryMap.get(period));
    }

    public DayToken findByPeriod() {
        DayPeriod period = getCurrentPeriod();
        return DayToken.create(DayPeriod.createSearchHeadline(period), periodDetailMap.get(period), period, timesFromPeriod(period), periodSummaryMap.get(period));
    }

    public List<String> getKeywords() {
        if (keywords == null) keywords = generateKeywords();
        return keywords;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String[] periodListing(boolean sorted) {
        if (sorted) return DayPeriod.listingSorted();
        return DayPeriod.listing();
    }

    private List<String> generateKeywords() {
        List<String> result = new ArrayList<>();
        result.add("marital");
        result.add("marriage");
        result.add("contract");
        result.add("meditate");
        result.add("meditation");
        result.add("favors from persons in high positions");
        result.add("favors");
        result.add("dealing with public officials");
        result.add("persons of high rank");
        result.add("writing of important letters");
        result.add("purpose of building up personal credit");
        result.add("credit of a business");
        result.add("start a new business");
        result.add("new plan");
        result.add("new proposition");
        result.add("livestock");
        result.add("signing of contracts");
        result.add("agreements");
        result.add("short journeys");
        result.add("marry");
        result.add("courting");
        result.add("court");
        result.add("loan money");
        result.add("move into a new location");
        result.add("building");
        result.add("financial investment");
        result.add("surgical operations");
        result.add("art");
        result.add("music");
        result.add("sensual ");
        result.add("undertaking");
        result.add("drama");
        result.add("dealing with the public");
        result.add("public");
        result.add("hiring");
        result.add("short journey");
        result.add("recreation");
        result.add("function");
        result.add("ball");
        result.add("social");
        result.add("women");
        result.add("dealing with women");
        result.add("long journey");
        result.add("scientific research");
        result.add("research");
        result.add("education");
        result.add("campaign");
        result.add("study");
        result.add("legal");
        result.add("writing");
        result.add("speaking");
        result.add("contracts");
        result.add("literary");
        result.add("newspaper");
        result.add("advertising");
        result.add("advert");
        result.add("medicine");
        result.add("lend");
        result.add("lend money");
        result.add("borrow");
        result.add("chance");
        result.add("tricky");
        result.add("questionable");
        result.add("signing");
        result.add("sign");
        result.add("important papers");
        result.add("papers");
        result.add("paper");
        result.add("important letter");
        result.add("enemies");
        result.add("court");
        result.add("argument");
        result.add("lawyer");
        result.add("invention");
        result.add("mechanical");
        result.add("public official");
        result.add("official");
        result.add("prominent person");
        result.add("prominent");
        result.add("real estate");
        result.add("favor");
        result.add("surgeon");
        result.add("deceitful"); // C
        result.add("mislead"); // C
        result.add("guard"); //c
        result.add("guard yourself"); //c
        result.add("planting");
        result.add("plant");
        result.add("marine");
        result.add("shipping");
        result.add("ship");
        result.add("transportation");
        result.add("transport");
        result.add("sell");
        result.add("with women");
        result.add("new");
        result.add("lawsuit");
        result.add("money matters");
        result.add("speculate");
        result.add("plea");
        result.add("request");
        result.add("personal");
        result.add("social");
        result.add("judge");
        result.add("referee");
        result.add("magistrate");
        result.add("police");
        result.add("senator");
        result.add("governor");
        result.add("mayor");
        result.add("president");
        result.add("authorities");
        result.add("authorit");
        result.add("sales promotion");
        result.add("sale");
        result.add("promotion");
        result.add("submission of brief");
        result.add("metallurgy");
        result.add("metal");
        result.add("scientific");
        result.add("agreement");
        result.add("journey");
        result.add("erect");
        result.add("risk");
        result.add("recreation");
        result.add("start");
        result.add("stock market");
        result.add("letter");
        result.add("cattle");
        result.add("account");
        result.add("collecting account");
        result.add("raising money");
        result.add("educational");
        result.add("plan");
        result.add("meeting");
        result.add("promotion");
        result.add("seeking promotion in business");
        result.add("trade");
        result.add("credit");
        result.add("for women");
        result.add("speculation");
        result.add("speculat");
        result.add("important letter");
        result.add("for men");
        result.add("men");
        result.add("permanency");
        result.add("permanen");
        result.add("collecting of money");
        result.add("collect");
        result.add("solicit");
        result.add("beneficent matters");
        result.add("receipt");
        result.add("humanitarian");
        result.add("accident");
        result.add("hazard");
        result.add("illness");
        result.add("fevers");
        result.add("banker");
        result.add("bank");
        result.add("financier");
        result.add("financ");
        result.add("loan");

        Collections.sort(result);

        return result;
    }

    private void initializePeriodDetailMap() {
        periodDetailMap.put(DayPeriod.A, a);
        periodDetailMap.put(DayPeriod.B, b);
        periodDetailMap.put(DayPeriod.C, c);
        periodDetailMap.put(DayPeriod.D, d);
        periodDetailMap.put(DayPeriod.E, e);
        periodDetailMap.put(DayPeriod.F, f);
        periodDetailMap.put(DayPeriod.G, g);
    }
    private void initializePeriodSummaryMap() {
        periodSummaryMap.put(DayPeriod.A, aSummary);
        periodSummaryMap.put(DayPeriod.B, bSummary);
        periodSummaryMap.put(DayPeriod.C, cSummary);
        periodSummaryMap.put(DayPeriod.D, dSummary);
        periodSummaryMap.put(DayPeriod.E, eSummary);
        periodSummaryMap.put(DayPeriod.F, fSummary);
        periodSummaryMap.put(DayPeriod.G, gSummary);
    }

    private DayPeriod onSunday(int slot) {
        switch (slot){
            case 1:
                return DayPeriod.G;
            case 2:
                return DayPeriod.A;
            case 3:
                return DayPeriod.B;
            case 4:
                return DayPeriod.C;
            case 5:
                return DayPeriod.D;
            case 6:
                return DayPeriod.E;
        }
        return DayPeriod.F;
    }

    private DayPeriod onMonday(int slot) {
        switch (slot){
            case 1:
                return DayPeriod.C;
            case 2:
                return DayPeriod.D;
            case 3:
                return DayPeriod.E;
            case 4:
                return DayPeriod.F;
            case 5:
                return DayPeriod.G;
            case 6:
                return DayPeriod.A;
        }
        return DayPeriod.B;
    }
    private DayPeriod onTuesday(int slot) {
        switch (slot){
            case 1:
                return DayPeriod.F;
            case 2:
                return DayPeriod.G;
            case 3:
                return DayPeriod.A;
            case 4:
                return DayPeriod.B;
            case 5:
                return DayPeriod.C;
            case 6:
                return DayPeriod.D;
        }
        return DayPeriod.E;
    }
    private DayPeriod onWednesday(int slot) {
        switch (slot){
            case 1:
                return DayPeriod.B;
            case 2:
                return DayPeriod.C;
            case 3:
                return DayPeriod.D;
            case 4:
                return DayPeriod.E;
            case 5:
                return DayPeriod.F;
            case 6:
                return DayPeriod.G;
        }
        return DayPeriod.A;
    }
    private DayPeriod onThursday(int slot) {
        switch (slot){
            case 1:
                return DayPeriod.E;
            case 2:
                return DayPeriod.F;
            case 3:
                return DayPeriod.G;
            case 4:
                return DayPeriod.A;
            case 5:
                return DayPeriod.B;
            case 6:
                return DayPeriod.C;
        }
        return DayPeriod.D;
    }
    private DayPeriod onFriday(int slot) {
        switch (slot){
            case 1:
                return DayPeriod.A;
            case 2:
                return DayPeriod.B;
            case 3:
                return DayPeriod.C;
            case 4:
                return DayPeriod.D;
            case 5:
                return DayPeriod.E;
            case 6:
                return DayPeriod.F;
        }
        return DayPeriod.G;
    }
    private DayPeriod onSaturday(int slot) {
        switch (slot){
            case 1:
                return DayPeriod.D;
            case 2:
                return DayPeriod.E;
            case 3:
                return DayPeriod.F;
            case 4:
                return DayPeriod.G;
            case 5:
                return DayPeriod.A;
            case 6:
                return DayPeriod.B;
        }
        return DayPeriod.C;
    }

    private final LocalTime Period1 = LocalTime.of(0, 0);
    private final LocalTime Period2 = LocalTime.of(3, 25);
    private final LocalTime Period3 = LocalTime.of(6, 51);
    private final LocalTime Period4 = LocalTime.of(10, 17);
    private final LocalTime Period5 = LocalTime.of(13, 42);
    private final LocalTime Period6 = LocalTime.of(17, 8);
    private final LocalTime Period7 = LocalTime.of(20, 34);

    public LocalTime getBeginningOfPeriod(DayPeriod period){
        switch (LocalDateTime.now().getDayOfWeek()){
            case SUNDAY: return beginingOfCurrentPeriodOnSunday(period);
            case MONDAY: return beginingOfCurrentPeriodOnMonday(period);
            case TUESDAY: return beginingOfCurrentPeriodOnTuesday(period);
            case WEDNESDAY: return beginingOfCurrentPeriodOnWednesday(period);
            case THURSDAY: return beginingOfCurrentPeriodOnThursday(period);
            case FRIDAY: return beginingOfCurrentPeriodOnFriday(period);
            default: return beginingOfCurrentPeriodOnSaturday(period);
        }
    }

    private LocalTime beginingOfCurrentPeriodOnSunday(DayPeriod period){
        switch(period){
            case G: return Period1;
            case A: return Period2;
            case B: return Period3;
            case C: return Period4;
            case D: return Period5;
            case E: return Period6;
            default: return Period7;
        }
    }
    private LocalTime beginingOfCurrentPeriodOnMonday(DayPeriod period){
        switch(period){
            case C: return Period1;
            case D: return Period2;
            case E: return Period3;
            case F: return Period4;
            case G: return Period5;
            case A: return Period6;
            default: return Period7;
        }
    }
    private LocalTime beginingOfCurrentPeriodOnTuesday(DayPeriod period){
        switch(period){
            case F: return Period1;
            case G: return Period2;
            case A: return Period3;
            case B: return Period4;
            case C: return Period5;
            case D: return Period6;
            default: return Period7;
        }
    }
    private LocalTime beginingOfCurrentPeriodOnWednesday(DayPeriod period){
        switch(period){
            case B: return Period1;
            case C: return Period2;
            case D: return Period3;
            case E: return Period4;
            case F: return Period5;
            case G: return Period6;
            default: return Period7;
        }
    }
    private LocalTime beginingOfCurrentPeriodOnThursday(DayPeriod period){
        switch(period){
            case E: return Period1;
            case F: return Period2;
            case G: return Period3;
            case A: return Period4;
            case B: return Period5;
            case C: return Period6;
            default: return Period7;
        }
    }
    private LocalTime beginingOfCurrentPeriodOnFriday(DayPeriod period){
        switch(period){
            case A: return Period1;
            case B: return Period2;
            case C: return Period3;
            case D: return Period4;
            case E: return Period5;
            case F: return Period6;
            default: return Period7;
        }
    }
    private LocalTime beginingOfCurrentPeriodOnSaturday(DayPeriod period){
        switch(period){
            case D: return Period1;
            case E: return Period2;
            case F: return Period3;
            case G: return Period4;
            case A: return Period5;
            case B: return Period6;
            default: return Period7;
        }
    }

    private void initializePeriodTimes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        aPeriods.add(sunday + space + Period2.format(formatter) + " to " + Period3.format(formatter));
        aPeriods.add(monday + space + Period6.format(formatter) + " to " + Period7.format(formatter));
        aPeriods.add(tuesday + space + Period3.format(formatter) + " to " + Period4.format(formatter));
        aPeriods.add(wednesday + space + Period7.format(formatter) + " to " + Period1.format(formatter));
        aPeriods.add(thursday + space + Period4.format(formatter) + " to " + Period5.format(formatter));
        aPeriods.add(friday + space + Period1.format(formatter) + " to " + Period2.format(formatter));
        aPeriods.add(saturday + space + Period5.format(formatter) + " to " + Period6.format(formatter));

        bPeriods.add(sunday + space + Period3.format(formatter) + " to " + Period4.format(formatter));
        bPeriods.add(monday + space + Period7.format(formatter) + " to " + Period1.format(formatter));
        bPeriods.add(tuesday + space + Period4.format(formatter) + " to " + Period5.format(formatter));
        bPeriods.add(wednesday + space + Period1.format(formatter) + " to " + Period2.format(formatter));
        bPeriods.add(thursday + space + Period5.format(formatter) + " to " + Period6.format(formatter));
        bPeriods.add(friday + space + Period2.format(formatter) + " to " + Period3.format(formatter));
        bPeriods.add(saturday + space + Period6.format(formatter) + " to " + Period7.format(formatter));

        cPeriods.add(sunday + space + Period4.format(formatter) + " to " + Period5.format(formatter));
        cPeriods.add(monday + space + Period1.format(formatter) + " to " + Period2.format(formatter));
        cPeriods.add(tuesday + space + Period5.format(formatter) + " to " + Period6.format(formatter));
        cPeriods.add(wednesday + space + Period2.format(formatter) + " to " + Period3.format(formatter));
        cPeriods.add(thursday + space + Period6.format(formatter) + " to " + Period7.format(formatter));
        cPeriods.add(friday + space + Period3 + " to " + Period4.format(formatter));
        cPeriods.add(saturday + space + Period7.format(formatter) + " to " + Period1.format(formatter));

        dPeriods.add(sunday + space + Period5.format(formatter) + " to " + Period6.format(formatter));
        dPeriods.add(monday + space + Period2.format(formatter) + " to " + Period3.format(formatter));
        dPeriods.add(tuesday + space + Period6.format(formatter) + " to " + Period7.format(formatter));
        dPeriods.add(wednesday + space + Period3.format(formatter) + " to " + Period4.format(formatter));
        dPeriods.add(thursday + space + Period7.format(formatter) + " to " + Period1.format(formatter));
        dPeriods.add(friday + space + Period4.format(formatter) + " to " + Period5.format(formatter));
        dPeriods.add(saturday + space + Period1.format(formatter) + " to " + Period2.format(formatter));

        ePeriods.add(sunday + space + Period6.format(formatter) + " to " + Period7.format(formatter));
        ePeriods.add(monday + space + Period3.format(formatter) + " to " + Period4.format(formatter));
        ePeriods.add(tuesday + space + Period7.format(formatter) + " to " + Period1.format(formatter));
        ePeriods.add(wednesday + space + Period4.format(formatter) + " to " + Period5.format(formatter));
        ePeriods.add(thursday + space + Period1.format(formatter) + " to " + Period2.format(formatter));
        ePeriods.add(friday + space + Period5.format(formatter) + " to " + Period6.format(formatter));
        ePeriods.add(saturday + space + Period2.format(formatter) + " to " + Period3.format(formatter));

        fPeriods.add(sunday + space + Period7.format(formatter) + " to " + Period1.format(formatter));
        fPeriods.add(monday + space + Period4.format(formatter) + " to " + Period5.format(formatter));
        fPeriods.add(tuesday + space + Period1.format(formatter) + " to " + Period2.format(formatter));
        fPeriods.add(wednesday + space + Period5.format(formatter) + " to " + Period6.format(formatter));
        fPeriods.add(thursday + space + Period2.format(formatter) + " to " + Period3.format(formatter));
        fPeriods.add(friday + space + Period6.format(formatter) + " to " + Period7.format(formatter));
        fPeriods.add(saturday + space + Period3.format(formatter) + " to " + Period4.format(formatter));

        gPeriods.add(sunday + space + Period1.format(formatter) + " to " + Period2.format(formatter));
        gPeriods.add(monday + space + Period5.format(formatter) + " to " + Period6.format(formatter));
        gPeriods.add(tuesday + space + Period2.format(formatter) + " to " + Period3.format(formatter));
        gPeriods.add(wednesday + space + Period6.format(formatter) + " to " + Period7.format(formatter));
        gPeriods.add(thursday + space + Period3.format(formatter) + " to " + Period4.format(formatter));
        gPeriods.add(friday + space + Period7.format(formatter) + " to " + Period1.format(formatter));
        gPeriods.add(saturday + space + Period4.format(formatter) + " to " + Period5.format(formatter));
    }

    public DayPeriod getCurrentPeriod() {
        int slot = 0;
        LocalTime now = LocalTime.now();
        if(now.equals(Period1)) slot = 1;
        else if (now.isAfter(Period1) && now.isBefore(Period2)) slot =1;
        else if (now.equals(Period2)) slot = 2;
        else if (now.isAfter(Period2) && now.isBefore(Period3)) slot = 2;
        else if (now.equals(Period3)) slot = 3;
        else if (now.isAfter(Period3) && now.isBefore(Period4)) slot = 3;
        else if (now.equals(Period4)) slot = 4;
        else if (now.isAfter(Period4) && now.isBefore(Period5)) slot = 4;
        else if (now.equals(Period5)) slot = 5;
        else if (now.isAfter(Period5) && now.isBefore(Period6)) slot = 5;
        else if (now.equals(Period6)) slot = 6;
        else if (now.isAfter(Period6) && now.isBefore(Period7)) slot = 6;
        else if (now.equals(Period7)) slot = 7;
        else if (now.isAfter(Period7)) slot = 7;

        switch (LocalDate.now().getDayOfWeek()){
            case SUNDAY:
                return onSunday(slot);
            case MONDAY:
                return onMonday(slot);
            case TUESDAY:
                return onTuesday(slot);
            case WEDNESDAY:
                return onWednesday(slot);
            case THURSDAY:
                return onThursday(slot);
            case FRIDAY:
                return onFriday(slot);
            default:
                return onSaturday(slot);
        }
    }

    /**
     * LocalTime.now().getHour() + offset.getHour() and LocalTime.now().getMinute() + offset.getMinute() must be within bounds or throws exception.
     * @param offset
     * @return
     * @throws IllegalArgumentException
     */
    public DayPeriod getCurrentPeriodFromTimezone(LocalTime offset) throws IllegalArgumentException {
        int slot = 0;
        LocalTime now = LocalTime.of(LocalTime.now().getHour() + offset.getHour(), LocalTime.now().getMinute() + offset.getMinute(), LocalTime.now().getSecond());
        if(now.equals(Period1)) slot = 1;
        else if (now.isAfter(Period1) && now.isBefore(Period2)) slot =1;
        else if (now.equals(Period2)) slot = 2;
        else if (now.isAfter(Period2) && now.isBefore(Period3)) slot = 2;
        else if (now.equals(Period3)) slot = 3;
        else if (now.isAfter(Period3) && now.isBefore(Period4)) slot = 3;
        else if (now.equals(Period4)) slot = 4;
        else if (now.isAfter(Period4) && now.isBefore(Period5)) slot = 4;
        else if (now.equals(Period5)) slot = 5;
        else if (now.isAfter(Period5) && now.isBefore(Period6)) slot = 5;
        else if (now.equals(Period6)) slot = 6;
        else if (now.isAfter(Period6) && now.isBefore(Period7)) slot = 6;
        else if (now.equals(Period7)) slot = 7;
        else if (now.isAfter(Period7)) slot = 7;

        switch (LocalDate.now().getDayOfWeek()){
            case SUNDAY:
                return onSunday(slot);
            case MONDAY:
                return onMonday(slot);
            case TUESDAY:
                return onTuesday(slot);
            case WEDNESDAY:
                return onWednesday(slot);
            case THURSDAY:
                return onThursday(slot);
            case FRIDAY:
                return onFriday(slot);
            default:
                return onSaturday(slot);
        }
    }

    private final String a = "THERE are many things which may be done during this period of the day, with the hope of fortunate realization and Cosmic cooperation. For instance, one may concentrate or meditate upon any plan for the purpose of evolving its details; he may ask favors from persons in high positions, especially when such favors relate to a promotion in position, in political power, or in social position; he may ask for stays or delays in legal procedure, the loan of money, the endorsement or recommendation of a proposition, the introduction to a person in high position. This is a propitious time for dealing with public officials, or persons of high rank; the signing of wills, deeds, or transfers, the writing of important letters that seek favors, promotions, or recommendations, or which carry to the mind of another person a high regard of one’s self, his business, or any plan he is proposing. It is a good time in which to talk to bankers or financiers for the purpose of building up personal credit or the credit of a business, the making of a public appearance or address for the purpose of bringing esteem and honor to yourself or your business, or for building up your reputation or the reputation of your affairs. It is not a good period to deal with criminals or evil matters, even as a lawyer or adviser. It is a time filled with energy which must be controlled. It is also a period filled with fiery impulses which must be governed, just as all words and acts must be cautiously controlled. It is not a good period to start a new business, a new plan, or a new proposition of any kind; it is not good for the buying of livestock; neither is it good for the signing of contracts or agreements. It is not a good period in which to start short journeys of several days’ duration, nor is it a good period in which to deal with marital affairs or to marry, or to go courting. It is a bad period in which to loan money, to move into a new location for either home or business, or to start the erection of a new building of any kind. And it is not a good time in which to make the first financial investment in a new business. It is not fortunate for buying real estate or even for selling or renting it. Nor is it a good period for surgical operations.";
    private final String aSummary = "This is a propitious time for dealing with public officials, or persons of high rank.\n" +
            "\n" +
            "It is a good time in which to talk to bankers or financiers for the purpose of building up personal credit or the credit of a business.\n" +
            "\n" +
            "It is not a good period to deal with criminals or evil matters, even as a lawyer or adviser.\n" +
            "\n" +
            "It is a time filled with energy which must be controlled. It is also a period filled with fiery impulses which must be governed, just as all words and acts must be cautiously controlled.\n";
    private final String b = "This period is fortunate for the following things: Matters dealing with art, music, the beautifying of the home or person, or with matters pertaining to purely material and sensual affairs. It is an excellent period for starting any new undertaking; for the enjoyment of art, music, and drama; for the buying of livestock; for the collecting of accounts; or for dealing with the public in connection with public administration, public affairs, and public utilities, or soliciting business from the public. It is also good for the hiring of agents, collectors, traveling representatives, salesmen, and employees for important positions in the business or home. New acquaintances made during this period are generally dependable and worthy of friendship and trust, if they come into your life purely in a social way. It is a good period to start short journeys, lasting for two or three days, or less than a month; a good time for marriage and courting, for loaning money or borrowing money; to put into material form any new plans for business or pleasure; for indulging in recreation and social affairs, or holding any social function. It is also a good period for seeking favors in a social way, or business favors in social circles. It is also good for speculating, for games of chance, and for investments of a speculative nature; also a good period for dealing with women in either business or social matters. It is not a period of great ambition, and while it is changeable, it is easily adapted to many conditions. It is a fruitful period inasmuch as most things started or culminated during this period will be more prolific than one may anticipate. It also brings its impulses of an intellectual and social nature, which must be guarded against. It is not a good time for hiring servants or persons for menial positions, and is not a good time for starting long journeys, especially those which either by train or water take one far from home. \n" +
            "\n";
    private final String bSummary = "New acquaintances made during this period are generally dependable and worthy of friendship and trust, if they come into your life purely in a social way.\n" +
            "\n" +
            "It is also good for speculating, for games of chance, and for investments of a speculative nature.\n" +
            "\n" +
            "It is also a good period for dealing with women in either business or social matters.\n" +
            "\n" +
            "It also brings its impulses of an intellectual and social nature, which must be guarded against.\n";
    private final String c = "This period is especially fortunate for dealing with the fine arts, or the intellectual things of life, especially education, scientific research, publishing, printing, instructing in schools, colleges, universities, and in the promotion of campaigns involving an educational element. It is a good time for study, memory work, and absorption of special knowledge, analytical examination of documents, books, papers, and propositions, or to deal with legal arguments in court requiring the use of the intellect and logic. It is an especially good period for mental activity of any kind, including writing, thinking, speaking, and self-examination. It is also a good period to indulge in the drama, music, and art. The buying of livestock, or dealing in cattle or the livestock market, is fortunate during this period. It is a good time for the making of contracts providing same are not for long periods but of short duration, collecting of accounts, making of new acquaintances that are dependable, the hiring of business employees and servants of all kinds and classes. It is also a good time to start short journeys, to do literary and newspaper work, prepare advertising, start new advertising campaigns, or to send out literature to the public pertaining to business or social affairs. It is also a fortunate time for the taking of medicine or any system of therapeutics which is to benefit the physical body. It is a good time to lend money but it is questionable whether it is a good period in which to borrow. It is a good time in which to erect new buildings, or to plan new undertakings, and students of the occult, the philosophical, and metaphysical will find that this is an excellent period for study and objective realization of great truths. It is a good period in which to take a chance with undertakings that are highly tricky, or questionable from a financial point of view, for one who has the means to do this without bringing financial embarrassment should the result not be all that is expected. It is a good period in which to have a few minutes of recreation or social intercourse, and for signing important papers of all kinds, and it is likewise the best period for traveling salesmen to call upon the most difficult of prospective customers. It is also a good time for writing important letters. This period is not so good for dealing with private or public enemies or bringing them into court, or attempting to adjust matters with them, for this period will bring endless discussions and arguments without any beneficial results. It is a period that is quite changeable in many ways, giving great mental activity, but is not good for prudence and caution, and, therefore, no dependence should be placed in one’s usual cautiousness. It is not good for marriage, and it is a questionable period to deal with lawyers in regard to any problem, or to deal with inventions and mechanical problems, or to seek promotion in business, or to ask for the favor or recognition of public officials or prominent persons. It is not a good time to buy real estate, and it is questionable whether it is a good time to sell real estate. It is a doubtful period for seeking favors, or for spiritual development or concentration, and is a very unfortunate period for dealing with surgeons or having a surgical operation of any kind. \n" +
            "\n" +
            "It should be remembered that during this period one comes in contact with the nimbleness of mind and tongue. Any person presenting a proposition or plan to you at this time is very apt to exaggerate or mislead through his statements or his evidence. Forgers, blackmailers, and persons who are deceitful, lying, and too nimble with their expert fingers, are apt to present themselves during this period. Therefore guard yourself accordingly. ";
    private final String cSummary = "This period is especially fortunate for the intellectual things of life, especially education, scientific research, publishing, printing, instructing in schools, colleges, universities, and in the promotion of campaigns involving an educational element.\n" +
            "\n" +
            "It is also a good time to do literary and newspaper work, prepare advertising, start new advertising campaigns, or to send out literature to the public pertaining to business or social affairs.\n" +
            "\n" +
            "It is a good period in which to take a chance with undertakings that are highly tricky, or questionable from a financial point of view, for one who has the means to do this without bringing financial embarrassment should the result not be all that is expected.\n" +
            "\n" +
            "It is a good period in which to have a few minutes of recreation or social intercourse.\n" +
            "\n" +
            "This period is not so good for dealing with private or public enemies or bringing them into court, or attempting to adjust matters with them, for this period will bring endless discussions and arguments without any beneficial results.\n" +
            "\n" +
            "It is a period that is quite changeable in many ways, giving great mental activity, but is not good for prudence and caution, and, therefore, no dependence should be placed in one’s usual cautiousness.\n" +
            "\n" +
            "It is a doubtful period for seeking favors.\n" +
            "\n" +
            "It should be remembered that during this period one comes in contact with the nimbleness of mind and tongue. Any person presenting a proposition or plan to you at this time is very apt to exaggerate or mislead through his statements or his evidence. Forgers, blackmailers, and persons who are deceitful, lying, and too nimble with their expert fingers, are apt to present themselves during this period. Therefore guard yourself accordingly.\n";
    private final String d = "Here we have a period that is especially fortunate for all general material affairs of business, dealings with the public in any general capacity, educational work of any kind, planting or farming operations, the making of new acquaintances, and the hiring of servants of all classes. \n" +
            "\n" +
            "It is also a good period in which to start short journeys or long journeys by water, and for writing, supervising, or dealing with literary or newspaper work. It is also a good period for marriage or for courting, for all marine affairs, for the taking of medicine or any system of therapeutic help for the body or mind, for metaphysical study and analysis, or for dealing with shipping interests, transportation interests, or the actual shipping of goods to places out of the city in which you may live. It is also good for dealing with surgeons or for surgical operations, and it is one of the good periods for salesmen, traveling agents, and others to solicit and sell, and for dealing especially with women. It is a period in which the ambitions may be highly aroused, and while these ambitions may be very impulsive, they will generally prove fruitful. It is not a good period for commencing any new undertaking, the buying of livestock, the making of contracts, or the signing of legal papers of any kind, or to start lawsuits, or court actions. It is not a good period in which to borrow money or attempt to borrow it, nor sign any papers or notes pertaining to money matters, nor speculate, nor take part in games of chance of any kind. It is also a bad period for writing letters, pleas, or requests of any kind asking for important favors or aids in connection with business, personal, or social life. \n";
    private final String dSummary = "It is one of the good periods for salesmen, traveling agents, and others to solicit and sell.\n" +
            "\n" +
            "It is not a good period for commencing any new undertaking, the making of contracts, or the signing of legal papers of any kind, or to start lawsuits, or court actions.\n" +
            "\n" +
            "It is not a good period in which to speculate, nor take part in games of chance of any kind.\n" +
            "\n" +
            "It is also a bad period for writing letters, pleas, or requests of any kind asking for important favors or aids in connection with business, personal, or social life.\n";
    private final String e = "This period is particularly good for aggressive pursuits, or those activities that require deep thought followed by a long campaign or a long period of steady action. It is good to begin these things during this period. It is an excellent time to have one’s affairs come before judges, referees, magistrates, police authorities, senators, governors, mayors, or the presidents of large corporations, or those persons who have within their power the privilege to decide or render decisions in any matters of dispute. It is a good period for bringing permanency to anything started or finished during it, and gives great persistency and endurance to all activities. It is also good for literary or newspaper work or advertising, or sales promotion by mail through the use of letters or brief printed communications. It is good, too, for starting any legal action in court, or for the submission of briefs or arguments, and for all inventions or mechanical problems or matters dealing with them, also for matters pertaining to metallurgy, or affairs with metal workers. It is a good time to move into a new house or to buy and sell real estate or to move into or transfer real estate. It is an excellent period for starting or indulging in scientific pursuits, and for spiritual meditation. This period, however, is also unfortunate for certain things, and these are quite definite; it should be noted that the unfortunate things will prove to be unfortunate indeed. They are: The making of contracts or agreements of any kind, other than the purchase of homes, attempting to collect money, the planting of seeds, or starting of farm operations, making new acquaintances for the first time, the hiring of servants, agents, salesmen, or collectors of any class or for any position, or for starting long journeys. The period is also very unfortunate for journeys by water, or for marriage; or, for the taking of medicine or any method of mental or physical cure, for borrowing or loaning money, erecting new buildings, dealing with public officials, or prominent persons from whom you seek personal favors or special recognition, starting any risky business, indulging in recreational or social affairs, speculating in business, in the stock market, or otherwise, for surgical operations, or for writing letters of an important nature. \n";
    private final String eSummary = "This period is particularly good for aggressive pursuits, or those activities that require deep thought followed by a long campaign or a long period of steady action.\n" +
            "\n" +
            "It is an excellent time to have one’s affairs come before judges, referees, magistrates, police authorities, senators, governors, mayors, or the presidents of large corporations, or those persons who have within their power the privilege to decide or render decisions in any matters of dispute.\n" +
            "\n" +
            "It is a good period for bringing permanency to anything started or finished during it.\n" +
            "\n" +
            "It is good, too, for starting any legal action in court, or for the submission of briefs or arguments.\n" +
            "\n" +
            "This period, however, is also unfortunate for certain things, and these are quite definite; it should be noted that the unfortunate things will prove to be unfortunate indeed. Includes: The dealing with public officials, or prominent persons from whom you seek personal favors or special recognition.\n";
    private final String f = "This one of the most fortunate periods in each day. It might be called the lucky period, just as the preceding one is generally considered the unlucky period. During this “F” period of each day, we find conditions are fortunate for the starting of any new undertaking, the buying or marketing of cattle or livestock, either in speculation or for actual business purposes, for making contracts or signing contracts, agreements, and all papers of specific stipulation, for collecting accounts or raising money, for educational work, and educational interests, for making new acquaintances, or starting long journeys, either for business or pleasure. It is also a good period for short journeys by water and other means, and for literary and newspaper work, or for dealing with lawyers, or the submission of briefs or papers to court, or the actual starting of court procedure. It is also good for marriage or courting, for borrowing money, erecting of new buildings, working out the plans of new undertakings, and holding directors’ meetings for the discussion of business conditions or new ventures, for seeking promotion in business, or the building up of trade and credit reputes, dealing with public officials, or with the public mass in all affairs, or with prominent persons. It is a good time for the buying or selling of real estate, for all social affairs and recreations, for seeking favors, especially for women who are seeking favors from those of the opposite sex, in either a business or social manner, and for signing papers dealing with important matters of any nature. It is the fortunate period for all forms of speculation, and for the writing of important letters. There are a few things that should be noticed in regard to this fortunate period, however. It is a period that brings a great deal of energy to the body and mind, and tempts one to overdraw in many ways, and yet with all the impulsiveness of this period it is generally fruitful, and, therefore, fortunate. It is a more fortunate period for men than for women, in business affairs, but more fortunate for women than men in social affairs. It is a period of positiveness, and yet with a natural tendency toward caution and prudence. It generally gives and begets the spirit and love of justice, and the period makes for permanency. It is not a good time for hiring servants for any menial position, nor is it good for marine affairs. ";
    private final String fSummary = "This period is good for literary and newspaper work.\n" +
            "\n" +
            "This period is good for dealing with lawyers, or the submission of briefs or papers to court, or the actual starting of court procedure.\n" +
            "\n" +
            "It is also good for marriage or courting.\n" +
            "\n" +
            "It is also good for holding directors’ meetings for the discussion of business conditions or new ventures, for seeking promotion in business, or the building up of trade and credit reputes.\n" +
            "\n" +
            "It is also good for dealing with public officials or with prominent persons.\n" +
            "\n" +
            "It is also good for dealing with the public mass in all affairs.\n" +
            "\n" +
            "It is especially good for women who are seeking favors from those of the opposite sex, in either a business or social manner.\n" +
            "\n" +
            "It is a more fortunate period for men than for women, in business affairs, but more fortunate for women than men in social affairs.\n" +
            "\n" +
            "It is a period that brings a great deal of energy to the body and mind, and tempts one to overdraw in many ways, and yet with all the impulsiveness of this period it is generally fruitful, and, therefore, fortunate. It is a period of positiveness, and yet with a natural tendency toward caution and prudence. It generally gives and begets the spirit and love of justice, and the period makes for permanency.\n";
    private final String g = "This period is especially good for mastering those affairs which require considerable energy and aggressiveness, endurance, and persistency. It is an excellent period for dealing with those matters that require the expenditure of more physical energy than mental energy, and require real labor and muscle. Therefore, all material and sensual affairs will be fortunate during this period, as well as the collecting of money, the hiring of traveling salesmen, agents or collectors, or the soliciting on their part. It is also fortunate for martial affairs, marine affairs, the working out of mechanical problems, inventions, or building plans, or matters dealing with metal and metal workers. It is also good for scientific pursuits, and for women who are seeking favors from men in social or business affairs. It is not a good period for any beneficent matters, or matters dealing with the receipt of gifts or favors, or public humanitarian activities, nor is this period fraught with much prudence and caution. It is an unfortunate period for the buying of cattle or livestock, or speculating with them, or for dealing with enemies, or for starting long journeys, or for legal actions, or dealings with lawyers or matters in court. Naturally it would be a bad period for marriage or for courting, and for seeking favors generally. It is very questionable whether it is a good period for surgical operations, or for dealing with women. This is the period in which accidents are apt to occur; therefore, one should be careful about being in any place of hazard or being near firearms, fire explosions, or other things that would affect the physical body. In illness, fevers are apt to be high during this time and the temperature of the body is naturally warmer during this period than at any other. \n" +
            "\n";
    private final String gSummary = "This period is especially good for mastering those affairs which require considerable energy and aggressiveness, endurance, and persistency. It is an excellent period for dealing with those matters that require the expenditure of more physical energy than mental energy, and require real labor and muscle.\n" +
            "\n" +
            "It is also good for women who are seeking favors from men in social or business affairs.\n" +
            "\n" +
            "It is not a good period for any beneficent matters, or matters dealing with the receipt of gifts or favors, or public humanitarian activities.\n" +
            "\n" +
            "It is not fraught with much prudence and caution.\n" +
            "\n" +
            "It is an unfortunate period for dealing with enemies, or for legal actions, or dealings with lawyers or matters in court.\n" +
            "\n" +
            "Naturally it would be a bad period for marriage or for courting, and for seeking favors generally.\n" +
            "\n" +
            "This is the period in which accidents are apt to occur.\n" +
            "\n" +
            "In illness, fevers are apt to be high during this time and the temperature of the body is naturally warmer during this period than at any other.\n";

    public String createTitle() {
        return "Daily Cycle Printout";
    }

    public String getSystemTimezone(){
        //todo get default system's timezone, and possibly include timezones in the same TimeBelt
        return "";
    }

}
