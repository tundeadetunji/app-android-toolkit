package com.inovationware.toolkit.global.domain;

public class Transfer {

    public enum Intent {
        readText,
        writeText,
        readFile,
        writeFile,
        readApps,
        writeApps,
        userExists,
        userIsEnabled,
        readNetTimerTasks,
        writeNote,
        readNote,
        clearNote,
        deleteNote,
        readWhoIs,
        readPing,
        readWhoIsTimestamp,
        readLast30,
        meetingGetIds,
        meetingGetMeeting,
        meetingGetContributions,
        meetingCreateMeeting,
        meetingContribute,
        meetingArchiveMeeting,
        meetingEnableMeeting,
        meetingDeleteMeeting
    }

    public enum Tag{
        meetingContribute;
    }
}
