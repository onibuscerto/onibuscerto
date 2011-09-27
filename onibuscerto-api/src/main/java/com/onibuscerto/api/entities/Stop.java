package com.onibuscerto.api.entities;

public interface Stop extends Location {

    public String getId();

    public String getName();

    public void setName(String name);

    public String getZoneId();

    public void setZoneId(String zoneId);

    public FareAttribute getFare();

    public void setFare(FareAttribute fare);
}
