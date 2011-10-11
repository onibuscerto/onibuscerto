package com.onibuscerto.api;

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

    /*public double getDistanceTo(GlobalPosition globalPosition) {
        double dx = Math.pow(this.latitude - globalPosition.getLatitude(), 2);
        double dy = Math.pow(this.longitude - globalPosition.getLongitude(), 2);
        return Math.sqrt(dx + dy);
    }*/

    public double getDistanceTo(GlobalPosition globalPosition) {
        double ans, theta;
        double lat1, lng1, lat2, lng2;

        lat1 = this.getLatitude();
        lng1 = this.getLongitude();

        lat2 = globalPosition.getLatitude();
        lng2 = globalPosition.getLongitude();

        lat1 *= Math.PI / 180.0;
        lng1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lng2 *= Math.PI / 180.0;

        theta = lng1 - lng2;
        ans = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(theta);
        ans = Math.acos(ans) * 180.0 / Math.PI;
        ans = ans * 60 * 1.1515 * 1609.344;

        return ans;
    }
}
