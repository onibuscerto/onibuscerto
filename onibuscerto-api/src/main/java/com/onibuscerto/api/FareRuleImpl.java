package com.onibuscerto.api;

import com.onibuscerto.api.entities.FareAttribute;
import com.onibuscerto.api.entities.FareRule;
import com.onibuscerto.api.entities.Stop;
import java.util.Collection;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class FareRuleImpl implements FareRule {

    private final Node underlyingNode;
    static final String KEY_FARE_ATTRIBUTE_ID = "fare_attribute_id";
    static final String KEY_ID = "fare_rule_id";

    FareRuleImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
        this.setId("");
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
    public FareAttribute getFareAttribute() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.FARE_RULE_TO_FARE_ATTRIBUTE, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new FareAttributeImpl(rel.getStartNode());
        }
    }

    @Override
    public void setFareAttribute(FareAttribute fare) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.FARE_RULE_TO_FARE_ATTRIBUTE, Direction.INCOMING);
        FareAttributeImpl fareAttributeImpl = (FareAttributeImpl) fare;

        if (rel != null) {
            rel.delete();
        }

        fareAttributeImpl.getUnderlyingNode().createRelationshipTo(underlyingNode,
                Relationships.FARE_RULE_TO_FARE_ATTRIBUTE);
    }

    @Override
    public Stop getSource() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.FARE_RULE_TO_SOURCE, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new StopImpl(rel.getStartNode());
        }
    }

    @Override
    public void setSource(Stop source) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.FARE_RULE_TO_SOURCE, Direction.INCOMING);
        StopImpl stopImpl = (StopImpl) source;

        if (rel != null) {
            rel.delete();
        }

        stopImpl.getUnderlyingNode().createRelationshipTo(underlyingNode,
                Relationships.FARE_RULE_TO_SOURCE);
    }

    @Override
    public Stop getTarget() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.FARE_RULE_TO_TARGET, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new StopImpl(rel.getStartNode());
        }
    }

    @Override
    public void setTarget(Stop target) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.FARE_RULE_TO_TARGET, Direction.INCOMING);
        StopImpl stopImpl = (StopImpl) target;

        if (rel != null) {
            rel.delete();
        }

        stopImpl.getUnderlyingNode().createRelationshipTo(underlyingNode,
                Relationships.FARE_RULE_TO_TARGET);
    }

    @Override
    public Collection<Stop> getStopsFromFare() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStopsFromFare(Collection<Stop> stops) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FareRuleImpl) {
            return getUnderlyingNode().equals(
                    ((FareRuleImpl) object).getUnderlyingNode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
