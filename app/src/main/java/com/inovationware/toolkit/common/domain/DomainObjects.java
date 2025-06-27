package com.inovationware.toolkit.common.domain;

import android.content.Context;
import android.content.pm.PackageManager;

import com.inovationware.toolkit.features.bistable.service.BistableManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.ApkClient;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.nettimer.model.NetTimerObject;
import com.inovationware.toolkit.common.infrastructure.tts.service.TTSService;
import com.inovationware.toolkit.common.utility.Code;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;


public class DomainObjects {

    public static String[] resumeAppsListing;
    public static final String START_LOCATION_UPDATES_MESSAGE = "Have a nice day!";

    public static List<String> apps = new ArrayList<>();
    public static List<NetTimerObject> net_timer_objects = new ArrayList<>();

    public static final String EMPTY_STRING = "";
    public static final String NULL = "Null";
    public static final String IGNORE = "Ignore";
    public static final String value_saved = "Value has been saved";
    public static final String WELCOME = "Welcome";
    public static final String yes = "Yes";
    public static final String Method_Not_Supported_Yet = "This method is not supported yet.";
    public static final String sure = "Sure";
    public static final String exactly = "Exactly!";
    public static final String no = "No";
    public static final String ok = "OK";
    public static final String gotIt = "Got It";
    public static final String cancel = "Cancel";
    public static final String never_mind = "Never mind";

    public static final String PostHeaderFilename = "PostFileName";
    public static final String PostHeaderContentType = "PostContentType";
    public static final String PostHeaderTimestamp = "PostTimestamp";
    public static final String BASE_URL(Context context, SharedPreferencesManager store){
        return store.getBaseUrl(context);
    }
    public static final String HTTP_TRANSFER_URL(Context context, SharedPreferencesManager store){
        return BASE_URL(context, store) + "/api/regular.ashx";
    }
    public static final String DEFAULT_ERROR_MESSAGE_SUFFIX = " didn't go so well!";
    public static final String DEFAULT_FAILURE_MESSAGE_SUFFIX = " resulted in an error!";

    public static final String SHARED_PREFERENCES_USERNAME_KEY = "USERNAME";

