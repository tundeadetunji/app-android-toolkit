package com.inovationware.toolkit.features.location.service;

public interface LocationService {
    void updateLocation();
    void updateLocationPeriodically();
    void stopLocationUpdates();
}
