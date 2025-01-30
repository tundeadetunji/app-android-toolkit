package com.inovationware.toolkit.esp.model.value;

import androidx.annotation.NonNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Api {
    private final String password;

    public static Api create(String password){
        return Api.builder()
                .password(password)
                .build();
    }

    @NonNull
    @Override
    public String toString() {
        return "api:\n" +
                "  password: !secret " + this.getPassword();
    }
}
