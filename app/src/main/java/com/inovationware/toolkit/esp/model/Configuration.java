package com.inovationware.toolkit.esp.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.esp.model.domain.BasePlatform;
import com.inovationware.toolkit.esp.model.domain.Board;
import com.inovationware.toolkit.esp.model.domain.SensorPlatform;
import com.inovationware.toolkit.esp.model.value.Api;
import com.inovationware.toolkit.esp.model.value.Logger;
import com.inovationware.toolkit.esp.model.value.Sensor;
import com.inovationware.toolkit.esp.model.value.Wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Configuration {
    private final String name;
    private final BasePlatform platform;
    private final Board board;

    private final Wifi wifi;
    private final Api api;
    private List<Sensor> sensors = new ArrayList<>();
    private final Logger logger;

    public static Configuration create(String name, BasePlatform platform, Board board, Wifi wifi, Api api, List<Sensor> sensors, Logger logger){
        return Configuration.builder()
                .name(name)
                .platform(platform)
                .board(board)
                .wifi(wifi)
                .api(api)
                .sensors(sensors)
                .logger(logger)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public String toString() {

        String br = "\n" +
                "\n";

        StringBuilder builder = new StringBuilder();

        builder.append("esphome:\n" +
                "  name: " + this.name + "\n" +
                "  platform: " + this.platform.name() + "\n" +
                "  board: " + this.board.name() + "\n" +
                "\n");

        builder.append(this.wifi.toString());
        builder.append(br);
        builder.append(this.api.toString());
        builder.append(br);

        builder.append("sensor:");
        builder.append("\n");

        builder.append(this.sensors.stream().map(Sensor::toString).collect(Collectors.joining("\n")));
        builder.append("\n");

        builder.append("logger:");
        builder.append("\n");
        builder.append(this.logger.toString());

        return builder.toString();
    }
}
