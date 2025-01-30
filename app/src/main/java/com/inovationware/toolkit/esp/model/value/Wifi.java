package com.inovationware.toolkit.esp.model.value;

import androidx.annotation.NonNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Wifi {
    private final String ssid;
    private final String password;

    public static Wifi create(String ssid, String password) {
        return Wifi.builder()
                .ssid(ssid)
                .password(password)
                .build();
    }

    @NonNull
    @Override
    public String toString() {
        return "wifi:\n" +
                "  ssid: !secret " + this.getSsid() + "\n" +
                "  password: !secret " + this.getPassword();
    }
}
