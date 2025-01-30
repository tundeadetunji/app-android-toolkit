package com.inovationware.toolkit.profile.model;


import java.io.IOException;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
