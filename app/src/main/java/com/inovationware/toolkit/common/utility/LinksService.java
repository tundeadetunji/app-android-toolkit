package com.inovationware.toolkit.common.utility;

import static com.inovationware.toolkit.common.domain.DomainObjects.EMPTY_STRING;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_FAVORITE_URL_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_PINNED_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_READING_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_RUNNING_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_SCRATCH_KEY;
import static com.inovationware.toolkit.common.domain.DomainObjects.SHARED_PREFERENCES_TODO_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LinksService {
    private static LinksService instance;
    private Activity activity;
    private Context context;
    private SharedPreferencesManager store;

    public static LinksService getInstance(Activity activity,  Context context, SharedPreferencesManager store) {
        if (instance == null) instance = new LinksService(activity, context, store);
        return instance;
    }

    private LinksService(Activity activity,  Context context, SharedPreferencesManager store) {
        this.activity = activity;
        this.context = context;
        this.store = store;
    }

    public void setupListeners(Button pinnedButton, Button readingLinkButton, Button todoLinkButton, Button runningLinkButton, Button scratchLinkButton, Button extraLinkButton) {

        pinnedButton.setOnClickListener(pinnedButtonClickHandler);
        readingLinkButton.setOnClickListener(readingLinkButtonClickHandler);
        todoLinkButton.setOnClickListener(todoLinkButtonClickHandler);
        runningLinkButton.setOnClickListener(runningLinkButtonClickHandler);
        scratchLinkButton.setOnClickListener(scratchLinkButtonClickHandler);
        extraLinkButton.setOnClickListener(extraLinkButtonClickHandler);
    }

    private final View.OnClickListener pinnedButtonClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(context, SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(context, SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING))) {
                    activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(context, SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING))));
                } else {
                    if (context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING)) != null) {
                        activity.startActivity(context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_PINNED_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(context)) {
                    return;
                }
                Toast.makeText(activity, "resulted in an error, url may not be valid.", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private final View.OnClickListener readingLinkButtonClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(context, SHARED_PREFERENCES_READING_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(context, SHARED_PREFERENCES_READING_KEY, EMPTY_STRING))) {
                    activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(context, SHARED_PREFERENCES_READING_KEY, EMPTY_STRING))));
                } else {
                    if (context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_READING_KEY, EMPTY_STRING)) != null) {
                        activity.startActivity(context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_READING_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(context)) {
                    return;
                }
                Toast.makeText(activity, "resulted in an error, url may not be valid.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener todoLinkButtonClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(context, SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(context, SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING))) {
                    activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(context, SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING))));
                } else {
                    if (context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING)) != null) {
                        activity.startActivity(context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_TODO_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(context)) {
                    return;
                }
                Toast.makeText(activity, "resulted in an error, url may not be valid.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener runningLinkButtonClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(context, SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(context, SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING))) {
                    activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(context, SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING))));
                } else {
                    if (context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING)) != null) {
                        activity.startActivity(context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_RUNNING_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(context)) {
                    return;
                }
                Toast.makeText(activity, "resulted in an error, url may not be valid.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener scratchLinkButtonClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(context, SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(context, SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING))) {
                    activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(context, SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING))));
                } else {
                    if (context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING)) != null) {
                        activity.startActivity(context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_SCRATCH_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(context)) {
                    return;
                }
                Toast.makeText(activity, "resulted in an error, url may not be valid.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener extraLinkButtonClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (store.getString(context, SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING).length() < 1) {
                //feedback.toast("set URL to open\n(in Settings > Advanced)");
                return;
            }
            try {
                if (isInternetResource(store.getString(context, SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING))) {
                    activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(store.getString(context, SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING))));
                } else {
                    if (context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING)) != null) {
                        activity.startActivity(context.getPackageManager().getLaunchIntentForPackage(store.getString(context, SHARED_PREFERENCES_FAVORITE_URL_KEY, EMPTY_STRING)));
                    }
                }
            } catch (Exception e) {
                if (!store.shouldDisplayErrorMessage(context)) {
                    return;
                }
                Toast.makeText(activity, "resulted in an error, url may not be valid.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    boolean isInternetResource(String url) {
        return url.toLowerCase().startsWith("http");
    }

}
