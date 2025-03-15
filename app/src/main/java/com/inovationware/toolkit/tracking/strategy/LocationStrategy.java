package com.inovationware.toolkit.tracking.strategy;

public interface LocationStrategy {
    void updateLocation();
    void updateLocationPeriodically();
    void stopLocationUpdates();

    /**
     * Sets priority based on if device is low in power or not
     * @return
     */
    int determineBestPriority();
}
