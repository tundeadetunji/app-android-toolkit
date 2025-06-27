package com.inovationware.toolkit.features.location.model;

import android.content.Context;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class LocationData {
    private double longitude;
    private double latitude;
    private double altitude;
    private boolean hasAltitude;
    private float speed; // in meters/second
    private boolean hasSpeed;
    private float bearing; // in degrees
    private boolean hasBearing;
    private float accuracy; // in meters
    private boolean hasAccuracy;
    private long timestamp; // in milliseconds since epoch
    private String provider; // e.g., "GPS", "Network"
    private String physicalAddress; // Address from geocoding
    //Todo remove this field
    private List<String> nearbyPlaces = new ArrayList<>(); // List of nearby places
    private String subject;

    private LocationData(Context context, Location location, String subject, AddressFinderClient addressService) {
        this.subject = subject;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.hasAccuracy = location.hasAccuracy();
        this.accuracy = location.getAccuracy();
        this.hasAltitude = location.hasAltitude();
        this.altitude = location.hasAltitude() ? location.getAltitude() : 0;
        this.hasSpeed = location.hasSpeed();
        this.speed = location.getSpeed();
        this.hasBearing = location.hasBearing();
        this.bearing = location.getBearing();
        this.timestamp = location.getTime();
        this.provider = location.getProvider();
        this.physicalAddress = addressService.covertLocationToAddress();
        this.nearbyPlaces = addressService.getNearbyPlaces();
    }

    public static LocationData create(Context context, Location location, String subject, AddressFinderClient addressService){
        return new LocationData(context, location, subject, addressService);
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
                ? "Nearby places includes..." + String.join(";\n ", nearbyPlaces.subList(0, Math.min(3, nearbyPlaces.size())))
                : "Could not locate nearby places.";

        double speedInKnots = speed * 1.94384; // Convert m/s to knots

        String altitudeInfo = hasAltitude ? String.format("Altitude is %.2f meters.", altitude) : "Altitude information not available.";
        String speedInfo = hasSpeed ? String.format("Speed is %.2f m/s (%.2f km/hr, %.2f knots).", speed, speed * 3.6, speedInKnots) : "Speed information not available.";
        String bearingInfo = hasBearing ? String.format("Bearing is %.2f degrees.", bearing) : "Bearing information not available.";
        String accuracyInfo = hasAccuracy ? String.format("Accurate to approximately %.2f meters.", accuracy) : "Accuracy information not available.";
        String timestampInfo = convertEpochToLocalDateTime(timestamp);

        StringBuilder builder = new StringBuilder();
        builder.append(subject)
                .append(", as at ").append(timestampInfo)
                .append(",\nis at ").append(physicalAddress)
                .append("\nwhich is on Long ").append(String.format("%.6f", longitude))
                .append(", Lat ").append(String.format("%.6f", latitude)).append(".")
                .append("\n" + altitudeInfo)
                .append("\n" + speedInfo)
                .append("\n" + accuracyInfo)
                .append("\n" + bearingInfo)
                .append("\n" + nearbyPlacesString)
                .append("\nInformation is provided by " + provider + ".");

        return builder.toString();

        /*return String.format("[%s], as at [%s], is at [%s] which is on Long [%.6f], Lat [%.6f], %s, " +
                        "%s %s %s. " +
                        "Nearby places include: [%s]. Provider: [%s].",
                subject, timestamp, physicalAddress, longitude, latitude, altitudeInfo, speedInfo, accuracyInfo, bearingInfo, nearbyPlacesString, provider);*/
    }

    public interface AddressFinderClient {
        String covertLocationToAddress();
        List<String> getNearbyPlaces();
    }
    public static String convertEpochToLocalDateTime(long epochMilli) {
        Date date = new Date(epochMilli);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, hh:mm a");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }}
