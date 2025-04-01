package com.inovationware.toolkit.bistable.service;

//import static com.inovationware.toolkit.global.domain.Strings.bistable;
//import static com.inovationware.toolkit.global.domain.DomainObjects.netTimerMobileServiceIsRunning;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.inovationware.toolkit.bistable.verb.BistableCommand;
import com.inovationware.toolkit.system.foreground.LocalTaskService;

import lombok.Getter;
import lombok.Setter;

public class BistableManager {
    private static BistableManager instance;
    @Getter @Setter
    private BistableCommand bistable;
    @Getter @Setter
    private boolean netTimerMobileServiceIsRunning = false;


    private BistableManager(BistableCommand bistable) {
        this.bistable = bistable;
        //DomainObjects.bistable = null;
    }

    public static BistableManager getInstance(BistableCommand bistable){
        if (instance == null){
            instance = new BistableManager(bistable);
        }
        return instance;
    }

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









