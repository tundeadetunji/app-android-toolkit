package com.inovationware.toolkit.features.location.strategy.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.inovationware.toolkit.features.location.model.CallbackCommand;
import com.inovationware.toolkit.features.location.strategy.LocationStrategy;

public class LocationStrategyImpl implements LocationStrategy {

    private int fastestIntervalInMilliseconds;
    private int intervalInMilliseconds;
    private CurrentLocationRequest request;
    private CancellationToken requestCancellationToken;
    private FusedLocationProviderClient service;
    private LocationRequest requests;
    private static LocationStrategyImpl instance;
    private CallbackCommand currentLocationCallback;
    private CallbackCommand periodicUpdatesCallback;
    private OnSuccessListener<Location> requestCallback;
    private LocationCallback requestsCallback;
    private Context context;


    public static LocationStrategyImpl getInstance(Context context, CallbackCommand currentLocationCallback, CallbackCommand periodicUpdatesCallback, int intervalInMilliseconds, int fastestIntervalInMilliseconds) {
        if (instance == null) instance = new LocationStrategyImpl(context,currentLocationCallback, periodicUpdatesCallback, intervalInMilliseconds,  fastestIntervalInMilliseconds);
        return instance;
    }

    private LocationStrategyImpl(Context context, CallbackCommand currentLocationCallback, CallbackCommand periodicUpdatesCallback, int intervalInMilliseconds, int fastestIntervalInMilliseconds) {
        this.context = context;
        this.currentLocationCallback = currentLocationCallback;
        this.periodicUpdatesCallback = periodicUpdatesCallback;
        this.intervalInMilliseconds = intervalInMilliseconds;
        this.fastestIntervalInMilliseconds = fastestIntervalInMilliseconds;


        setReferences();
        setConfigurations();
        setCallbacks();

    }
    private void setConfigurations() {
        requests.setInterval(intervalInMilliseconds);
        requests.setFastestInterval(fastestIntervalInMilliseconds);
        requests.setPriority(determineBestPriority());

        request = new CurrentLocationRequest.Builder()
                .setPriority(determineBestPriority())
                .setDurationMillis(intervalInMilliseconds)
                .build();

        requestCancellationToken = new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        };

    }
    private void setReferences() {

        requests = new LocationRequest();
        service = LocationServices.getFusedLocationProviderClient(context);
    }
    private void setCallbacks() {
        requestCallback = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocationCallback.execute(location);
            }
        };

        requestsCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                periodicUpdatesCallback.execute(locationResult.getLastLocation());
            }
        };


    }

    @Override
    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            service.getCurrentLocation(request, requestCancellationToken).addOnSuccessListener(requestCallback);

            //service.getLastLocation().addOnSuccessListener(this, callback);
        }
    }

    @Override
    public void updateLocationPeriodically() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            service.requestLocationUpdates(requests, requestsCallback, null);
        }
    }

    @Override
    public void stopLocationUpdates() {
        service.removeLocationUpdates(requestsCallback);
    }

    @Override
    public int determineBestPriority() {
        return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    }
}
