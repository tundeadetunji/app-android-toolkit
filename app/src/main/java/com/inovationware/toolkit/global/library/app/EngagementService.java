package com.inovationware.toolkit.global.library.app;

import java.util.*;

public class EngagementService {

    private static EngagementService instance;

    public static EngagementService getInstance() {
        if (instance == null) instance = new EngagementService();
        return instance;
    }

    private EngagementService() {
    }


    public enum Engagement {
        Hibernate,
        Increase_Volume,
        Mute,
        Reduce_Volume,
        Restart,
        Shutdown,
        Task_Manager,
        Volume_Down,
        Volume_Up,
        Who_Is,
        Who_Was,
        Workstation,
        Last_30,
        What_Is_On,
        Ping;

        public static boolean isNotApp(String engagementValue) {
            if (engagementValue.equalsIgnoreCase(Who_Is.name().replace(underscore, space)) || engagementValue.equalsIgnoreCase(Last_30.name().replace(underscore, space)))
                return true;
            return false;
        }

        private static final String underscore = "_";
        private static final String space = " ";

        public static Engagement fromCanonicalString(String engagement){
            return Engagement.valueOf(engagement.replace(space, underscore));
        }

    }


    public List<String> listing() {
        List<String> result = new ArrayList<>();
        for (Engagement value : Engagement.values()) {
            result.add(value.name().replace("_", " "));
        }
        Collections.sort(result);
        return result;
    }

}
