package com.inovationware.toolkit.datatransfer.strategy.rest;

import android.content.Context;
import android.os.Build;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.datatransfer.dto.request.ReadTextRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendFileRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendMeetingContributionRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendNoteRequest;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.datatransfer.strategy.DataTransferStrategy;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.auxiliary.ReceiveTextUtility;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inovationware.toolkit.global.library.utility.Code.content;

import androidx.annotation.RequiresApi;

public class RestDataTransferStrategy implements DataTransferStrategy {

    private static RestDataTransferStrategy instance;
    private ReceiveTextUtility utility;

    private RestDataTransferStrategy(){
        //super();
    }

    public static RestDataTransferStrategy getInstance(){
        if(instance == null){
            instance = new RestDataTransferStrategy();
        }
        return instance;
    }

    @Override
    public void sendText(Context context, SharedPreferencesManager store, GroupManager machines,  SendTextRequest entity, String errorMessageOutput, String failureMessageOutput) {

        if (entity == null) return;

        if (!Support.canTransferData(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate =
                retrofitImpl.sendText(
                        entity.getBaseUrl(),
                        entity.getUsername(),
                        entity.getId(),
                        String.valueOf(entity.getIntent()),
                        entity.getSender(),
                        entity.getTarget(),
                        entity.getPurpose(),
                        entity.getMeta(),
                        entity.getInfo(),
                        entity.getTag()
                );

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(context).toast(response.body());
                } else {
                    if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                        new Feedback(context).toast(errorMessageOutput);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                    new Feedback(context).toast(failureMessageOutput);
                }
            }
        });

    }

    @Override
    public void sendMeetingContribution(Context context, SharedPreferencesManager store, GroupManager machines, SendMeetingContributionRequest entity, String errorMessageOutput, String failureMessageOutput) {
        if (entity == null) return;

        if (!Support.canTransferData(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate =
                retrofitImpl.sendMeetingContribution(
                        entity.getBaseUrl(),
                        entity.getUsername(),
                        entity.getId(),
                        String.valueOf(entity.getIntent()),
                        entity.getSender(),
                        entity.getTarget(),
                        entity.getPurpose(),
                        entity.getMeta(),
                        entity.getInfo(),
                        entity.getTag(),
                        entity.getMeetingId()
                );

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(context).toast(response.body());
                } else {
                    if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                        new Feedback(context).toast(errorMessageOutput);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                    new Feedback(context).toast(failureMessageOutput);
                }
            }
        });

    }

    @Override
    public void sendNote(Context context, SharedPreferencesManager store, GroupManager machines,  SendNoteRequest entity, String errorMessageOutput, String failureMessageOutput) {

        if (entity == null) return;

        if (!Support.canTransferData(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate =
                retrofitImpl.sendNote(
                        entity.getBaseUrl(),
                        entity.getUsername(),
                        entity.getId(),
                        String.valueOf(entity.getIntent()),
                        entity.getSender(),
                        entity.getTarget(),
                        entity.getPurpose(),
                        entity.getMeta(),
                        entity.getInfo(),
                        entity.getNote(),
                        entity.getNoteId(),
                        entity.getNoteTitle(),
                        entity.getNoteDate(),
                        entity.getNoteTime()
                );

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(context).toast(response.body());
                } else {
                    if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                        new Feedback(context).toast(errorMessageOutput);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                    new Feedback(context).toast(failureMessageOutput);
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void sendFile(Context context, SharedPreferencesManager store, GroupManager machines,  SendFileRequest entity, String errorMessageOutput, String failureMessageOutput) {
        if(entity == null) return;

        if (!Support.canTransferData(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.sendFile(
                entity.getBaseUrl(),
                entity.getUsername(),
                entity.getId(),
                String.valueOf(entity.getIntent()),
                entity.getFile(),
                entity.getFilename(),
                Code.createTimestamp(),
                entity.getPurpose()
        );

        navigate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        if (!response.body().isEmpty())
                            new Feedback(context).toast(response.body());
                } else {
                    if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                        new Feedback(context).toast(errorMessageOutput);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)){
                    new Feedback(context).toast(failureMessageOutput);
                }
            }
        });

    }

    @Override
    public String readText(Context context, SharedPreferencesManager store, GroupManager machines,  ReadTextRequest entity, String errorMessageOutput, String failureMessageOutput) {
        throw new RuntimeException("WIP");
        /*if (entity == null) throw new RuntimeException("entity is null!");

        final String[] result = {""};

        Call<String> navigate = retrofitImpl.readText(
                entity.getBaseUrl(),
                entity.getUsername(),
                entity.getId(),
                String.valueOf(entity.getIntent()),
                entity.getTag()
        );
        navigate.enqueue(new Callback<String>() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    if (response.body() == null) return;

                    if (response.body().trim().length() < 1) return;

                    result[0] = response.body();

                } else {
                    if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                        new Feedback(context).toast(DEFAULT_ERROR_MESSAGE_SUFFIX);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                    new Feedback(context).toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
                }
            }
        });

        return result[0];*/
    }

    @Override
    public void setOnReceiveTextListener(ReceiveTextUtility utility){
        this.utility = utility;
    }

    /*public interface ReceiveTextUtility {
        void handleReceivedText(String received);
    }*/
}


