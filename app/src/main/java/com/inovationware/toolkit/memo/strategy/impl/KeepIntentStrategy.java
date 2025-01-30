package com.inovationware.toolkit.memo.strategy.impl;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;

import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.memo.strategy.MemoStrategy;

import java.io.IOException;

public class KeepIntentStrategy implements MemoStrategy {
    private static KeepIntentStrategy instance;

    public static KeepIntentStrategy getInstance(Context context, SharedPreferencesManager preferencesManager, DeviceClient device) {
        if (instance == null) instance = new KeepIntentStrategy(context, preferencesManager, device);
        return instance;
    }

    private KeepIntentStrategy(Context context, SharedPreferencesManager store, DeviceClient device) {
        this.context = context;
        this.store = store;
        this.device = device;
    }

    private Context context;
    private SharedPreferencesManager store;
    private DeviceClient device;
    private final String DEFAULT_MIME_TYPE = "text/plain";
    private final String CLOUD_APP_PACKAGE = "com.google.android.keep";

    @Override
    public void saveNoteToCloud(Memo memo) throws IOException {
        if (!store.shouldPromptToSyncNote(context)) return;

        try {
            Intent keepIntent = new Intent(Intent.ACTION_SEND);
            keepIntent.setType(DEFAULT_MIME_TYPE);
            keepIntent.setPackage(CLOUD_APP_PACKAGE);

            keepIntent.putExtra(Intent.EXTRA_SUBJECT, memo.getNoteTitle());
            keepIntent.putExtra(Intent.EXTRA_TEXT, Code.formatOutput(context, memo.getPostnote(), store, device));

            startActivity(context, keepIntent, null);
        } catch (Exception ignored) {
            //ignored.printStackTrace();
        }

    }

}
