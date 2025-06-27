package com.inovationware.toolkit.features.meeting.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.common.utility.Json;
import com.inovationware.toolkit.common.utility.Code;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Contribution {
    @JsonProperty
    private String id;
    @JsonProperty
    private String meetingId;
    @JsonProperty
    private String contributor;
    @JsonProperty
    private String contributedAt;
    @JsonProperty
    private String timezone;
    @JsonProperty
    private String contribution;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Contribution create(String meetingId, String contributor, LocalDateTime contributedAt, String timezone, String contribution){
        return Contribution.builder()
                .id(Code.newGUID(Code.IDPattern.Short))
                .meetingId(meetingId)
                .contributor(contributor)
                .contributedAt(String.valueOf(contributedAt))
                .timezone(timezone)
                .contribution(contribution)
                .build();
    }

    public static List<Contribution> getListing(String response) {
        try{
            return Json.asList(response, Contribution.class);
        }catch (IOException exception){
            throw new RuntimeException("may need to use TypeReference in Json.asList()");
            //return new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public String toString() {
        //SimpleDateFormat formatter = new SimpleDateFormat("h mm tt, d MMM yyyy");

        return new StringBuilder()
                .append("\n\n")
                .append(this.contribution)
                .append("\n\n")
                .append(" said " + this.contributor)
                .append(DomainObjects.NEW_LINE)
                .append(" at " + this.contributedAt)
                .append(DomainObjects.NEW_LINE)
                .append(" from " + this.timezone)
                .append("\n\n")
                .toString();
    }
}
