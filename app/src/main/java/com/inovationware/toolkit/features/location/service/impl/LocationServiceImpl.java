package com.inovationware.toolkit.features.location.service.impl;

import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_ERROR_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.DEFAULT_FAILURE_MESSAGE_SUFFIX;
import static com.inovationware.toolkit.common.domain.DomainObjects.HTTP_TRANSFER_URL;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_PERIODIC_SOS;
import static com.inovationware.toolkit.common.domain.DomainObjects.POST_PURPOSE_SINGLE_SOS;
import static com.inovationware.toolkit.common.utility.Support.determineTarget;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.inovationware.toolkit.features.datatransfer.dto.request.SendTextRequest;
import com.inovationware.toolkit.common.domain.DomainObjects;
import com.inovationware.toolkit.features.datatransfer.domain.Transfer;
import com.inovationware.toolkit.application.factory.Factory;
import com.inovationware.toolkit.common.utility.EncryptionManager;
import com.inovationware.toolkit.common.utility.GroupManager;
import com.inovationware.toolkit.common.utility.SharedPreferencesManager;
import com.inovationware.toolkit.common.utility.Support;
import com.inovationware.toolkit.features.location.model.CallbackCommand;
import com.inovationware.toolkit.features.location.model.LocationData;
import com.inovationware.toolkit.features.location.service.LocationService;
import com.inovationware.toolkit.features.location.strategy.LocationStrategy;
import com.inovationware.toolkit.features.location.strategy.impl.LocationStrategyImpl;

import java.util.ArrayList;
import java.util.List;

public class LocationServiceImpl implements LocationService {
    private static final int FASTEST_INTERVAL_MILLIS = 15 * 1000;
    private static final int INTERVAL_MILLIS = 30 * 1000;
    private static final int MAX_ADDRESSES_TO_RETURN = 15;

    private LocationStrategy strategy;
    private static LocationServiceImpl instance;

    public static LocationServiceImpl getInstance(Context context) {
        if (instance == null) instance = new LocationServiceImpl(context);
        return instance;
    }

    private LocationServiceImpl(Context context) {
        setStrategy(context, createCallback(context, POST_PURPOSE_SINGLE_SOS), createCallback(context, POST_PURPOSE_PERIODIC_SOS));
    }

    private void setStrategy(Context context, CallbackCommand currentLocationCallback, CallbackCommand periodicUpdatesCallback){
        strategy = LocationStrategyImpl.getInstance(context, currentLocationCallback, periodicUpdatesCallback, INTERVAL_MILLIS, FASTEST_INTERVAL_MILLIS);
    }

    @Override
    public void updateLocation() {
        strategy.updateLocation();
    }

    @Override
    public void updateLocationPeriodically() {
        strategy.updateLocationPeriodically();
    }

    @Override
    public void stopLocationUpdates() {
        strategy.stopLocationUpdates();
    }

    private CallbackCommand createCallback(Context context, String purpose){
        //Todo what happens if user didn't turn on Location?

        CallbackCommand command = new CallbackCommand() {
            @Override
            public void execute(Location location) {
                SharedPreferencesManager store = SharedPreferencesManager.getInstance();
                GroupManager machines = GroupManager.getInstance();
                EncryptionManager security = EncryptionManager.getInstance();
                Factory factory = Factory.getInstance();


                LocationData data = LocationData.create(context, location, store.getSender(context), createLocationToAddressConverter(context, location, MAX_ADDRESSES_TO_RETURN));

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
                                purpose,
                                Support.determineMeta(context, store),
                                security.encrypt(context, store, data.toString()),
                                DomainObjects.EMPTY_STRING
                        ),
                        DEFAULT_ERROR_MESSAGE_SUFFIX,
                        DEFAULT_FAILURE_MESSAGE_SUFFIX);

            }
        };

        return command;
    }

    private LocationData.AddressFinderClient createLocationToAddressConverter(Context context, Location location, int maxAddressesToReturn){
        Geocoder geocoder = new Geocoder(context);

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
