package com.inovationware.toolkit.tracking.service;

import android.location.Location;

import com.inovationware.toolkit.tracking.model.LocationData;

public interface LocationService {
    void updateLocation();
    void updateLocationPeriodically();
    void stopLocationUpdates();
}
