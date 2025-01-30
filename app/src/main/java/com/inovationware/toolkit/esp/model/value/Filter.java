package com.inovationware.toolkit.esp.model.value;

import androidx.annotation.NonNull;

import com.inovationware.toolkit.esp.model.domain.TimingValue;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Filter {

    private final TimingValue delayed_on;
    private final TimingValue delayed_off;

    public static Filter create(TimingValue delayed_on, TimingValue delayed_off){
        return Filter.builder()
                .delayed_on(delayed_on)
                .delayed_off(delayed_off)
                .build();
    }

    @NonNull
    @Override
    public String toString() {
        return "      - delayed_on: " + TimingValue.to(delayed_on) + "\n" +
                "      - delayed_off: " + TimingValue.to(delayed_off);
    }
}
