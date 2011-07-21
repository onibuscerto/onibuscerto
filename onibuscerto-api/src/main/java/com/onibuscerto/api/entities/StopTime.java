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

    public StopTime getNext();

    public StopTime getPrevious();
}
