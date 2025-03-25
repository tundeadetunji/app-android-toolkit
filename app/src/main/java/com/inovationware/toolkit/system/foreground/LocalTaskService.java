package com.inovationware.toolkit.system.foreground;

import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.domain.Strings.bistable;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.inovationware.toolkit.R;
import com.inovationware.toolkit.bistable.service.BistableManager;
import com.inovationware.toolkit.bistable.verb.BistableCommand;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.repository.Repo;
import com.inovationware.toolkit.system.foreground.utility.ForegroundServiceUtility;
import com.inovationware.toolkit.ui.activity.AdvancedSettingsActivity;
import com.inovationware.toolkit.ui.activity.StopServiceActivity;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalTaskService extends Service {
    private static final int NOTIFICATION_ID = 902;
    private static final long INTERVAL = 5002;
    private final String CHANNEL_ID = "Reminder";
    private final String CHANNEL_NAME = "Reminder Channel";
    private BistableManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = new BistableManager();
        createNotificationChannel(); // Create the notification channel
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        // Todo Start the periodic task
        manager.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Todo Stop the periodic task
        manager.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        return ForegroundServiceUtility.createNotification(
                this,
                StopServiceActivity.class,
                "Reminder",
                "Tap to stop reminding about that...",
                CHANNEL_ID);
    }

    private void createNotificationChannel() {
        ForegroundServiceUtility.createNotificationChannel(
                this,
                CHANNEL_ID,
                CHANNEL_NAME);
    }
}


