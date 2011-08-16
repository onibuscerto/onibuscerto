package com.onibuscerto.api;

import com.onibuscerto.api.entities.Location;
import org.neo4j.graphdb.Node;

class LocationImpl implements Location {

    private final Node underlyingNode;
    static final String KEY_LATITUDE = "location_lat";
    static final String KEY_LONGITUDE = "location_lon";

    LocationImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    @Override
    public double getLatitude() {
        return (Double) underlyingNode.getProperty(KEY_LATITUDE);
    }

    @Override
    public void setLatitude(double latitude) {
        underlyingNode.setProperty(KEY_LATITUDE, latitude);
    }

    @Override
    public double getLongitude() {
        return (Double) underlyingNode.getProperty(KEY_LONGITUDE);
    }

    @Override
    public void setLongitude(double longitude) {
        underlyingNode.setProperty(KEY_LONGITUDE, longitude);
    }
}
