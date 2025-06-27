package com.inovationware.toolkit.features.datatransfer.service.rest;

import android.content.Context;

import com.inovationware.toolkit.features.datatransfer.dto.request.ReadTextRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendFileRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendMeetingContributionRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendNoteRequest;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.ReceiveTextUtility;
import com.inovationware.toolkit.features.datatransfer.service.DataTransferService;
import com.inovationware.toolkit.features.datatransfer.strategy.DataTransferStrategy;

public class RestDataTransferService implements DataTransferService {
    private static RestDataTransferService instance;

    private DataTransferStrategy strategy;

    private RestDataTransferService(DataTransferStrategy strategy) {
        this.strategy = strategy;
    }

    public static RestDataTransferService getInstance(DataTransferStrategy strategy) {
        if (instance == null) {
            instance = new RestDataTransferService(strategy);
        }
        return instance;
    }


    public void setStrategy(DataTransferStrategy strategy) {
        this.strategy = strategy;
    }


    @Override
    public void sendText(Context context, SharedPreferencesManager store, GroupManager machines, SendTextRequest entity, String errorMessageOutput, String failureMessageOutput) {
        strategy.sendText(context, store, machines, entity, errorMessageOutput, failureMessageOutput);
    }

    @Override
    public void sendMeetingContribution(Context context, SharedPreferencesManager store, GroupManager machines, SendMeetingContributionRequest entity, String errorMessageOutput, String failureMessageOutput) {
        strategy.sendMeetingContribution(context, store, machines, entity, errorMessageOutput, failureMessageOutput);
    }

    @Override
    public void sendNote(Context context, SharedPreferencesManager store, GroupManager machines,  SendNoteRequest entity, String errorMessageOutput, String failureMessageOutput) {
        strategy.sendNote(context, store, machines, entity, errorMessageOutput, failureMessageOutput);
    }

    @Override
    public void sendFile(Context context, SharedPreferencesManager store, GroupManager machines,  SendFileRequest entity, String errorMessageOutput, String failureMessageOutput) {
        strategy.sendFile(context, store, machines, entity, errorMessageOutput, failureMessageOutput);
    }

    @Override
    public String readText(Context context, SharedPreferencesManager store, GroupManager machines,  ReadTextRequest entity, String errorMessageOutput, String failureMessageOutput) {
        return strategy.readText(context, store, machines, entity, errorMessageOutput, failureMessageOutput);
    }


    @Override
    public void setOnReceiveTextListener(ReceiveTextUtility utility) {
        strategy.setOnReceiveTextListener(utility);
    }

}

