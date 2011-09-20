package com.onibuscerto.api;

import com.onibuscerto.api.entities.FareAttribute;
import com.onibuscerto.api.entities.FareRule;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class FareRuleImpl implements FareRule {

    private final Node underlyingNode;
    static final String KEY_ID = "fare_id";
    private static final String KEY_PRICE = "price";

    FareRuleImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
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
}
