package com.inovationware.toolkit.features.profile.model;


import lombok.Getter;

@Getter
public class Profile {
    private String name;
    private int day;
    private int month;

    private Profile(String name, int day, int month){
        this.name = name;
        this.day = day;
        this.month = month;
    }
    public static Profile create(String name, int day, int month){
        return new Profile(name, day, month);
    }
}
