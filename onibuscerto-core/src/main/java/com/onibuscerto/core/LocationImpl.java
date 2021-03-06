package com.onibuscerto.core;

import com.onibuscerto.core.entities.Connection;
import com.onibuscerto.core.entities.Location;
import com.onibuscerto.api.GlobalPosition;
import java.util.Collection;
import java.util.LinkedList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

class LocationImpl implements Location {

    protected final Node underlyingNode;
    static final String KEY_LATITUDE = "location_lat";
    static final String KEY_LONGITUDE = "location_lon";

    LocationImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
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
    public GlobalPosition getGlobalPosition() {
        return new GlobalPosition(this.getLatitude(), this.getLongitude());
    }

    @Override
    public void setGlobalPosition(GlobalPosition globalPosition) {
        this.setLatitude(globalPosition.getLatitude());
        this.setLongitude(globalPosition.getLongitude());
    }

    private Collection<Connection> buildConnectionsCollection(Iterable<Relationship> relationships) {
        Collection<Connection> connections = new LinkedList<Connection>();

        for (Relationship relationship : relationships) {
            if (relationship.getType().equals(Relationships.TRANSPORT_CONNECTION)) {
                connections.add(new TransportConnectionImpl(relationship));
            } else if (relationship.getType().equals(Relationships.WALKING_CONNECTION)) {
                connections.add(new WalkingConnectionImpl(relationship));
            }
        }

        return connections;
    }

    @Override
    public Collection<Connection> getOutgoingConnections() {
        Iterable<Relationship> outgoingRelationships = underlyingNode.getRelationships(Direction.OUTGOING,
                Relationships.TRANSPORT_CONNECTION,
                Relationships.WALKING_CONNECTION);
        return buildConnectionsCollection(outgoingRelationships);
    }

    @Override
    public Collection<Connection> getIncomingConnections() {
        Iterable<Relationship> incomingRelationships = underlyingNode.getRelationships(Direction.INCOMING,
                Relationships.TRANSPORT_CONNECTION,
                Relationships.WALKING_CONNECTION);
        Collection<Connection> incomingConnections = new LinkedList<Connection>();
        return buildConnectionsCollection(incomingRelationships);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof LocationImpl) {
            return getUnderlyingNode().equals(
                    ((LocationImpl) object).getUnderlyingNode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
