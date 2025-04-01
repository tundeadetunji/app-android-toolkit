package com.inovationware.toolkit.global.library.app;

import java.util.*;

public class EngagementService {

    private static EngagementService instance;
    private static Map<Engagement, EngagementResponseType> engagementResponseTypes = new HashMap<>();

    public static EngagementService getInstance() {
        if (instance == null) instance = new EngagementService();
        return instance;
    }

    private EngagementService() {
        engagementResponseTypes.put(Engagement.Hibernate, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Increase_Volume, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Mute, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Reduce_Volume, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Restart, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Workstation, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Shutdown, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Task_Manager, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Volume_Down, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Volume_Up, EngagementResponseType.isApp);
        engagementResponseTypes.put(Engagement.Dim_Screen, EngagementResponseType.isApp);

        engagementResponseTypes.put(Engagement.Who_Is, EngagementResponseType.isRequest);
        engagementResponseTypes.put(Engagement.Who_Was, EngagementResponseType.isRequest);
        engagementResponseTypes.put(Engagement.Last_30, EngagementResponseType.isRequest);
        engagementResponseTypes.put(Engagement.What_Is_On, EngagementResponseType.isRequest);

        engagementResponseTypes.put(Engagement.Ping, EngagementResponseType.isPing);

    }


    public enum EngagementResponseType {
        isApp,
        isRequest,
        isRegular,
        isPing;

    }
    public enum Engagement {
        Hibernate,
        Increase_Volume,
        Mute,
        Reduce_Volume,
        Restart,
        Workstation,
        Shutdown,
        Task_Manager,
        Volume_Down,
        Volume_Up,
        Who_Is,
        Who_Was,
        Last_30,
        What_Is_On,
        Ping,
        Dim_Screen;

        private static final String underscore = "_";
        private static final String space = " ";

        public static Engagement fromCanonicalString(String engagement){
            return Engagement.valueOf(engagement.replace(space, underscore));
        }

    }

    public EngagementResponseType from(Engagement engagement) {
        return engagementResponseTypes.get(engagement);
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
