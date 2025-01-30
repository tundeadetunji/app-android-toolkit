package com.inovationware.toolkit.datatransfer.dto.request;


import com.inovationware.toolkit.global.domain.Transfer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadTextRequest {

    @Getter
    private String baseUrl;
    @Getter
    private String username;
    @Getter
    private String id;
    @Getter
    private Transfer.Intent intent;
    @Getter
    private String tag;

    public static ReadTextRequest create(String baseUrl, String username, String id, Transfer.Intent intent, String tag){
        return new ReadTextRequest(
                baseUrl, username, id, intent, tag
        );
    }
}
