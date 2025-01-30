package com.inovationware.toolkit.datatransfer.dto.response;

import java.io.IOException;

import static com.inovationware.toolkit.global.library.utility.Code.stringToList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inovationware.toolkit.global.library.external.Json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public final class ResponseEntity {
    public static Envelope from(String json) throws IOException {
        return Json.to(json, Envelope.class);
    }

    @Getter
/*
    @AllArgsConstructor
    @NoArgsConstructor
*/
    public static class Envelope {
        @JsonProperty
        private String Sender;
        @JsonProperty
        private String Target;
        @JsonProperty
        private String Purpose;
        @JsonProperty
        private String Meta;
        @JsonProperty
        private String Info;
        @JsonProperty
        private String Filename;
        @JsonProperty
        private String Timestamp;

/*
    @NonNull
    @Override
    public String toString() {
        return "Sender: " + getSender() + ", Target: " + getTarget() + ", Purpose: " + getPurpose() + ", Meta: " + getMeta() + ", Info: " + getInfo();
    }
*/

    }

}
