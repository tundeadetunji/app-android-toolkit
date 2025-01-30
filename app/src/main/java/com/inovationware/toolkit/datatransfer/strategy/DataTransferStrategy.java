package com.inovationware.toolkit.datatransfer.strategy;

import android.content.Context;

import com.inovationware.toolkit.datatransfer.dto.request.ReadTextRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendFileRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendMeetingContributionRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendNoteRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.auxiliary.ReceiveTextUtility;

public interface DataTransferStrategy {
    void sendText(Context context, SharedPreferencesManager store, GroupManager machines, SendTextRequest entity, String errorMessageOutput, String failureMessageOutput);
    void sendMeetingContribution(Context context, SharedPreferencesManager store, GroupManager machines, SendMeetingContributionRequest entity, String errorMessageOutput, String failureMessageOutput);
    void sendNote(Context context, SharedPreferencesManager store, GroupManager machines, SendNoteRequest entity, String errorMessageOutput, String failureMessageOutput);

    void sendFile(Context context, SharedPreferencesManager store, GroupManager machines, SendFileRequest entity, String errorMessageOutput, String failureMessageOutput);

    String readText(Context context, SharedPreferencesManager store, GroupManager machines, ReadTextRequest entity, String errorMessageOutput, String failureMessageOutput);

    void setOnReceiveTextListener(ReceiveTextUtility utility);
}

