package com.inovationware.toolkit.features.esp.model.value;

import androidx.annotation.NonNull;

import com.inovationware.toolkit.features.esp.model.domain.LogLevel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Logger {
    private final LogLevel level;

    public static Logger create(LogLevel level){
        return Logger.builder()
                .level(level)
                .build();
    }

    @NonNull
    @Override
    public String toString() {
        return "  level: " + level.name();
    }
}
