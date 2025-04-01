package com.inovationware.toolkit.meeting.model;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.external.Json;

import java.io.IOException;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Meeting {
    @JsonProperty
    private String id;
    @JsonProperty
    private String groupId; //store.getId
    @JsonProperty
    private String moderatorDisplayName;
    @JsonProperty
    private String moderatorUsername;
    @JsonProperty
    private String title;
    @JsonProperty
    private String createdAt;
    @JsonProperty
    private String timezone;
    @JsonProperty
    private boolean enabled;
    @JsonProperty
    private String attendees;
    //@RequiresApi(api = Build.VERSION_CODES.O)

    public static Meeting create(String id, String groupId, String moderatorDisplayName, String moderatorUsername, String title, LocalDateTime createdAt, String timezone, boolean enabled, String attendees){
        //Todo meetingId should include username, to avoid duplicates
        return Meeting.builder()
                .id(id)
                .groupId(groupId)
                .moderatorDisplayName(moderatorDisplayName)
                .moderatorUsername(moderatorUsername)
                .title(title)
                .createdAt(String.valueOf(createdAt))
                .timezone(timezone)
                .enabled(enabled)
                .attendees(attendees)
                .build();
    }

    public static Meeting from(String json) throws IOException {
        return Json.to(json, Meeting.class);
    }

    @NonNull
    @Override
    public String toString() {
        //SimpleDateFormat formatter = new SimpleDateFormat("h mm tt, d MMM yyyy");
        int in_attendance = this.attendees.split(DomainObjects.NEW_LINE).length;
        return new StringBuilder()
                .append(this.title)
                .append(DomainObjects.NEW_LINE)
                .append("moderated by ").append(this.moderatorDisplayName)
                .append(DomainObjects.NEW_LINE)
                .append(" (").append(this.moderatorUsername).append(")")
                .append(DomainObjects.NEW_LINE)
                .append(DomainObjects.NEW_LINE)
                //.append("Created: ").append(formatter.format(this.createdAt))
                .append("created ").append(this.createdAt)
                .append(DomainObjects.NEW_LINE)
                .append("(").append(this.timezone).append(")")
                .append(DomainObjects.NEW_LINE)
                .append(DomainObjects.NEW_LINE)
                .append(in_attendance).append(in_attendance == 1 ? " person " : " people ") .append("in attendance")
                .append(DomainObjects.NEW_LINE)
                .append("This meeting is").append(this.enabled ? " active." : " archived.")
                .toString();
    }
}
