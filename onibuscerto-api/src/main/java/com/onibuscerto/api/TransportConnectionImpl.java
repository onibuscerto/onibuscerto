package com.onibuscerto.api;

import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.entities.Trip;
import org.neo4j.graphdb.Relationship;

class TransportConnectionImpl implements TransportConnection {

    private final Relationship underlyingRelationship;
    private static final String KEY_TRIP = "tc_trip";

    TransportConnectionImpl(Relationship relationship, Trip trip) {
        this.underlyingRelationship = relationship;
        setTrip(trip);
    }

    public Relationship getUnderlyingRelationship() {
        return underlyingRelationship;
    }

    @Override
    public String getTripId() {
        return (String) this.underlyingRelationship.getProperty(KEY_TRIP);
    }

    private void setTrip(Trip trip) {
        this.underlyingRelationship.setProperty(KEY_TRIP, trip.getId());
    }

    @Override
    public int getDepartureTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDepartureTime(int departureTime) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Stop getSource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Stop getTarget() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTimeCost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTimeCost(int timeCost) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof TransportConnectionImpl) {
            return getUnderlyingRelationship().equals(
                    ((TransportConnectionImpl) object).getUnderlyingRelationship());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingRelationship().hashCode();
    }
}
