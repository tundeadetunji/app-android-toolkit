package com.inovationware.toolkit.features.interaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class InteractionToken {

    public enum PcOp {
        CapsLock,
        NumLock,
        PlayerFullScreen,
        PlayerPause,
        MediaNextTrack,
        MediaPreviousTrack,
        MediaPlayPause,
        MediaStop,
        MiscTouch,
        MoveApp,
        ExitApp
    }

    @JsonProperty private final PcOp op;
    @JsonProperty private final int x;
    @JsonProperty private final int y;
    @JsonProperty private final String app;

    private InteractionToken(PcOp op, int x, int y, String app) {
        this.app = app;
        this.op = op;
        this.x = x;
        this.y = y;
    }

    public static InteractionToken create(PcOp op, int x, int y, String app){
        return new InteractionToken(op, x, y, app);
    }

    public static String[] opListing(){
        List<String> result = new ArrayList<>();
        for (PcOp app : PcOp.values()){
            result.add(app.name());
        }
        Collections.sort(result);
        return result.toArray(new String[0]);
    }

}
