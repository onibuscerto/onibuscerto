package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.ShapePoint;

public interface ShapePointFactory {

    public ShapePoint createShapePoint();
    
    public void setShapeFirstPoint(ShapePoint shapePoint);

    public ShapePoint getShapeById(String id);
}
