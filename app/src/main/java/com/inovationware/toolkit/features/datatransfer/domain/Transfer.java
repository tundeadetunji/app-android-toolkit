package com.inovationware.toolkit.features.datatransfer.domain;

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
        readWhatIsOn,
        readWhoIsTimestamp,
        readLast30,
        meetingGetIds,
        meetingGetMeeting,
        meetingGetContributions,
        meetingCreateMeeting,
        meetingContribute,
        meetingArchiveMeeting,
        meetingEnableMeeting,
        meetingDeleteMeeting,
        readHaptic,
        clearHaptic,
        readLocationRequest,
        readResumeWorkAppsListing
    }

    public enum Tag{
        meetingContribute;
    }
}
