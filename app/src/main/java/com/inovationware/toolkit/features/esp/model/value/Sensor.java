package com.inovationware.toolkit.features.esp.model.value;


import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.features.esp.model.domain.Mode;
import com.inovationware.toolkit.features.esp.model.domain.SensorPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Sensor {

    private final SensorPlatform platform;
    private final int pin;
    private List<Component> components = new ArrayList<>(); //Todo compile error - build project to verify status
    private Mode mode;
    private String name;
    private Filter filter;

    public static Sensor create(SensorPlatform platform, int pin, List<Component> components, Mode mode, String name, Filter filter){
        return Sensor.builder()
                .platform(platform)
                .pin(pin)
                .components(components)
                .mode(mode)
                .name(name)
                .filter(filter)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public String toString() {
        String br = "\n";
        StringBuilder builder = new StringBuilder();
        builder.append("  - platform: " + this.platform.name() + "\n" +
                "    pin: " + this.pin + "\n");

        builder.append(!this.components.isEmpty() ? this.components.stream().map(Component::toString).collect(Collectors.joining("\n")) : "");
        builder.append(br);

        builder.append("    mode: " + this.mode.name() + "\n" +
                "    name: \"" + this.name + "\"");

        builder.append(br);
        builder.append(this.filter != null ? "    filters:" : "");
        builder.append(this.filter != null ? br : "");
        builder.append(this.filter != null ? this.filter.toString() : "");
        builder.append(this.filter != null ? br : "");

        return builder.toString();
    }
}
