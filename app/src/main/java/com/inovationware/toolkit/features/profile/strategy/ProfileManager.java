package com.inovationware.toolkit.features.profile.strategy;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.features.profile.model.Profile;

import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

public class ProfileManager {
    private static ProfileManager instance;

    public static ProfileManager getInstance(SharedPreferencesManager store, Context context) {
        if (instance == null) instance = new ProfileManager(store, context);
        return instance;
    }

    private ProfileManager(SharedPreferencesManager store, Context context) {
        this.store = store;
        this.context = context;
        initialize();
    }

    @Getter
    private Map<String, Profile> profiles = new HashMap<>();
    @Getter
    private String[] nameListing = new String[0];
    private SharedPreferencesManager store;
    private Context context;
    private final String namesKey = "PROFILE_MANAGER_NAMES";
    private final String daysKey = "PROFILE_MANAGER_DAYS";
    private final String monthsKey = "PROFILE_MANAGER_MONTHS";
    private final String newLine = "\n";

    public void initialize() {
        String names = store.getString(context, namesKey).trim();
        String days = store.getString(context, daysKey).trim();
        String months = store.getString(context, monthsKey).trim();
        if (names.isEmpty() || days.isEmpty() || months.isEmpty()) return;
        String[] namesArray = names.split(newLine);
        String[] daysArray = days.split(newLine);
        String[] monthsArray = months.split(newLine);
        for (int i = 0; i < namesArray.length; i++) {
            profiles.put(namesArray[i], Profile.create(namesArray[i], Integer.parseInt(daysArray[i]), Integer.parseInt(monthsArray[i])));
        }

        Arrays.sort(namesArray);
        nameListing = namesArray;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add(String name, int day, String month) {
        int m = intValueFromMonthString(month);

        //if (profiles.containsKey(name.trim())) return;
        profiles.put(name.trim(), Profile.create(name.trim(), day, m));

        store.setString(context, namesKey, store.getString(context, namesKey) + newLine + name.trim());
        store.setString(context, daysKey, store.getString(context, daysKey) + newLine + day);
        store.setString(context, monthsKey, store.getString(context, monthsKey) + newLine + m);
    }

    public Profile getProfile(String name){
        return profiles.get(name.trim());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int intValueFromMonthString(String month) {
        if (month.equalsIgnoreCase(Month.JANUARY.name())) return 1;
        if (month.equalsIgnoreCase(Month.FEBRUARY.name())) return 2;
        if (month.equalsIgnoreCase(Month.MARCH.name())) return 3;
        if (month.equalsIgnoreCase(Month.APRIL.name())) return 4;
        if (month.equalsIgnoreCase(Month.MAY.name())) return 5;
        if (month.equalsIgnoreCase(Month.JUNE.name())) return 6;
        if (month.equalsIgnoreCase(Month.JULY.name())) return 7;
        if (month.equalsIgnoreCase(Month.AUGUST.name())) return 8;
        if (month.equalsIgnoreCase(Month.SEPTEMBER.name())) return 9;
        if (month.equalsIgnoreCase(Month.OCTOBER.name())) return 10;
        if (month.equalsIgnoreCase(Month.NOVEMBER.name())) return 11;
        return 12;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String monthStringFromInt(int month){
        if (month == 1) return Month.JANUARY.name();
        if (month == 2) return Month.FEBRUARY.name();
        if (month == 3) return Month.MARCH.name();
        if (month == 4) return Month.APRIL.name();
        if (month == 5) return Month.MAY.name();
        if (month == 6) return Month.JUNE.name();
        if (month == 7) return Month.JULY.name();
        if (month == 8) return Month.AUGUST.name();
        if (month == 9) return Month.SEPTEMBER.name();
        if (month == 10) return Month.OCTOBER.name();
        if (month == 11) return Month.NOVEMBER.name();
        return Month.DECEMBER.name();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void remove(String name) {
        if (!profiles.containsKey(name.trim())) return;

        profiles.remove(name.trim());

        StringBuilder names = new StringBuilder();
        StringBuilder days = new StringBuilder();
        StringBuilder months = new StringBuilder();

        profiles.values().stream().map(Profile::getName).collect(Collectors.toList()).forEach(i -> names.append(i).append(newLine));
        profiles.values().stream().map(Profile::getDay).collect(Collectors.toList()).forEach(j -> days.append(j).append(newLine));
        profiles.values().stream().map(Profile::getMonth).collect(Collectors.toList()).forEach(k -> months.append(k).append(newLine));

        store.setString(context, namesKey, names.toString());
        store.setString(context, daysKey, days.toString());
        store.setString(context, monthsKey, months.toString());

        /*for(int i = 0; i < profiles.size(); i++){
            names.append(profiles.get(i).getName()).append(newLine);
            days.append(profiles.get(i).getDay()).append(newLine);
            months.append(profiles.get(i).getMonth()).append(newLine);
        }*/

    }


}
