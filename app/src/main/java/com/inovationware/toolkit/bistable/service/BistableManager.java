package com.inovationware.toolkit.bistable.service;

import static com.inovationware.toolkit.global.domain.Strings.bistable;
import static com.inovationware.toolkit.global.domain.Strings.netTimerMobileServiceIsRunning;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.inovationware.toolkit.bistable.verb.BistableCommand;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.system.foreground.LocalTaskService;

public class BistableManager {
    public void stop(){
        if (netTimerMobileServiceIsRunning) {
            if (bistable != null) {
                bistable.cancel();
                bistable.repeat = false;
                bistable = null;
            }
            netTimerMobileServiceIsRunning = false;
        }
    }

    public void stopForegroundService(Context context){
        new ContextWrapper(context).stopService(new Intent(context, LocalTaskService.class));
    }

    public void start(){
        if (bistable == null) return;
        bistable.start();
        netTimerMobileServiceIsRunning = true;
    }

}









