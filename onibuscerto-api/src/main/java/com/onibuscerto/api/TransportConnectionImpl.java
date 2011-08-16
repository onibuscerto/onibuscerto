package com.onibuscerto.api;

import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.entities.Trip;
import org.neo4j.graphdb.Relationship;

class TransportConnectionImpl implements TransportConnection {

    private final Relationship underlyingRelationship;
    private static final String KEY_TRIP = "tc_trip";
    private static final String KEY_DEPARTURE_TIME = "tc_departure";
    private static final String KEY_TIME_COST = "tc_timecost";

    TransportConnectionImpl(Relationship relationship, Trip trip) {
        this(relationship);
        setTrip(trip);
    }

    TransportConnectionImpl(Relationship relationship) {
        this.underlyingRelationship = relationship;
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
        return (Integer) this.underlyingRelationship.getProperty(KEY_DEPARTURE_TIME);
    }

    @Override
    public void setDepartureTime(int departureTime) {
        this.underlyingRelationship.setProperty(KEY_DEPARTURE_TIME, departureTime);
    }

    @Override
    public Stop getSource() {
        return new StopImpl(this.underlyingRelationship.getStartNode());
    }

    @Override
    public Stop getTarget() {
        return new StopImpl(this.underlyingRelationship.getEndNode());
    }

    @Override
    public int getTimeCost() {
        return (Integer) this.underlyingRelationship.getProperty(KEY_TIME_COST);
    }

    @Override
    public void setTimeCost(int timeCost) {
        this.underlyingRelationship.setProperty(KEY_TIME_COST, timeCost);
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