    public static final String DTO_CLASS_STRING = "CLASS_STRING";
    public static final String REQUIRED_FIELDS_MISSING_STRING = "Required field or fields missing.";
    public static final String SHARED_PREFERENCES_BASE_URL_KEY = "BASE_URL";
    public static final String SHARED_PREFERENCES_ID_KEY = "ID";
    public static final String SHARED_PREFERENCES_MACHINES_KEY = "MACHINES";
    public static final String SHARED_PREFERENCES_THEME_KEY = "THEME";
    public static final String PINKY = "Pinky";
    public static final String DARKER = "Darker";
    public static final String NATURAL = "Natural";
    public static final String TAN = "Tan";
    public static final String BLUISH = "Bluish";
    public static final String AGRELLITE = "Agrellite";
    public static final String THROWBACK = "Throwback";
    public static final String CHOSEN = "Chosen";
    public static final String WARM = "Warm";
    public static final String FLUORITE = "Fluorite";
    public static final String SHARED_PREFERENCES_MEMO_TITLES_KEY = "MEMO_TITLES_KEY";
    public static final String SHARED_PREFERENCES_SITES_KEY = "SITES";
    public static final String SHARED_PREFERENCES_DEFAULT_DEVICE = "DEFAULT_MACHINE";
    public static final String SHARED_PREFERENCES_TARGET_MODE = "TARGET_MODE";
    public static final String SHARED_PREFERENCES_ENCRYPTION_SALT = "ENCRYPTION_SALT";
    public static final String SHARED_PREFERENCES_ENCRYPTION_PASSWORD = "ENCRYPTION_PASSWORD";
    public static final String SHARED_PREFERENCES_GITHUB_OWNER = "GITHUB_OWNER";
    public static final String SHARED_PREFERENCES_GITHUB_TOKEN = "GITHUB_TOKEN";
    public static final String SHARED_PREFERENCES_GITHUB_DEFAULT_REPOSITORY = "GITHUB_DEFAULT_REPOSITORY";
    public static final String SHARED_PREFERENCES_GITHUB_DEFAULT_BRANCH = "GITHUB_DEFAULT_BRANCH";
    public static final String SHARED_PREFERENCES_SENDER = "SENDER";
    public static final String SHARED_PREFERENCES_DEFAULT_NOTE_TITLE_KEY = "DEFAULT_NOTE_TITLE";
    public static final String SHARED_PREFERENCES_DEFAULT_NOTE_TITLE = "Scratch";
    public static final String SHARED_PREFERENCES_REMOTE_LINK_APPS_KEY = "APPS";
    public static final String SHARED_PREFERENCES_PERIODIC_UPDATES_KEY = "PERIODIC_UPDATES";
    public static final String SHARED_PREFERENCES_SEARCH_ON_RECEIVE_KEY = "SEARCH_ON_RECEIVE";
    public static final String SHARED_PREFERENCES_SEND_TO_CLIPBOARD_ON_RECEIVE_KEY = "SEND_TO_CLIPBOARD_ON_RECEIVE";
    public static final String SHARED_PREFERENCES_DISPLAY_ERROR_MESSAGE = "DISPLAY_ERROR_MESSAGE";
    public static final String SHARED_PREFERENCES_APPEND_UUID_WHEN_SENDING = "APPEND_UUID";
    public static final String SHARED_PREFERENCES_APPEND_TIMEZONE_WHEN_SENDING = "APPEND_TIMEZONE";
    public static final String SHARED_PREFERENCES_PROMPT_TO_SYNC_NOTE = "SYNC_NOTE";
    public static final String SHARED_PREFERENCES_SHOW_VOCABULARY = "SHOW_VOCABULARY";
    public static final String SHARED_PREFERENCES_READ_OUT_LOUD_ON_RECEIVE_KEY = "READ_OUT_LOUD_ON_RECEIVE";
    public static final String SHARED_PREFERENCES_LAST_VISITED_SITE_KEY = "LAST_VISITED_SITE";
    public static final String SHARED_PREFERENCES_LAST_SEARCH_TERM_KEY = "LAST_SEARCH_TERM";
    public static final String SHARED_PREFERENCES_NET_TIMER_REPLY_APPEND_ORIGINAL_KEY = "NET_TIMER_REPLY_APPEND_ORIGINAL";

    public static final String SHARED_PREFERENCES_FIREBASE_TOKEN_KEY = "FIREBASE_TOKEN_KEY";

    //public static final String SHARED_PREFERENCES_LOCAL_TASK_KEYS_KEY = "NET_TIMER_MOBILE_REGULAR_KEYS";

    public static final String SHARED_PREFERENCES_LOCAL_TASK_REGULAR = "NET_TIMER_MOBILE_REGULAR";
    public static final String SHARED_PREFERENCES_LOCAL_TASK_REVERSE = "NET_TIMER_MOBILE_REVERSE";
    public static final String SHARED_PREFERENCES_LOCAL_TASK_REGULAR_INTERVAL_KEY = "NET_TIMER_MOBILE_REGULAR_INTERVAL";
    public static final String SHARED_PREFERENCES_LOCAL_TASK_REGULAR_TIME_UNIT_KEY = "NET_TIMER_MOBILE_REGULAR_TIME_UNIT";

    public static final String SHARED_PREFERENCES_LOCAL_TASK_REVERSE_INTERVAL_KEY = "NET_TIMER_MOBILE_REVERSE_INTERVAL";
    public static final String SHARED_PREFERENCES_LOCAL_TASK_REVERSE_TIME_UNIT_KEY = "NET_TIMER_MOBILE_REVERSE_TIME_UNIT";

    public static final String SHARED_PREFERENCES_LOCAL_TASK_READ_OUT_LOUD_REPEAT_COUNT_KEY = "NET_TIMER_MOBILE_READ_OUT_LOUD_REPEAT_COUNT";
    public static final String SHARED_PREFERENCES_LOCAL_TASK_REPEAT_KEY = "NET_TIMER_MOBILE_REPEAT";

