package com.inovationware.toolkit.tracking.model;

import java.util.List;
import java.util.Objects;

public class LocationData {
    private double longitude;
    private double latitude;
    private double altitude;
    private float speed; // in meters/second
    private float bearing; // in degrees
    private float accuracy; // in meters
    private long timestamp; // in milliseconds since epoch
    private String provider; // e.g., "GPS", "Network"
    private String physicalAddress; // Address from geocoding
    private List<String> nearbyPlaces; // List of nearby places
    private String subject = "Subject";

    public LocationData(double longitude, double latitude, double altitude, float speed, float bearing,
                        float accuracy, long timestamp, String provider, String physicalAddress,
                        List<String> nearbyPlaces) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.provider = provider;
        this.physicalAddress = physicalAddress;
        this.nearbyPlaces = nearbyPlaces;
    }
    public LocationData(String subject, double longitude, double latitude, double altitude, float speed, float bearing,
                        float accuracy, long timestamp, String provider, String physicalAddress,
                        List<String> nearbyPlaces) {
        this.subject = subject;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.provider = provider;
        this.physicalAddress = physicalAddress;
        this.nearbyPlaces = nearbyPlaces;
    }


    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude, altitude, speed, bearing, accuracy, timestamp, provider, physicalAddress, nearbyPlaces);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LocationData that = (LocationData) obj;
        return Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.altitude, altitude) == 0 &&
                Float.compare(that.speed, speed) == 0 &&
                Float.compare(that.bearing, bearing) == 0 &&
                Float.compare(that.accuracy, accuracy) == 0 &&
                timestamp == that.timestamp &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(physicalAddress, that.physicalAddress) &&
                Objects.equals(nearbyPlaces, that.nearbyPlaces);
    }
    @Override
    public String toString() {
        String nearbyPlacesString = nearbyPlaces != null && nearbyPlaces.size() > 0
                ? String.join(", ", nearbyPlaces.subList(0, Math.min(3, nearbyPlaces.size())))
                : "No nearby places available";

        double speedInKnots = speed * 1.94384; // Convert m/s to knots

        return String.format("[%s], as at [%s], is at [%s] which is on Long [%.6f], Lat [%.6f], Altitude [%.2f] meters, " +
                        "going at [%.2f] m/s (%.2f km/hr, %.2f knots), accurate to approximately [%.2f] meters, heading [%.2f degrees]. " +
                        "Nearby places include: [%s]. Provider: [%s].",
                subject, timestamp, physicalAddress, longitude, latitude, altitude, speed, speed * 3.6, speedInKnots, accuracy, bearing, nearbyPlacesString, provider);
    }
}
