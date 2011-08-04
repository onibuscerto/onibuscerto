package com.onibuscerto.api;

import com.onibuscerto.api.entities.ShapePoint;
import org.neo4j.graphdb.Node;

public class ShapePointImpl implements ShapePoint {
    
    private final Node underlyingNode;
    private final String KEY_SHAPE_ID = "shape_point_shape_id";
    private final String KEY_LATITUDE = "shape_point_lat";
    private final String KEY_LONGITUDE = "shape_point_lon";
    private final String KEY_SEQUENCE = "shape_point_sequence";
    private final String KEY_SHAPE_DIST_TRAVELED = "shape_point_dist_traveled";
    
    public ShapePointImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }
    
    @Override
    public int getShapeId() {
        return (int) underlyingNode.getProperty(KEY_SHAPE_ID);
    }
    
    @Override
    public void setShapeId(int shapeId) {
        underlyingNode.setProperty(KEY_SHAPE_ID, shapeId);
    }
    
    @Override
    public double getLatitude() {
        return (double) underlyingNode.getProperty(KEY_LATITUDE);
    }
    
    @Override
    public void setLatitude(double latitude) {
        underlyingNode.setProperty(KEY_LATITUDE, latitude);
    }
    
    @Override
    public double getLongitude() {
        return (double) underlyingNode.getProperty(KEY_LONGITUDE);
    }
    
    @Override
    public void setLongitude(double longitude) {
        underlyingNode.setProperty(KEY_LONGITUDE, longitude);
    }
    
    @Override
    public int getSequence() {
        return (int) underlyingNode.getProperty(KEY_SEQUENCE);
    }
    
    @Override
    public void setSequence(int sequence) {
        underlyingNode.setProperty(KEY_SEQUENCE, sequence);
    }
    
    @Override
    public double getShapeDistTraveled() {
        return (double) underlyingNode.getProperty(KEY_SHAPE_DIST_TRAVELED);
    }
    
    @Override
    public void setShapeDistTraveled(double shapeDistTraveled) {
        underlyingNode.setProperty(KEY_SHAPE_DIST_TRAVELED, shapeDistTraveled);
    }
}
