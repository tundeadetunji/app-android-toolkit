package com.inovationware.toolkit.nettimer.model;


import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetTimerObject {
    public String headline;
    public String details;
    public String time_string;
    public String zone_string;

    public static List<NetTimerObject> listing(String responseFromServer, Context context) {
        List<NetTimerObject> result = new ArrayList<>();
        List<String> tokens = Arrays.asList(responseFromServer.strip().trim().split(delimiter));
        for (String token : tokens) {
            NetTimerObject object = new NetTimerObject();
            String[] objectString = token.strip().replace(delimiter, "").trim().split("\n");
            if (objectString.length < 4) continue;
            for (int i = 0; i < objectString.length; i++) {
                object.headline = objectString[0];
                object.details = objectString[1];
                object.time_string = objectString[2];
                object.zone_string = objectString[3];
            }
            result.add(object);
        }
        Collections.reverse(result);
        return result;
    }

    private static final String delimiter = "---";

}
