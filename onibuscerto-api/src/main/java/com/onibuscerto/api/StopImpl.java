package com.onibuscerto.api;

import com.onibuscerto.api.entities.Stop;
import org.neo4j.graphdb.Node;

class StopImpl implements Stop {

    private final Node underlyingNode;
    static final String KEY_ID = "stop_id";
    private static final String KEY_NAME = "stop_name";
    private static final String KEY_LATITUDE = "stop_lat";
    private static final String KEY_LONGITUDE = "stop_lon";

    StopImpl(Node underlyingNode, String id) {
        this(underlyingNode);
        setId(id);
    }

    StopImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public String getId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    private final void setId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    @Override
    public String getName() {
        return (String) underlyingNode.getProperty(KEY_NAME);
    }

    @Override
    public void setName(String name) {
        underlyingNode.setProperty(KEY_NAME, name);
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

    @Override
    public boolean equals(Object object) {
        if (object instanceof StopImpl) {
            return getUnderlyingNode().equals(
                    ((StopImpl) object).getUnderlyingNode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
