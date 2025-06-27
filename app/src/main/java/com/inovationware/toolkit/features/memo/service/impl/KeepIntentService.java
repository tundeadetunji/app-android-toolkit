package com.inovationware.toolkit.features.memo.service.impl;

import android.content.Context;

import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.DeviceClient;
import com.inovationware.toolkit.features.memo.model.Memo;
import com.inovationware.toolkit.features.memo.service.MemoService;
import com.inovationware.toolkit.features.memo.strategy.impl.KeepIntentStrategy;

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
