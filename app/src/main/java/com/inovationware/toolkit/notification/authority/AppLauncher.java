package com.inovationware.toolkit.notification.authority;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.inovationware.toolkit.R;

public class AppLauncher {

    private static final String CHANNEL_ID = "Data_Transfer_AppLauncher_Channel";

    public static void launchAppWithNotification(Context context, String packageName, String appName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        if (intent != null) {
            // Create a PendingIntent to launch the app
            launch(context, intent, appName);
        } else {
            Toast.makeText(context, appName + " is not installed.", Toast.LENGTH_SHORT).show();
            // Handle the case where the app is not installed
            // You might want to prompt the user to install the app
        }
    }

    private static void createNotificationChannel(NotificationManager notificationManager) {
        // Create the NotificationChannel if running on Android 8.0 (API level 26) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Data Transfer App Launcher Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
    }
    private static void launch(Context context, Intent intent, String appName){
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create a notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Set your own icon here
                .setContentTitle("Launch " + appName)
                .setContentText("Tap to open " + appName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Dismiss the notification when tapped

        // Show the notification
        notificationManager.notify(1, builder.build());

    }
}