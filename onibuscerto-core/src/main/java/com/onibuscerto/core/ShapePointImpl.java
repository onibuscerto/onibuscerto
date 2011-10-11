package com.onibuscerto.core;

import com.onibuscerto.core.entities.ShapePoint;
import com.onibuscerto.core.utils.GlobalPosition;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class ShapePointImpl implements ShapePoint {

    private final Node underlyingNode;
    static String KEY_SHAPE_ID = "shape_point_shape_id";
    private static final String KEY_LATITUDE = "shape_point_lat";
    private static final String KEY_LONGITUDE = "shape_point_lon";
    private static final String KEY_SEQUENCE = "shape_point_sequence";
    private static final String KEY_SHAPE_DIST_TRAVELED = "shape_point_dist_traveled";

    public ShapePointImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public String getShapeId() {
        return (String) underlyingNode.getProperty(KEY_SHAPE_ID);
    }

    @Override
    public void setShapeId(String shapeId) {
        underlyingNode.setProperty(KEY_SHAPE_ID, shapeId);
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

    @Override
    public int getSequence() {
        return (Integer) underlyingNode.getProperty(KEY_SEQUENCE);
    }

    @Override
    public void setSequence(int sequence) {
        underlyingNode.setProperty(KEY_SEQUENCE, sequence);
    }

    @Override
    public double getShapeDistTraveled() {
        return (Double) underlyingNode.getProperty(KEY_SHAPE_DIST_TRAVELED);
    }

    @Override
    public void setShapeDistTraveled(double shapeDistTraveled) {
        underlyingNode.setProperty(KEY_SHAPE_DIST_TRAVELED, shapeDistTraveled);
    }

    @Override
    public ShapePoint getNext() {
        if (hasNext()) {
            Relationship rel = underlyingNode.getSingleRelationship(
                    Relationships.NEXT_SHAPE_POINT, Direction.OUTGOING);
            return new ShapePointImpl(rel.getEndNode());
        } else {
            return null;
        }
    }

    @Override
    public void setNext(ShapePoint shapePoint) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.NEXT_SHAPE_POINT, Direction.OUTGOING);

        if (rel != null) {
            rel.delete();
        }
        if (shapePoint != null) {
            underlyingNode.createRelationshipTo(
                    ((ShapePointImpl) shapePoint).getUnderlyingNode(),
                    Relationships.NEXT_SHAPE_POINT);
        }
    }

    @Override
    public boolean hasNext() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.NEXT_SHAPE_POINT, Direction.OUTGOING);
        if (rel != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFirst() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.NEXT_SHAPE_POINT, Direction.INCOMING);
        if (rel == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ShapePointImpl) {
            return getUnderlyingNode().equals(
                    ((ShapePointImpl) object).getUnderlyingNode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
