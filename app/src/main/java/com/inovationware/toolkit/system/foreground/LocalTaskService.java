package com.inovationware.toolkit.system.foreground;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.inovationware.toolkit.bistable.service.BistableManager;
import com.inovationware.toolkit.system.foreground.utility.ForegroundServiceUtility;
import com.inovationware.toolkit.ui.activity.StopServiceActivity;

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


