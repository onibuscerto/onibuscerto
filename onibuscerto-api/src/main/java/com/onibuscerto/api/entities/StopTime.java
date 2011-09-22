package com.onibuscerto.api.entities;

public interface StopTime {

    public Trip getTrip();

    public void setTrip(Trip trip);

    public Stop getStop();

    public void setStop(Stop stop);

    public int getArrivalTime();

    public void setArrivalTime(int arrivalTime);

    public int getDepartureTime();

    public void setDepartureTime(int departureTime);

    public int getSequence();

    public void setSequence(int sequence);

    public double getShapeDistTraveled();

    public void setShapeDistTraveled(double shapeDistTraveled);

    public boolean hasShapeDistTraveled();

    public ShapePoint getShape();

    public void setShape(ShapePoint shapePoint);

    public int getShapeLength();

    public void setShapeLength(int length);

    public StopTime getNext();

    public void setNext(StopTime stopTime);

    public boolean hasNext();

    public StopTime getPrevious();

    public void setPrevious(StopTime stopTime);

    public boolean hasPrevious();
}
