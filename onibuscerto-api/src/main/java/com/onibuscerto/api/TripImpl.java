package com.onibuscerto.api;

import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Trip;
import java.util.Collection;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

class TripImpl implements Trip {

    private final Node underlyingNode;
    private static final String KEY_ID = "trip_id";

    public TripImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    @Override
    public Route getRoute() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.ROUTE_TO_TRIP, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new RouteImpl(rel.getStartNode());
        }
    }

    @Override
    public void setRoute(Route route) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.ROUTE_TO_TRIP, Direction.INCOMING);
        RouteImpl routeImpl = (RouteImpl) route;

        if (rel != null) {
            rel.delete();
        }

        routeImpl.getUnderlyingNode().createRelationshipTo(underlyingNode,
                Relationships.ROUTE_TO_TRIP);
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
