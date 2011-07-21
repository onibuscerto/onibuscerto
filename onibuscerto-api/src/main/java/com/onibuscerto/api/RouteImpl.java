package com.onibuscerto.api;

import java.util.Collection;
import org.neo4j.graphdb.Node;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.Trip;

class RouteImpl implements Route {

    private final Node underlyingNode;
    private static final String KEY_ID = "route_id";
    private static final String KEY_SHORT_NAME = "route_short_name";
    private static final String KEY_LONG_NAME = "route_long_name";
    private static final String KEY_TYPE = "route_type";

    public RouteImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public String getId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    @Override
    public void setId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    @Override
    public String getShortName() {
        return (String) underlyingNode.getProperty(KEY_SHORT_NAME);
    }

    @Override
    public void setShortname(String shortName) {
        underlyingNode.setProperty(KEY_SHORT_NAME, shortName);
    }

    @Override
    public String getLongName() {
        return (String) underlyingNode.getProperty(KEY_LONG_NAME);
    }

    @Override
    public void setLongName(String longName) {
        underlyingNode.setProperty(KEY_LONG_NAME, longName);
    }

    @Override
    public Type getType() {
        return (Type) underlyingNode.getProperty(KEY_TYPE);
    }

    @Override
    public void setType(Type type) {
        underlyingNode.setProperty(KEY_TYPE, type);
    }

    @Override
    public Collection<Trip> getTrips() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
