package com.inovationware.toolkit.features.datatransfer.strategy;

import android.content.Context;

import com.inovationware.toolkit.features.datatransfer.dto.request.ReadTextRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendFileRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendMeetingContributionRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendNoteRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.ReceiveTextUtility;

public interface DataTransferStrategy {
    void sendText(Context context, SharedPreferencesManager store, GroupManager machines, SendTextRequest entity, String errorMessageOutput, String failureMessageOutput);
    void sendMeetingContribution(Context context, SharedPreferencesManager store, GroupManager machines, SendMeetingContributionRequest entity, String errorMessageOutput, String failureMessageOutput);
    void sendNote(Context context, SharedPreferencesManager store, GroupManager machines, SendNoteRequest entity, String errorMessageOutput, String failureMessageOutput);

    void sendFile(Context context, SharedPreferencesManager store, GroupManager machines, SendFileRequest entity, String errorMessageOutput, String failureMessageOutput);

    String readText(Context context, SharedPreferencesManager store, GroupManager machines, ReadTextRequest entity, String errorMessageOutput, String failureMessageOutput);

    void setOnReceiveTextListener(ReceiveTextUtility utility);
}

