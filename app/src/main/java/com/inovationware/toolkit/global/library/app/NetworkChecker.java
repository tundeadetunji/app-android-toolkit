package com.inovationware.toolkit.global.library.app;

import static com.inovationware.generalmodule.Device.clipboardSetText;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.library.utility.Code.content;
import static com.inovationware.toolkit.global.library.utility.Support.responseStringIsValid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.R;
import com.inovationware.toolkit.datatransfer.dto.response.ResponseEntity;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.repository.Repo;
import com.inovationware.toolkit.ui.activity.AdvancedSettingsActivity;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkChecker extends Service {
    private static final int NOTIFICATION_ID = 900;
    private static final long INTERVAL = 5000;
    private final String CHANNEL_ID = "NETWORK_CHECKER_CHANNEL_ID";
    private final String CHANNEL_NAME = "NETWORK_CHECKER_CHANNEL";
    private Handler handler;
    private Runnable runnable;
    private Retrofit retrofitImpl;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(); // Create the notification channel

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                SharedPreferencesManager store = SharedPreferencesManager.getInstance();
                retrofitImpl = Repo.getInstance().create(context, store);

                Call<String> navigate = retrofitImpl.readText(
                        HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        String.valueOf(Transfer.Intent.readHaptic),
                        Strings.EMPTY_STRING
                );
                navigate.enqueue(new Callback<String>() {
                    @SneakyThrows
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {

                            if (response.body() == null) return;
                            if (response.body().equalsIgnoreCase(Strings.NULL)) return;

                            Factory.getInstance().feedback.service.giveFeedback(
                                    context,
                                    store,
                                    response.body(),
                                    true,
                                    1
                            );

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });

                handler.postDelayed(runnable, INTERVAL);

            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        handler.post(runnable); // Start the periodic task
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop the periodic task
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        Intent stopServiceIntent = new Intent(this, AdvancedSettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create a notification for the foreground service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Data Transfer")
                .setContentText("Network Service is running.")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent) // Set the intent that fires when the user taps the notification
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
