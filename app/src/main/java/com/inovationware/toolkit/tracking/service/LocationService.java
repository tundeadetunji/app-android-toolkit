package com.inovationware.toolkit.tracking.service;

import android.location.Location;

import com.inovationware.toolkit.tracking.model.LocationData;

public interface LocationService {
    void sendCurrentLocation();
    void startLocationUpdates(boolean showFriendlyMessage);
    void startLocationUpdates();
    void stopLocationUpdates();
    LocationData from(Location location);
    boolean isGettingUpdates();
}