    public static final String SHARED_PREFERENCES_READING_KEY = "READING";
    public static final String SHARED_PREFERENCES_RUNNING_KEY = "RUNNING";
    public static final String SHARED_PREFERENCES_TODO_KEY = "TODO";
    public static final String SHARED_PREFERENCES_SCRATCH_KEY = "SCRATCH";
    public static final String SHARED_PREFERENCES_FAVORITE_URL_KEY = "FAVORITE_URL";
    public static final String SHARED_PREFERENCES_PINNED_KEY = "PINNED";

    public static final String SHARED_PREFERENCES_MEMO_CACHED_KEY = "MEMO_CACHED";
    public static final String SHARED_PREFERENCES_MEMO_CACHED_LENGTH_KEY = "MEMO_CACHED_LENGTH";
    public static final String SHARED_PREFERENCES_LAST_RESUME_WORK_APP = "LAST_RESUME_WORK_APP";

    public static List<Memo> cachedMemos = new ArrayList<>();

    public enum SendFrom {
        TextView, Clipboard
    }

    public static TTSService ttsServiceProvider;

    /*public static final Retrofit retrofitImpls = new retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(Retrofit.class);*/

    public static boolean NET_TIMER_NOTIFICATION_SERVICE_IS_RUNNING = false;


    //public static SharedPreferencesAdapter store;
    //public static MachineManager machines;
    //public static SiteManager sites;

    //public static boolean netTimerMobileServiceIsRunning = false;
//    public static BistableCommand bistable;
    public static BistableManager bistableManager;
    public static final String MINUTES = TimeUnit.MINUTES.toString();
    public static final String HOURS = TimeUnit.HOURS.toString();
    public static final String SECONDS = TimeUnit.SECONDS.toString();

    public static final String MINUTES_CAPITALIZED = Code.capitalize(TimeUnit.MINUTES.toString());
    public static final String HOURS_CAPITALIZED = Code.capitalize(TimeUnit.HOURS.toString());
    public static final String SECONDS_CAPITALIZED = Code.capitalize(TimeUnit.SECONDS.toString());

    public static final String[] DATA_TRANSFER_OBJECTS = new String[]{"Audio", "Excel", "PDF", "Picture", "PowerPoint", "RAR", "Text", "Video", "Word"};
    public static final String[] TIME_UNITS = new String[]{MINUTES_CAPITALIZED, HOURS_CAPITALIZED, SECONDS_CAPITALIZED};


    public static final String HIBERNATE = "Hibernate";
    public static final String LAST_THIRTY = "Last 30 seconds";
    public static final String LOCK = "Workstation";
    public static final String WORKSTATION = "Workstation";
    public static final String MUTE = "Mute";
    public static final String RESTART = "Restart";
    public static final String SHUTDOWN = "Shutdown";
    public static final String WHO_IS = "Who Is";
    public static final String[] BISTABLE_MODES = new String[]{
            HIBERNATE,
            LAST_THIRTY,
            LOCK,
            MUTE,
            RESTART,
            SHUTDOWN,
            WHO_IS
    };


    public static final String[] TARGET_MODES = new String[]{"To Device", "To Group"};
    public static final String TARGET_MODE_TO_DEVICE = TARGET_MODES[0];
    public static final String TARGET_MODE_TO_GROUP = TARGET_MODES[1];

    public static final String AUDIO = "Audio";
    public static final String EXCEL = "Excel";
    public static final String PDF = "PDF";
    public static final String PICTURE = "Picture";
    public static final String POWERPOINT = "PowerPoint";
    public static final String RAR = "RAR";
    public static final String TEXT = "Text";
    public static final String VIDEO = "Video";
    public static final String WORD = "Word";

    public static final String MIME_TYPE_AUDIO = "audio/mpeg";
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String MIME_TYPE_EXCEL = "application/vnd.ms-excel";
    public static final String MIME_TYPE_PICTURE = "image/jpeg";
    public static final String MIME_TYPE_POWERPOINT = "application/vnd.ms-powerpoint";
    public static final String MIME_TYPE_RAR = "application/zip";
    public static final String MIME_TYPE_VIDEO = "video/mp4";
    public static final String MIME_TYPE_WORD = "application/msword";
    public static final String MIME_TYPE_ANY = "*/*";

