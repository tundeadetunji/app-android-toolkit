package com.inovationware.toolkit.esp.model.value;

import androidx.annotation.NonNull;

import com.inovationware.toolkit.esp.model.domain.Control;
import com.inovationware.toolkit.esp.model.domain.Unit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Component {

    private final Control control;
    private final String name;
    private final Unit unit_of_measurement;

    public static Component create(Control control, String name, Unit unit_of_measurement){
        return Component.builder()
                .control(control)
                .name(name)
                .unit_of_measurement(unit_of_measurement)
                .build();
    }

    @NonNull
    @Override
    public String toString() {
        return "    " + control.name() + ":\n" +
                "      name: \"" + name + "\"\n" +
                "      unit_of_measurement: \"" + Unit.to(unit_of_measurement) + "\"";
    }
}
