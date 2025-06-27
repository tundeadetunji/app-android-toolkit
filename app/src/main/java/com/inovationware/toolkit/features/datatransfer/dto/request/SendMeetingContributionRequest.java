package com.inovationware.toolkit.features.datatransfer.dto.request;

import com.inovationware.toolkit.features.datatransfer.domain.Transfer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendMeetingContributionRequest {
    @Getter
    private String baseUrl;
    @Getter
    private String username;
    @Getter
    private String id;
    @Getter
    private Transfer.Intent intent;
    @Getter
    private String sender;
    @Getter
    private String target;
    @Getter
    private String purpose;
    @Getter
    private String meta;
    @Getter
    private String info;
    @Getter
    private String tag;
    @Getter
    private String meetingId;

    public static SendMeetingContributionRequest create(String baseUrl, String username, String id, Transfer.Intent intent, String sender, String target, String purpose, String meta, String info, String tag, String meetingId) {
        return new SendMeetingContributionRequest(
                baseUrl, username, id, intent, sender, target, purpose, meta, info, tag, meetingId);
    }

}
