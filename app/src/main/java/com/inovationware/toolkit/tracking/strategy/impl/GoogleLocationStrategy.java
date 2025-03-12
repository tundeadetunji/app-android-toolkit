package com.inovationware.toolkit.tracking.strategy.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.inovationware.toolkit.tracking.strategy.LocationStrategy;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoogleLocationStrategy implements LocationStrategy {

    private final int LOCATION_PERMISSION_REQUEST_CODE = 120;
    private Context context;
    private Activity activity;
    private LocationListener callback;
    private LocationManager service;

    public GoogleLocationStrategy(Context context, LocationListener callback, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.callback = callback;
        setupVariables();
    }

    private void setupVariables() {
        service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public Location getCurrentLocation() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "It's started!", Toast.LENGTH_SHORT).show();
            }
        });

        CancellationSignal cancellationSignal = getCancellationSignal();

        Executor executor = Executors.newSingleThreadExecutor();

        android.location.LocationRequest request = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            request = new android.location.LocationRequest.Builder(15000).build();
        }

        if (request == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Not supported!", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            service.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    request,
                    cancellationSignal,
                    executor,
                    (location) -> {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (location == null) {
                                    Toast.makeText(context, "Location is null!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, (int) location.getLatitude(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
            );
        }
        return null;
    }

    @NonNull
    private CancellationSignal getCancellationSignal() {
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Location request cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return cancellationSignal;
    }

    @Override
    public void startLocationUpdates() {

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);

        service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        //service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 3, callback);
        //service.requestLocationUpdates(10000, 3, criteria, callback, Looper.getMainLooper());
        service.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, callback);

    }

    @Override
    public void stopLocationUpdates() {
        service.removeUpdates(callback);
    }
}
