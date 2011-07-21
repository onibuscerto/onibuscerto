package com.onibuscerto.api;

import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Trip;
import java.util.Collection;
import org.neo4j.graphdb.Node;

class TripImpl implements Trip {
    
    private final Node underlyingNode;
    private static final String KEY_ID = "trip_id";

    public TripImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    @Override
    public Route getRoute() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public Collection<StopTime> getStopTimes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
