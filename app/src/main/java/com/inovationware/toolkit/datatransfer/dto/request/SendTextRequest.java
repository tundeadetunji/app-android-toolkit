package com.inovationware.toolkit.datatransfer.dto.request;

import com.inovationware.toolkit.global.domain.Transfer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendTextRequest {


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

    public static SendTextRequest create(String baseUrl, String username, String id, Transfer.Intent intent, String sender, String target, String purpose, String meta, String info, String tag) {
        return new SendTextRequest(
                baseUrl, username, id, intent, sender, target, purpose, meta, info, tag);
    }
}
