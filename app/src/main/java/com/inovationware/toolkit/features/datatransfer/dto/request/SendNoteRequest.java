package com.inovationware.toolkit.features.datatransfer.dto.request;

import com.inovationware.toolkit.features.datatransfer.domain.Transfer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendNoteRequest {


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
    private String note;
    @Getter
    private String noteId;
    @Getter
    private String noteTitle;
    @Getter
    private String noteDate;
    @Getter
    private String noteTime;

    public static SendNoteRequest create(String baseUrl, String username, String id, Transfer.Intent intent, String sender, String target, String purpose, String meta, String info, String note, String noteId, String noteTitle, String noteDate, String noteTime) {
        return new SendNoteRequest(
                baseUrl, username, id, intent, sender, target, purpose, meta, info, note, noteId, noteTitle, noteDate, noteTime);
    }
}
