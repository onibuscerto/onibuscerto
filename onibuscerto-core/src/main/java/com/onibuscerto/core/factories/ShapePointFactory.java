package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.ShapePoint;

public interface ShapePointFactory {

    public ShapePoint createShapePoint();
    
    public void setShapeFirstPoint(ShapePoint shapePoint);

    public ShapePoint getShapeById(String id);
}
