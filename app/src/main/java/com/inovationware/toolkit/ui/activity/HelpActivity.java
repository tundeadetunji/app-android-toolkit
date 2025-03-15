package com.inovationware.toolkit.ui.activity;

import static com.inovationware.generalmodule.Device.clipboardGetText;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.global.domain.Strings.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.global.library.utility.Support.determineTarget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.Manifest;

import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.inovationware.toolkit.databinding.ActivityHelpBinding;
import com.inovationware.toolkit.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.global.domain.Strings;
import com.inovationware.toolkit.global.domain.Transfer;
import com.inovationware.toolkit.global.factory.Factory;
import com.inovationware.toolkit.global.library.app.EncryptionManager;
import com.inovationware.toolkit.global.library.app.GroupManager;
import com.inovationware.toolkit.global.library.app.SharedPreferencesManager;
import com.inovationware.toolkit.global.library.utility.Code;
import com.inovationware.toolkit.global.library.utility.Support;
import com.inovationware.toolkit.tracking.model.LocationData;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    private static final int INTERVAL_MILLIS = 30000;
    private static final int MAX_ADDRESSES_TO_RETURN = 15;
    private static final int FASTEST_INTERVAL_MILLIS = 5000;
    private static final int PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private static final int PERMISSION_LOCATION_REQUEST = 997;
    private static final int PERMISSION_LOCATION_REQUESTS = 996;
    private ActivityHelpBinding binding;
    private Context context;
    private FusedLocationProviderClient service;
    private LocationRequest requests;
    private CurrentLocationRequest request;
    private CancellationToken requestCancellationToken;
    private SharedPreferencesManager store;
    private OnSuccessListener<Location> requestCallback;
    private LocationCallback requestsCallback;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setReferences();
        setConfigurations();
        setCallbacks();
        setUi();

    }
    private void setUi() {
        updateLocationPeriodically();
    }
    private void setCallbacks() {
        requestCallback = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                updateUi(location);
            }
        };

        requestsCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location lastLocation = locationResult.getLastLocation();
                updateUi(lastLocation);
            }
        };


    }
    private void setConfigurations() {
        requests.setInterval(INTERVAL_MILLIS);
        requests.setFastestInterval(FASTEST_INTERVAL_MILLIS);
        requests.setPriority(PRIORITY);

        request = new CurrentLocationRequest.Builder()
                .setPriority(PRIORITY)
                .setDurationMillis(INTERVAL_MILLIS)
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
        context = HelpActivity.this;
        store = SharedPreferencesManager.getInstance();

        requests = new LocationRequest();
        service = LocationServices.getFusedLocationProviderClient(context);
        geocoder = new Geocoder(context);
    }
    private void updateUi(Location location) {
        //Todo what happens if user didn't turn on Location?

        LocationData data = LocationData.create(context, location, store.getSender(context), createLocationToAddressConverter(location, MAX_ADDRESSES_TO_RETURN));
        binding.textDetail.setText(data.toString());

        GroupManager machines = GroupManager.getInstance();
        EncryptionManager security = EncryptionManager.getInstance();
        Factory factory = Factory.getInstance();
        factory.transfer.service.sendText(
                context,
                store,
                machines,
                SendTextRequest.create(HTTP_TRANSFER_URL(context, store),
                        store.getUsername(context),
                        store.getID(context),
                        Transfer.Intent.writeText,
                        store.getSender(context),
                        determineTarget(context, store, machines),
                        Strings.POST_PURPOSE_REGULAR,
                        Support.determineMeta(context, store),
                        security.encrypt(context, store, data.toString()),
                        Strings.EMPTY_STRING
                ),
                DEFAULT_ERROR_MESSAGE_SUFFIX,
                DEFAULT_FAILURE_MESSAGE_SUFFIX);
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            service.getCurrentLocation(request, requestCancellationToken).addOnSuccessListener(requestCallback);

            //service.getLastLocation().addOnSuccessListener(this, callback);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION_REQUEST
                );
            }
        }

    }

    private void updateLocationPeriodically(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            service.requestLocationUpdates(requests, requestsCallback, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION_REQUESTS
                );
            }
        }


    }

    private void stopUpdatingLocationPeriodically(){
        service.removeLocationUpdates(requestsCallback);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                } else {
                    //Toast.makeText(context, "Permission is required for Location to work!", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_LOCATION_REQUESTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationPeriodically();
                } else {
                    //Toast.makeText(context, "Permission is required for Location to work!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private LocationData.AddressFinderClient createLocationToAddressConverter(Location location, int maxAddressesToReturn){
        List<Address> addressesFromProvider = new ArrayList<>();

        try{
            addressesFromProvider = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), maxAddressesToReturn);
        } catch (Exception ignored) {
        }

        List<Address> addresses = addressesFromProvider;
        LocationData.AddressFinderClient service = new LocationData.AddressFinderClient() {

            @Override
            public String covertLocationToAddress() {
                return addresses == null ? " somewhere I can't figure out" : addresses.get(0).getAddressLine(0);
            }

            @Override
            public List<String> getNearbyPlaces() {
                List<String> places = new ArrayList<>();
                for(int i = 1; i < addresses.size(); i++){
                    places.add(addresses.get(i).getAddressLine(0));
                }
                return places;
            }
        };

        return service;
    }
}