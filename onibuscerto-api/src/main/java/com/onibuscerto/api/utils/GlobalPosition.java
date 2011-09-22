package com.onibuscerto.api.utils;

/**
 * Um par latitude, longitude representando uma posição global.
 */
public final class GlobalPosition {

    private double latitude;
    private double longitude;

    public GlobalPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistanceTo(GlobalPosition globalPosition) {
        double dx = Math.pow(this.latitude - globalPosition.getLatitude(), 2);
        double dy = Math.pow(this.longitude - globalPosition.getLongitude(), 2);
        return Math.sqrt(dx + dy);
    }
}
