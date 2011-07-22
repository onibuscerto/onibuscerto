package com.onibuscerto.api;

import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Trip;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class StopTimeImpl implements StopTime {

    private final Node underlyingNode;
    private static final String KEY_ARRIVAL_TIME = "stop_time_arrival_time";
    private static final String KEY_DEPARTURE_TIME = "stop_time_departure_time";
    private static final String KEY_SEQUENCE = "stop_time_sequence";

    public StopTimeImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public Trip getTrip() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.TRIP_TO_STOPTIME, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new TripImpl(rel.getStartNode());
        }
    }

    @Override
    public void setTrip(Trip trip) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.TRIP_TO_STOPTIME, Direction.INCOMING);
        TripImpl tripImpl = (TripImpl) trip;

        if (rel != null) {
            rel.delete();
        }

        tripImpl.getUnderlyingNode().createRelationshipTo(
                underlyingNode, Relationships.TRIP_TO_STOPTIME);
    }

    @Override
    public Stop getStop() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.STOP_TO_STOPTIME, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new StopImpl(rel.getStartNode());
        }
    }

    @Override
    public void setStop(Stop stop) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.STOP_TO_STOPTIME, Direction.INCOMING);
        StopImpl stopImpl = (StopImpl) stop;

        if (rel != null) {
            rel.delete();
        }

        stopImpl.getUnderlyingNode().createRelationshipTo(
                underlyingNode, Relationships.STOP_TO_STOPTIME);
    }

    @Override
    public int getArrivalTime() {
        return (Integer) underlyingNode.getProperty(KEY_ARRIVAL_TIME);
    }

    @Override
    public void setArrivalTime(int arrivalTime) {
        underlyingNode.setProperty(KEY_ARRIVAL_TIME, arrivalTime);
    }

    @Override
    public int getDepartureTime() {
        return (Integer) underlyingNode.getProperty(KEY_DEPARTURE_TIME);
    }

    @Override
    public void setDepartureTime(int departureTime) {
        underlyingNode.setProperty(KEY_DEPARTURE_TIME, departureTime);
    }

    @Override
    public int getSequence() {
        return (Integer) underlyingNode.getProperty(KEY_SEQUENCE);
    }

    @Override
    public void setSequence(int sequence) {
        underlyingNode.setProperty(KEY_SEQUENCE, sequence);
    }

    @Override
    public StopTime getNext() {
        if (hasNext()) {
            Relationship rel = underlyingNode.getSingleRelationship(
                    Relationships.NEXT_STOPTIME, Direction.OUTGOING);
            return new StopTimeImpl(rel.getEndNode());
        } else {
            return null;
        }
    }

    @Override
    public void setNext(StopTime stopTime) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.NEXT_STOPTIME, Direction.OUTGOING);

        if (rel != null) {
            rel.delete();
        }

        underlyingNode.createRelationshipTo(
                ((StopTimeImpl) stopTime).getUnderlyingNode(),
                Relationships.NEXT_STOPTIME);
    }

    @Override
    public boolean hasNext() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.NEXT_STOPTIME, Direction.OUTGOING);
        if (rel != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public StopTime getPrevious() {
        if (hasPrevious()) {
            Relationship rel = underlyingNode.getSingleRelationship(
                    Relationships.NEXT_STOPTIME, Direction.INCOMING);
            return new StopTimeImpl(rel.getStartNode());
        } else {
            return null;
        }
    }

    @Override
    public void setPrevious(StopTime stopTime) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.NEXT_STOPTIME, Direction.INCOMING);

        if (rel != null) {
            rel.delete();
        }

        ((StopTimeImpl) stopTime).getUnderlyingNode().createRelationshipTo(
                underlyingNode, Relationships.NEXT_STOPTIME);
    }

    @Override
    public boolean hasPrevious() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.NEXT_STOPTIME, Direction.INCOMING);
        if (rel != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof StopTimeImpl) {
            return getUnderlyingNode().equals(
                    ((StopTimeImpl) object).getUnderlyingNode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
