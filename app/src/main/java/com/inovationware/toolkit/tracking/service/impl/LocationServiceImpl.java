package com.inovationware.toolkit.tracking.service.impl;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.inovationware.toolkit.tracking.model.LocationData;
import com.inovationware.toolkit.tracking.service.LocationService;
import com.inovationware.toolkit.tracking.strategy.LocationStrategy;
import com.inovationware.toolkit.tracking.strategy.impl.DeviceLocationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

public class LocationServiceImpl implements LocationService {
    private LocationStrategy strategy;
    private LocationListener callback;
    private Context context;
    private Activity activity;
    private static LocationServiceImpl instance;
    @Getter
    private boolean isGettingUpdates = false;

    private LocationServiceImpl(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        setCallback();
        setStrategy();
    }

    public static LocationServiceImpl getInstance(Context context, Activity activity){
        if(instance == null){
            instance = new LocationServiceImpl(context, activity);
        }
        return instance;
    }

    private void setStrategy(){
        //this.strategy = new GoogleGpsStrategy(context, callback, activity);
        this.strategy = new DeviceLocationStrategy(context, callback, activity);
    }
    private void setCallback(){
        callback = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LocationData data = from(location);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "callback firing...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

    }

    @Override
    public void sendCurrentLocation() {
        LocationData data =from(strategy.getCurrentLocation());
        if (data != null){
            //Todo send the data, probably with Factory.transfer.service
            //Todo suppress any visual indication that location is being dealt with
        }else{
            //Toast.makeText(context, "Didn't get any location info!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void startLocationUpdates(boolean showFriendlyMessage) {
        if (showFriendlyMessage){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Have a nice day!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (!isGettingUpdates) {
            strategy.startLocationUpdates();
            isGettingUpdates = true;
        }
    }

    @Override
    public void startLocationUpdates() {
        startLocationUpdates(false);
    }

    @Override
    public void stopLocationUpdates() {
        isGettingUpdates = false;
        strategy.stopLocationUpdates();
    }
    @Override
    public LocationData from(Location location) {
        if (location == null) return null;

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        double altitude = location.getAltitude();
        float speed = location.getSpeed(); // in m/s
        float bearing = location.getBearing();
        float accuracy = location.getAccuracy();
        long timestamp = location.getTime();
        String provider = location.getProvider();
        List<String> nearbyPlaces = new ArrayList<>();
        // Geocoding to get the physical address
        String physicalAddress = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                physicalAddress = addresses.get(0).getAddressLine(0); // Get the first address line
            }
        } catch (Exception ignored) {
            Toast.makeText(context, "Error createGPSDetails", Toast.LENGTH_SHORT).show();
            //e.printStackTrace(); // Handle exceptions (e.g., IOException)
        }

        return new LocationData(longitude, latitude, altitude, speed, bearing, accuracy, timestamp, provider, physicalAddress, nearbyPlaces);
    }

}
