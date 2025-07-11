package com.inovationware.toolkit.ui.support;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.Support;

public class EngageAuthority {
    private static EngageAuthority instance;

    private Context context;

    public static EngageAuthority getInstance(Context context) {
        if (instance == null) instance = new EngageAuthority(context);
        return instance;
    }

    private EngageAuthority(Context context) {
        this.context = context;
    }

    public String readPingUrl(){
        return Support.createUrl(context, Transfer.Intent.readPing, SharedPreferencesManager.getInstance());
    }
    public String getLast30Url(){
        return Support.createUrl(context, Transfer.Intent.readLast30, SharedPreferencesManager.getInstance());
        //return "https://tundeadetunji2017.bsite.net/audio.mp3";
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String createFilenameForDownloadedAudio() {
        //Todo coordinate with Desktop to get startTime, endTime probably writing it into timestamp
        return Support.createTimestampForFile() + ".mp3";
    }
    public String getWhatIsOnUrl() {
        return Support.createUrl(context, Transfer.Intent.readWhatIsOn, SharedPreferencesManager.getInstance());
    }
    public String getEngagementUrl() {
        return Support.createUrl(context, Transfer.Intent.readWhoIs, SharedPreferencesManager.getInstance());
    }

}
