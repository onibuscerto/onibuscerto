package com.onibuscerto.api;

import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.WalkingConnection;
import org.neo4j.graphdb.Relationship;

class WalkingConnectionImpl implements WalkingConnection {

    private final Relationship underlyingRelationship;

    WalkingConnectionImpl(Relationship relationship) {
        this.underlyingRelationship = relationship;
    }

    public Relationship getUnderlyingRelationship() {
        return underlyingRelationship;
    }

    @Override
    public void setWalkingDistance(double distance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getWalkingDistance() {
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
