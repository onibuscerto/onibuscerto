package com.onibuscerto.api;

import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.entities.WalkingConnection;
import org.neo4j.graphdb.Relationship;

class WalkingConnectionImpl implements WalkingConnection {

    private final Relationship underlyingRelationship;
    private static final String KEY_WALKING_DISTANCE = "wc_walkingdistance";
    private static final String KEY_TIME_COST = "wc_timecost";

    WalkingConnectionImpl(Relationship relationship) {
        this.underlyingRelationship = relationship;
    }

    public Relationship getUnderlyingRelationship() {
        return underlyingRelationship;
    }

    @Override
    public void setWalkingDistance(double distance) {
        this.underlyingRelationship.setProperty(KEY_WALKING_DISTANCE, distance);
        //supondo velocidade de caminhada 1 m/s
        setTimeCost((int) distance);
    }

    @Override
    public double getWalkingDistance() {
        return (Double) this.underlyingRelationship.getProperty(KEY_WALKING_DISTANCE);
    }

    @Override
    public Location getSource() {
        // FIXME: esse método deve retornar StopImpl quando for adequado
        return new LocationImpl(this.underlyingRelationship.getStartNode());
    }

    @Override
    public Location getTarget() {
        // FIXME: esse método deve retornar StopImpl quando for adequado
        return new LocationImpl(this.underlyingRelationship.getEndNode());
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
        if (object instanceof WalkingConnectionImpl) {
            return getUnderlyingRelationship().equals(
                    ((WalkingConnectionImpl) object).getUnderlyingRelationship());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingRelationship().hashCode();
    }
}
