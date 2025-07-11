package com.inovationware.toolkit.features.meeting.service.impl;

import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_LOGGER;
import static com.inovationware.toolkit.common.utility.Code.content;
import static com.inovationware.toolkit.common.utility.Support.determineMeta;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;

import android.content.Context;
import android.os.Build;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.features.meeting.authority.BoardAuthority;
import com.inovationware.toolkit.features.meeting.model.Contribution;
import com.inovationware.toolkit.features.meeting.model.Meeting;
import com.inovationware.toolkit.common.infrastructure.retrofit.Retrofit;
import com.inovationware.toolkit.common.infrastructure.retrofit.Repo;
import com.inovationware.toolkit.features.meeting.service.MeetingService;
import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.SignInManager;
import com.inovationware.toolkit.common.utility.Json;
import com.inovationware.toolkit.common.utility.Support;

import java.io.IOException;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingServiceImpl implements MeetingService {
    private final Factory factory;
    private final BoardAuthority authority = new BoardAuthority();

    private final SharedPreferencesManager store;
    private final GroupManager machines;

    public MeetingServiceImpl(Factory factory, SharedPreferencesManager store, GroupManager machines) {
        this.factory = factory;
        this.store = store;
        this.machines = machines;
    }

    @Override
    public String getDisplayName(Context context) {
        return SignInManager.getInstance().getSignedInUser(context).getName(); // + " from " + store.getSender(context);
    }

    @Override
    public String getUsername(Context context) {
        return store.getUsername(context);
    }

    @Override
    public void createMeeting(Context context, Meeting meeting) {

        if (meeting == null) return;

        String content = "";

        try {
            content = Json.from(meeting);
        } catch (IOException ignored) {}

        if (content.isEmpty()) {
            Toast.makeText(context, "Internal Error - WIP!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Support.canTransferData(context, store, machines)) return;

        factory.transfer.service.sendText(
                context,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        Transfer.Intent.meetingCreateMeeting,
                        store.getSender(context),
                        determineTarget(context, store, machines),
                        POST_PURPOSE_LOGGER,
                        determineMeta(context, store),
                        content,
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    @Override
    public void enableMeeting(Context context, String meetingId) {
        factory.transfer.service.sendText(
                context,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        Transfer.Intent.meetingEnableMeeting,
                        store.getSender(context),
                        determineTarget(context, store, machines),
                        POST_PURPOSE_LOGGER,
                        determineMeta(context, store),
                        meetingId,
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    @Override
    public void archiveMeeting(Context context, String meetingId) {
        factory.transfer.service.sendText(
                context,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        Transfer.Intent.meetingArchiveMeeting,
                        store.getSender(context),
                        determineTarget(context, store, machines),
                        POST_PURPOSE_LOGGER,
                        determineMeta(context, store),
                        meetingId,
                        DomainObjects.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    @Override
    public void getMeetingIds(Context context, AutoCompleteTextView dropdown) {
        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        if (!Support.canTransferData(context, store, machines)) return;
        Call<String> navigate = retrofitImpl.readText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                String.valueOf(Transfer.Intent.meetingGetIds),
                DomainObjects.EMPTY_STRING
        );
        navigate.enqueue(new Callback<String>() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    if (response.body() == null) return;
                    factory.ui.bindProperty(context, dropdown, response.body().split(DomainObjects.NEW_LINE));

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

    }

    @Override
    public void getMeeting(Context context, String meetingId, TextView textView) {
        if (!Support.canTransferData(context, store, machines)) return;

        Retrofit retrofitImpl = Repo.getInstance().create(context, store);

        Call<String> navigate = retrofitImpl.readText(
                HTTP_TRANSFER_URL(context, store),
                store.getUsername(context),
                store.getID(context),
                String.valueOf(Transfer.Intent.meetingGetMeeting),
                meetingId
        );
        navigate.enqueue(new Callback<String>() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    if (response.body() == null) return;
                    Meeting meeting = Meeting.from(response.body());
                    textView.setText(meeting.toString());

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

    }

    @Override
    public List<Contribution> getContributions(Context context, String meetingId) {
        return null;
    }

    @Override
    public void contribute(Context context, Contribution contribution) throws IOException {
        if (contribution == null) return;

        String content = "";

        try {
            content = Json.from(contribution);
        } catch (IOException ignored) {}

        if (content.isEmpty()) {
            Toast.makeText(context, "Internal Error - WIP!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Support.canTransferData(context, store, machines)) return;

        factory.transfer.service.sendText(
                context,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        Transfer.Intent.meetingContribute,
                        store.getSender(context),
                        determineTarget(context, store, machines),
                        POST_PURPOSE_LOGGER,
                        determineMeta(context, store),
                        content,
                        contribution.getMeetingId()
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }
}

