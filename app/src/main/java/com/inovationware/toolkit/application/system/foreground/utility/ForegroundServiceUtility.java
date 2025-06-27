package com.inovationware.toolkit.application.system.foreground.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.inovationware.toolkit.R;

public class ForegroundServiceUtility {
    public static Notification createNotification(Context context, Class<?> activityClass, String notificationTitle, String notificationText, String channelId) {
        Intent stopServiceIntent = new Intent(context, activityClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create a notification for the foreground service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent) // Set the intent that fires when the user taps the notification
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }
    public static void createNotificationChannel(Context context, String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }


}
