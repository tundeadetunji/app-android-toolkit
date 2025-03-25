package com.inovationware.toolkit.location.service;

public interface LocationService {
    void updateLocation();
    void updateLocationPeriodically();
    void stopLocationUpdates();
}
