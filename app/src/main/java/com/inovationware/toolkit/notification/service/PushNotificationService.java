package com.inovationware.toolkit.notification.service;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.inovationware.toolkit.global.library.app.Shared;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;

import static com.inovationware.toolkit.global.domain.Strings.SHARED_PREFERENCES_FIREBASE_TOKEN_KEY;

public class PushNotificationService extends FirebaseMessagingService {
//    private Context context;

/*
    private PushNotificationService(){}
*/

    //@Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //works
        SharedPreferencesManager.getInstance().setString(Shared.getContext(),SHARED_PREFERENCES_FIREBASE_TOKEN_KEY, FirebaseInstanceId.getInstance().getToken());

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //works
        SharedPreferencesManager.getInstance().setString(Shared.getContext(),SHARED_PREFERENCES_FIREBASE_TOKEN_KEY, s);
    }
}
