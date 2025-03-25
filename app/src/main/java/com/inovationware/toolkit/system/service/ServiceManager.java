package com.inovationware.toolkit.system.service;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.inovationware.toolkit.system.foreground.NetworkChecker;

public class ServiceManager {
    public boolean networkServiceShouldRun;
    public boolean networkServiceIsRunning;

    private static ServiceManager instance;

    public static ServiceManager getInstance() {
        if (instance == null) instance = new ServiceManager();
        return instance;
    }

    private ServiceManager() {
        networkServiceShouldRun = true;
        networkServiceIsRunning = false;
    }

    public void startServices(Context context){
        if (networkServiceShouldRun){
            if (!networkServiceIsRunning){
                Intent serviceIntent = new Intent(context, NetworkChecker.class);
                ContextCompat.startForegroundService(context, serviceIntent);
                networkServiceIsRunning = true;
            }
        }
    }

    public void stopServices(Context context){
        networkServiceShouldRun = false;
        new ContextWrapper(context).stopService(new Intent(context, NetworkChecker.class));
        networkServiceIsRunning = false;

    }

}
