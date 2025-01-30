package com.inovationware.toolkit.global.library.app;

import android.content.Context;

import com.inovationware.toolkit.global.library.external.EncoderLite;


public final class EncryptionManager {
    private static EncryptionManager instance;

    public static EncryptionManager getInstance() {
        if (instance == null) instance = new EncryptionManager();
        return instance;
    }

    private final EncoderLite strategy;

    private EncryptionManager() {
        this.strategy = EncoderLite.getInstance();
    }

    public String encrypt(Context context, SharedPreferencesManager preferences, String s) {
        try {
            return strategy.encrypt(s, strategy.fromUTF8String(preferences.getEncryptionPassword(context)), strategy.fromUTF8String(preferences.getEncryptionSalt(context)));
        } catch (Exception exception) {
            return s;
        }
    }

    public String decrypt(Context context, SharedPreferencesManager preferences, String s) {
        try {
            return strategy.decrypt(s, strategy.fromUTF8String(preferences.getEncryptionPassword(context)), strategy.fromUTF8String(preferences.getEncryptionSalt(context)));
        } catch (Exception exception) {
            return s;
        }
    }
}
