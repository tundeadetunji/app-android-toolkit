package com.inovationware.toolkit.tracking.strategy.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.inovationware.toolkit.tracking.strategy.LocationStrategy;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

public class DeviceLocationStrategy implements LocationStrategy {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Context context;
    private Activity activity;
    public DeviceLocationStrategy(Context context, LocationListener locationListener, Activity activity){
        this.context = context;
        this.activity = activity;
        this.locationListener = locationListener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null; // Permissions not granted
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }

    @Override
    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Permissions not granted
        }
        // Request location updates from GPS and Network providers
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 10, locationListener); // 5 seconds, 10 meters
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 10, locationListener);

    }

    @Override
    public void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }
}
