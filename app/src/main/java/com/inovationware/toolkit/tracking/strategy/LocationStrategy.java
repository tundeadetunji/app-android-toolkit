package com.inovationware.toolkit.tracking.strategy;

import android.location.Location;

public interface LocationStrategy {
    Location getCurrentLocation();
    void startLocationUpdates();
    void stopLocationUpdates();
}