    public static final String POST_RECORD_SENDER = "Sender";
    public static final String POST_RECORD_TARGET = "Target";
    public static final String POST_RECORD_PURPOSE = "Purpose";
    public static final String POST_RECORD_INFO = "Info";
    public static final String POST_PURPOSE_NEW_NET_TIMER_TASK = "NewNetTimerTask";
    public static final String POST_PURPOSE_RESUME_WORK = "ResumeWork";
    public static final String POST_PURPOSE_BLOB_AUDIO = "BlobAudio";
    public static final String POST_PURPOSE_BLOB_EXCEL = "BlobExcelSpreadsheet";
    public static final String POST_PURPOSE_BLOB_PDF = "BlobPdf";
    public static final String POST_PURPOSE_BLOB_PICTURE = "BlobPicture";
    public static final String POST_PURPOSE_BLOB_POWERPOINT = "BlobPowerpoint";
    public static final String POST_PURPOSE_BLOB_WORD = "BlobWordDocument";
    public static final String POST_PURPOSE_BLOB_RAR = "BlobRar";
    public static final String POST_PURPOSE_BLOB_VIDEO = "BlobVideo";
    public static final String POST_PURPOSE_LOGGER = "Logger";
    public static final String POST_PURPOSE_REGULAR = "Regular";
    public static final String POST_PURPOSE_SINGLE_SOS = "SingleSos";
    public static final String POST_PURPOSE_PERIODIC_SOS = "PeriodicSos";
    public static final String POST_PURPOSE_DIM_SCREEN = "DimScreen";
    public static final String POST_PURPOSE_INTERACTION = "Interaction";
    public static final String POST_PURPOSE_APP = "App";
    public static final String POST_PURPOSE_ENGAGE = "Engage";
    public static final String POST_PURPOSE_PING = "Ping";
    public static final String POST_PURPOSE_LAST_30 = "Last30";
    public static final String POST_PURPOSE_WHAT_IS_ON = "WhatIsOn";
    public static final String POST_PURPOSE_EMPHASIZE = "Emphasize";
    public static final String POST_PURPOSE_READ_NOTE = "readNote";
    public static final String POST_PURPOSE_WRITE_NOTE = "writeNote";
    public static final String POST_PURPOSE_CONTINUE_MEDIA = "continueMedia";
    public static final String POST_PURPOSE_CONTINUE_WORK = "continueWork";
    public static final String POST_PURPOSE_CONTINUE_PROGRAMS = "continuePrograms";
    public static final String POST_PURPOSE_INFORM = "Inform";
    public static final String POST_PURPOSE_UPDATE = "UpdateFile";
    public static final String POST_PURPOSE_CREATE = "CreateFile";
    public static final String POST_PURPOSE_CREATE_SCHEDULE = "CreateSchedule";
    public static final String POST_PURPOSE_NET_TIMER = "NetTimer";
    public static final String POST_PURPOSE_NET_TIMER_INFORM = "NetTimerInform";
    public static final String POST_PURPOSE_NET_TIMER_EMPHASIZE = "NetTimerEmphasize";
    public static final String POST_PURPOSE_NET_TIMER_UPDATE_TOKEN = "NetTimerUpdateToken";

    public static final String NET_TIMER_NOTIFICATION_TICKER = "Net Timer Update";
    public static final int NET_TIMER_NOTIFICATION_ID = 997;
    public static final String NET_TIMER_NOTIFICATION_CHANNEL_ID = "997";
    public static final String NET_TIMER_NOTIFICATION_CHANNEL_NAME = "ToolkitNetTimerNotification";
    public static final String NET_TIMER_NOTIFICATION_CHANNEL_DESCRIPTION = "ToolkitNetTimerNotification";

    public static final String HEADLINE = "headline";
    public static final String DETAILS = "details";
    public static final String TIME_STRING = "time_string";
    public static final String ZONE_STRING = "zone_string";
    public static final String NET_TIMER_REPLY_DELIMITER = "Original message was";
    private static final String INFO_COPIED_TO_CLIPBOARD_SUFFIX = " copied to clipboard.";
    public static String copiedToClipboardMessage(@Nullable String description){
        return description == null ?  (INFO_COPIED_TO_CLIPBOARD_SUFFIX.substring(1,1).toUpperCase() + INFO_COPIED_TO_CLIPBOARD_SUFFIX.substring(2)).trim() : description + INFO_COPIED_TO_CLIPBOARD_SUFFIX;
    }
    public static final String CODE = "Code";
    public static final String CONVERSION = "Conversion";

    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";

