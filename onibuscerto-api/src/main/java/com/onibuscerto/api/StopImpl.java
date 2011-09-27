package com.onibuscerto.api;

import com.onibuscerto.api.entities.FareAttribute;
import com.onibuscerto.api.entities.Stop;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

class StopImpl extends LocationImpl implements Stop {

    static final String KEY_ID = "stop_id";
    private static final String KEY_NAME = "stop_name";
    private static final String KEY_ZONE_ID = "zone_id";

    StopImpl(Node underlyingNode, String id) {
        this(underlyingNode);
        setId(id);
    }

    StopImpl(Node underlyingNode) {
        super(underlyingNode);
    }

    @Override
    public String getId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    private void setId(String id) {
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
    public String getZoneId() {
        return (String) underlyingNode.getProperty(KEY_ZONE_ID);
    }

    @Override
    public void setZoneId(String zoneId) {
        underlyingNode.setProperty(KEY_ZONE_ID, zoneId);
    }

    @Override
    public FareAttribute getFare() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.STOP_TO_FARE, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new FareAttributeImpl(rel.getStartNode());
        }
    }

    @Override
    public void setFare(FareAttribute fare) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.STOP_TO_FARE, Direction.INCOMING);
        FareAttributeImpl fareImpl = (FareAttributeImpl) fare;

        if (rel != null) {
            rel.delete();
        }

        fareImpl.getUnderlyingNode().createRelationshipTo(underlyingNode,
                Relationships.STOP_TO_FARE);
    }
}
