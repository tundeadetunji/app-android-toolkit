package com.inovationware.toolkit.memo.service.impl;

import android.content.Context;

import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.DeviceClient;
import com.inovationware.toolkit.memo.entity.Memo;
import com.inovationware.toolkit.memo.service.MemoService;
import com.inovationware.toolkit.memo.strategy.impl.KeepIntentStrategy;

import java.io.IOException;

public class KeepIntentService implements MemoService {
    private static KeepIntentService instance;
    public static KeepIntentService getInstance(Context context, SharedPreferencesManager preferencesManager, DeviceClient device){
        if(instance == null) instance = new KeepIntentService(context, preferencesManager, device);
        return instance;
    }

    private KeepIntentService(Context context, SharedPreferencesManager preferencesManager, DeviceClient device){
        this.strategy = KeepIntentStrategy.getInstance(context, preferencesManager, device);
    }

    private KeepIntentStrategy strategy;

    @Override
    public void saveNoteToCloud(Memo memo) throws IOException {
        strategy.saveNoteToCloud(memo);
    }
}
