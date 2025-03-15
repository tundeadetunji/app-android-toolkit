package com.inovationware.toolkit.tracking.model;

import android.location.Location;

public interface CallbackCommand {
    void execute(Location location);
}
