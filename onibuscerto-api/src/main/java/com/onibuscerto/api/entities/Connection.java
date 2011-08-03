package com.onibuscerto.api.entities;

public interface Connection {

    public Stop getSource();

    public Stop getTarget();

    public int getDepartureTime();

    public int getTimeCost();
}
