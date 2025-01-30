package com.inovationware.toolkit.meeting.service;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.inovationware.toolkit.meeting.model.Meeting;
import com.inovationware.toolkit.meeting.model.Contribution;

import java.io.IOException;
import java.util.List;

public interface MeetingService {
    String getDisplayName(Context context);
    String getUsername(Context context);
    void createMeeting(Context context, Meeting meeting);
    void enableMeeting(Context context, String id) throws IOException;
    void archiveMeeting(Context context, String id) throws IOException;
    void getMeetingIds(Context context, AutoCompleteTextView dropdown);
    void getMeeting(Context context, String id, TextView textView);
    List<Contribution> getContributions(Context context, String id);
    void contribute(Context context, Contribution contribution) throws IOException;

    /*
    List<Chat> getBoard(Context context, String title);
    void deleteBoard(Context context, Board board);*/
}
