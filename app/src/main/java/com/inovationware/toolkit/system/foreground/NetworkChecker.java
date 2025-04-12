package com.inovationware.toolkit.system.foreground;

import static com.inovationware.toolkit.global.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.library.utility.Code.content;
import static com.inovationware.toolkit.global.library.utility.Support.visit;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.inovationware.generalmodule.Feedback;
import com.inovationware.toolkit.datatransfer.dto.response.ResponseEntity;
import com.inovationware.toolkit.global.domain.DomainObjects;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.retrofit.Retrofit;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.app.retrofit.Repo;
import com.inovationware.toolkit.location.service.impl.LocationServiceImpl;
import com.inovationware.toolkit.system.foreground.utility.ForegroundServiceUtility;
import com.inovationware.toolkit.ui.activity.StopServiceActivity;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkChecker extends Service {
    private static final int NOTIFICATION_ID = 900;
    private static final long INTERVAL = 5000;
    private final String CHANNEL_ID = "Requests";
    private final String CHANNEL_NAME = "Requests Channel";
    private Handler mainHandler;
    private Runnable hapticRequestRunnable;
    private Handler webPageHandler;
    private Runnable webPageRequestRunnable;
    private Runnable locationRequestRunnable;
    private Handler locationRequestHandler;
    private Retrofit retrofitImpl;
    private String lastSentWebPage;

    @Override
    public void onCreate() {
        super.onCreate();

        setupConfigurations();
        setupReferences();
        setupListeners();


    }

    private void setupListeners() {
        hapticRequestRunnable = new Runnable() {
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
                        DomainObjects.EMPTY_STRING
                );
                navigate.enqueue(new Callback<String>() {
                    @SneakyThrows
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {

                            if (response.body() == null) return;
                            if (response.body().equalsIgnoreCase(DomainObjects.NULL)) return;

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

                mainHandler.postDelayed(hapticRequestRunnable, INTERVAL);

            }
        };
        locationRequestRunnable = new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                SharedPreferencesManager store = SharedPreferencesManager.getInstance();
                retrofitImpl = Repo.getInstance().create(context, store);

                Call<String> navigate = retrofitImpl.readText(
                        HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        String.valueOf(Transfer.Intent.readLocationRequest),
                        DomainObjects.EMPTY_STRING
                );
                navigate.enqueue(new Callback<String>() {
                    @SneakyThrows
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {

                            if (response.body() == null) return;
                            //if (response.body().equalsIgnoreCase(DomainObjects.NULL)) return;

                            if (response.body().equalsIgnoreCase(DomainObjects.IGNORE)) {
                                LocationServiceImpl.getInstance(context).updateLocationPeriodically();
                            } else {
                                LocationServiceImpl.getInstance(context).stopLocationUpdates();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });

                locationRequestHandler.postDelayed(locationRequestRunnable, INTERVAL);

            }
        };

        webPageRequestRunnable = new Runnable() {
            @Override
            public void run() {

                Context context = getApplicationContext();
                SharedPreferencesManager store = SharedPreferencesManager.getInstance();
                Retrofit retrofitImpl = Repo.getInstance().create(context, store);

                Call<String> navigate = retrofitImpl.readText(
                        HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        String.valueOf(Transfer.Intent.readText),
                        DomainObjects.EMPTY_STRING
                );
                navigate.enqueue(new Callback<String>() {
                    @SneakyThrows
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {

                            if (response.body() == null) return;

                            if (response.body().trim().length() < 1) return;

                            ResponseEntity.Envelope received = ResponseEntity.from(response.body());

                            if (received.getInfo() == null) return;

                            if (received.getInfo().trim().isEmpty()) return;

                            if (!received.getTarget().equalsIgnoreCase(store.getUsername(context)) ||
                                    received.getSender().equalsIgnoreCase(store.getSender(context)))
                                return;

                            String decrypted = EncryptionManager.getInstance().decrypt(context, store, received.getInfo());

                            if (!lastSentWebPage.equalsIgnoreCase(decrypted)) {
                                lastSentWebPage = decrypted;
                                SharedPreferencesManager.getInstance().setLastSentWebPage(context, decrypted);
                                try {
                                    visit(context, decrypted, true);
                                } catch (Exception ignored) {

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (SharedPreferencesManager.getInstance().shouldDisplayErrorMessage(context)) {
                            new Feedback(context).toast(DEFAULT_FAILURE_MESSAGE_SUFFIX);
                        }
                    }
                });

                webPageHandler.postDelayed(webPageRequestRunnable, INTERVAL);

            }
        };

    }

    private void setupReferences() {
        mainHandler = new Handler();
        webPageHandler = new Handler();
        locationRequestHandler = new Handler();
        lastSentWebPage = SharedPreferencesManager.getInstance().getLastSentWebPage(getApplicationContext());
    }

    private void setupConfigurations() {
        createNotificationChannel(); // Create the notification channel
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        mainHandler.post(hapticRequestRunnable); // Start the periodic task
        webPageHandler.post(webPageRequestRunnable);
        locationRequestHandler.post(locationRequestRunnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacks(hapticRequestRunnable); // Stop the periodic task
        webPageHandler.removeCallbacks(webPageRequestRunnable);
        locationRequestHandler.removeCallbacks(locationRequestRunnable);
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
                "Requests",
                "Tap to stop handling default requests from home base automatically...",
                CHANNEL_ID
        );
    }

    private void createNotificationChannel() {
        ForegroundServiceUtility.createNotificationChannel(
                this,
                CHANNEL_ID,
                CHANNEL_NAME
        );
    }
}
