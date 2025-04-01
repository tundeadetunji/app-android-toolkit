package com.inovationware.toolkit.meeting.authority;

import android.content.Context;

import com.inovationware.toolkit.datatransfer.dto.request.ReadTextRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendMeetingContributionRequest;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Support;

import java.util.Map;

public class BoardAuthority {
    public enum InformationIs {
        Fetching_Ids,
        Fetching_Title,
        Creating_Meeting,
        Archiving_Meeting,
        Contributing,
        Enabling_Meeting,
        Fetching_Meeting_Details
    }

    public enum ExceptionIs {
        Error, Failure
    }

    private final String underscore = "_";
    private final String space = " ";
    private final String errorPrefix = "Error ";
    private final String failurePrefix = "Failure ";
    private final Map<ExceptionIs, String> exceptionIsStringMap = Map.of(ExceptionIs.Error, errorPrefix, ExceptionIs.Failure, failurePrefix);

    public String createExceptionMessage(ExceptionIs error, InformationIs information) {
        return exceptionIsStringMap.get(error) + String.valueOf(information).replace(underscore, space);
    }

    public String createExceptionMessage() {
        return "";
    }

    public String createErrorMessage(InformationIs information) {
        return createExceptionMessage(ExceptionIs.Error, information);
    }

    public String createErrorMessage() {
        return "";
    }

    public String createFailureMessage(InformationIs information) {
        return createExceptionMessage(ExceptionIs.Failure, information);
    }

    public String createFailureMessage() {
        return "";
    }

    public SendMeetingContributionRequest createSendContributionRequest(Context context, SharedPreferencesManager store, GroupManager machines, Transfer.Intent intent, String info, String tag, String meetingId) {
        return SendMeetingContributionRequest.create(
                DomainObjects.BASE_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                intent,
                store.getSender(context),
                machines.getDefaultDevice(context),
                DomainObjects.POST_PURPOSE_LOGGER,
                Support.determineMeta(context, store),
                info,
                tag,
                meetingId
        );
    }

    public ReadTextRequest createReadRequest(Context context, SharedPreferencesManager store, Transfer.Intent intent, String tag) {
        return ReadTextRequest.create(
                DomainObjects.BASE_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                intent,
                tag
        );
    }

}