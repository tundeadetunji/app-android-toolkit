package com.inovationware.toolkit.features.nettimer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inovationware.toolkit.common.utility.Json;

import java.io.IOException;

public class NetTimerTaskDtoFromNetwork {
    @JsonProperty
    private String todo;
    @JsonProperty
    private String requirement;
    @JsonProperty
    private int after;
    @JsonProperty
    private String display;
    @JsonProperty
    private boolean remind;
    @JsonProperty
    private boolean repeat;

    private NetTimerTaskDtoFromNetwork(String todo, String requirement, int after, String display, boolean remind, boolean repeat){
        this.todo = todo;
        this.requirement = requirement;
        this.after = after;
        this.display = display;
        this.remind = remind;
        this.repeat = repeat;
    }
    public static NetTimerTaskDtoFromNetwork create(NetTimerToDo todo, String requirement, int after, String display, boolean remind, boolean repeat){
        return new NetTimerTaskDtoFromNetwork(todo.getLabel(), requirement, after, display, remind, repeat);
    }

    public static String toJson(NetTimerTaskDtoFromNetwork dto) throws IOException {
        return Json.from(dto);
    }

}
