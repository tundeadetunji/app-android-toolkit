package com.inovationware.toolkit.ui.activity;

import static com.inovationware.toolkit.global.library.utility.Code.content;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.meeting.model.Meeting;
import com.inovationware.toolkit.meeting.service.impl.MeetingServiceImpl;
import com.inovationware.toolkit.databinding.ActivityManageBoardBinding;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.ui.contract.BaseActivity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

public class ManageBoardActivity extends BaseActivity {
    private ActivityManageBoardBinding binding;
    private Context context;
    private MeetingServiceImpl service;
    private SharedPreferencesManager store;
    @Getter
    private Set<String> attendees;
    private Factory factory;
    private GroupManager machines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupProperties();
        setupListeners();
    }


    private void setupProperties() {
        context = binding.getRoot().getContext();
        machines = GroupManager.getInstance();
        factory = Factory.getInstance();
        store = SharedPreferencesManager.getInstance();
        service = new MeetingServiceImpl(factory, store, machines);
        attendees = new HashSet<>();
    }

    private void setupListeners() {

        binding.generateMeetingIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.meetingIdTextView.setText(
                        Code.newGUID(Code.IDPattern.Short_DateTime)
                );
            }
        });

        binding.addAttendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.attendeeDropDown.getText().toString().isEmpty()) return;
                if (!Code.isEmail(binding.attendeeDropDown.getText().toString())) {
                    Toast.makeText(context, "Invalid Email!", Toast.LENGTH_LONG).show();
                    return;
                }
                attendees.add(binding.attendeeDropDown.getText().toString());
                factory.ui.bindProperty(context, binding.attendeeDropDown, attendees.toArray(new String[0]), DomainObjects.EMPTY_STRING);
            }
        });

        binding.removeAttendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.attendeeDropDown.getText().toString().isEmpty()) return;
                attendees.remove(binding.attendeeDropDown.getText().toString());
                factory.ui.bindProperty(context, binding.attendeeDropDown, attendees.toArray(new String[0]), DomainObjects.EMPTY_STRING);
            }
        });

        binding.createMeetingButton.setOnClickListener(new View.OnClickListener() {
            //@SneakyThrows
            //@RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if (binding.meetingIdTextView.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Id is required.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (binding.meetingTitleTextView.getText().toString().isEmpty()) {
                    Toast.makeText(context, "A title is required.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (attendees.size() < 1) {
                    Toast.makeText(context, "Add at least one attendee.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!attendees.contains(store.getUsername(context)))
                    attendees.add(store.getUsername(context));

                Meeting meeting = Meeting.create(
                        binding.meetingIdTextView.getText().toString(),
                        store.getID(context),
                        service.getDisplayName(context),
                        service.getUsername(context),
                        binding.meetingTitleTextView.getText().toString(),
                        LocalDateTime.now(),
                        Code.getTimezone(),
                        true,
                        attendees.stream().collect(Collectors.joining(DomainObjects.NEW_LINE))
                );

                service.createMeeting(context, meeting);
            }
        });

        binding.loadMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.meetingIdsDropDown.getText().toString().isEmpty()) return;
                service.getMeeting(context, binding.meetingIdsDropDown.getText().toString(), binding.meetingDetailTextView);
            }
        });

        binding.getMeetingIdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.getMeetingIds(context, binding.meetingIdsDropDown);
            }
        });

        binding.enableMeetingButton.setOnClickListener(new View.OnClickListener() {
            /*@SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.O)*/
            @Override
            public void onClick(View v) {
                if (binding.meetingIdsDropDown.getText().toString().isEmpty()) return;
                service.enableMeeting(context, binding.meetingIdsDropDown.getText().toString().trim());
            }
        });

        binding.archiveMeetingButton.setOnClickListener(new View.OnClickListener() {
            /*@SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.O)*/
            @Override
            public void onClick(View v) {
                if (binding.meetingIdsDropDown.getText().toString().isEmpty()) return;
                service.archiveMeeting(context, binding.meetingIdsDropDown.getText().toString().trim());
            }
        });

        binding.copyMeetingIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.meetingIdsDropDown.getText().toString().isEmpty()) {
                    factory.device.toClipboard(binding.meetingIdsDropDown.getText().toString(), context);
                    Toast.makeText(context, "Meeting Id copied.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}