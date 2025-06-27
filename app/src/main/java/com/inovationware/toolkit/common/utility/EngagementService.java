package com.inovationware.toolkit.common.utility;

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
        engagementResponseTypes.put(Engagement.What_Was_On, EngagementResponseType.isRequest);

        engagementResponseTypes.put(Engagement.Ping, EngagementResponseType.isPing);

    }


    public enum EngagementResponseType {
        isApp,
        isRequest,
        isRegular,
        isPing;

    }

    public enum EngagementSection{
        Pc,
        Power,
        Engaging,
        Now
    }
    public enum Engagement {
        Hibernate,
        Now,
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
        What_Was_On, //new
        Ping,
        Dim_Screen;

        private static final String underscore = "_";
        private static final String space = " ";

        public static Engagement fromCanonicalString(String engagement){
            return Engagement.valueOf(engagement.replace(space, underscore));
        }

    }

    public enum NowEngagement{
        Now
    }
    public enum EngagingEngagement {
        Who_Is,
        Who_Was,
        What_Was_On,
        //Last_30,
        What_Is_On,
        Ping

    }

    public enum PcEngagement {
        Increase_Volume,
        Mute,
        Dim_Screen,
        Reduce_Volume,
        Workstation,
        Task_Manager,
        Volume_Down,
        Volume_Up;

    }

    public enum PowerEngagement {
        Hibernate,
        Restart,
        Shutdown;

    }

    public EngagementResponseType from(Engagement engagement) {
        return engagementResponseTypes.get(engagement);
    }

    public List<String> listing(EngagementSection section) {
        List<String> result = new ArrayList<>();
        if (section == EngagementSection.Pc){
            for (PcEngagement value : PcEngagement.values()) {
                result.add(value.name().replace("_", " "));
            }
        }else if (section == EngagementSection.Engaging){
            for (EngagingEngagement value : EngagingEngagement.values()) {
                result.add(value.name().replace("_", " "));
            }
        }else if (section == EngagementSection.Power){
            for (PowerEngagement value : PowerEngagement.values()) {
                result.add(value.name().replace("_", " "));
            }
        }else if (section == EngagementSection.Now){
            for (NowEngagement value : NowEngagement.values()) {
                result.add(value.name().replace("_", " "));
            }
        }
        Collections.sort(result);
        return result;
    }


}