    public static final String BG_NOTIFICATION_TITLE = "Data Transfer";
    public static final String BG_NOTIFICATION_TICKER = "Data Transfer";
    public static final String BG_NOTIFICATION_CONTENT = "CONTENT";
    public static final String BG_NOTIFICATION_BIG_TEXT = "BIG TEXT";
    public static final int BG_NOTIFICATION_ID = 998;
    public static final String BG_NOTIFICATION_CHANNEL_ID = "998";
    public static final String BG_NOTIFICATION_CHANNEL_NAME = "Data Transfer Background Service";
    public static final String BG_NOTIFICATION_CHANNEL_DESCRIPTION = "Data Transfer Background Service";

    public static final String READING = "Reading";
    public static final String RUNNING = "Running";
    public static final String TODO = "Todo";
    public static final String SCRATCH = "Scratch";
    public static final String FAVORITE = "Favorite";

    public static final String ABOUT_TEXT = "by Tunde Adetunji\nmade in the year 2023";
    public static final String ABOUT_URL = "https://www.linkedin.com/in/tundeadetunji";


    public static final String TODO_NO_SUCH_ELEMENT_EXCEPTION = "ToDo: NoSuchElementException";
    public static final String TODO_ERROR_VALIDATION_MSG = "TODO_ERROR_VALIDATION_MSG";
    public static final String SPACE = " ";
    public static final String ASTERISK = "*";
    public static final String BEGIN_HTML_TAG = "<";
    public static final String END_HTML_TAG = ">";
    public static final String HTML_FILENAME = "index.html";
    public static final String JAVA_FILENAME = "main.java";
    public static final String WRITE_FILE_SUCCEEDED = "File has been created.";
    public static final String WRITE_FILE_FAILED = "That resulted in an error. Ensure file was created.";

    public static final String TRANSFER_FRAGMENT_MEMENTO_KEY = "TRANSFER_FRAGMENT_MEMENTO_KEY";
    public static final String HAPTIC_FEEDBACK_ON_ACKNOWLEDGEMENT_KEY = "HAPTIC_FEEDBACK_ON_ACKNOWLEDGEMENT";
    public static final String LAST_WEB_PAGE_SENT_KEY = "LAST_WEB_PAGE_SENT";
    public static final String HAPTIC_FEEDBACK_ONLY_KEY = "HAPTIC_FEEDBACK_ONLY";
    public static final String DELIMITER = "Delimiter";
    public static final String SUCCESS_MESSAGE = "Go, Going, Gone!";

    public static final String MEMO_TOPIC_KEY = "MEMO_TOPIC";
    public static final String NEW_LINE = "\n";

    public static final String TIME_ZONE_INFO_URL = "https://en.wikipedia.org/wiki/Time_zone";
    public static final String FORMAT_FOR_TIME_DATE = "hh:mm a, d MMMM, yyyy";
    public static final String FORMAT_FOR_TIME = "hh:mm a";
    public static final String FORMAT_FOR_DATE_TIME = "d MMMM, yyyy, hh:mm a";
    public static final String FORMAT_FOR_DATE_TIME_FOR_FILE = "d-MMMM-yyyy_hh-mm a";
    public static final String FORMAT_FOR_DATE = "d MMMM";

    public static String[] installedApps;
    public static void getListOfInstalledApps(ApkClient client, PackageManager packageManager, boolean includeSystemApps){
        installedApps = client.packageNameListing(packageManager, includeSystemApps);
    }

    public static final List<String> FRIENDLY_MESSAGES = List.of(
            "Enjoy your day!",
            "Have fun today!",
            "Smile today!",
            "Make today great!",
            "Have a super day!",
            "Shine bright today!",
            "Happy day to you!",
            "Have a good one!",
            "Be awesome today!",
            "Have a nice day!"
    );
    public static final String FRIENDLY_MESSAGE = "Go have fun!";

}
