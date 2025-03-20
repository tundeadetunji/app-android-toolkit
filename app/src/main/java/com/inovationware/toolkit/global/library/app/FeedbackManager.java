package com.inovationware.toolkit.global.library.app;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

public class FeedbackManager {
    private final int SCHEME_SHORT = 500;
    private final int SCHEME_MEDIUM = 100;
    private final long[] SCHEME_LONG = {0, 200, 100, 300}; // Start immediately, vibrate for 200ms, pause for 100ms, vibrate for 300ms
    private Vibrator vibrator;

    public void giveFeedback(Context context, SharedPreferencesManager store, String feedback, boolean isAcknowledgement, int toastLength) {
        if (store.hapticFeedbackOnly(context)){
            giveHapticFeedback(context);
            return;
        }
        if (isAcknowledgement && store.hapticFeedbackOnAcknowledgement(context)){
            giveHapticFeedback(context);
        }else {
            giveToastFeedback(context, feedback, toastLength);
        }
    }

    private void giveToastFeedback(Context context, String feedback, int length){
        Toast.makeText(context, feedback, length).show();
    }

    private void giveHapticFeedback(Context context){
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) vibrator.vibrate(SCHEME_SHORT);
    }

}
