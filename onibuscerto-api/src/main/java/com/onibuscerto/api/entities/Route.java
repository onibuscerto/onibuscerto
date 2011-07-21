package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Route {

    public String getId();

    public String getShortName();

    public void setShortname(String shortName);

    public String getLongName();

    public void setLongName(String longName);

    public Type getType();

    public void setType(Type type);

    public Collection<Trip> getTrips();

    public enum Type {

        STREETCAR,
        SUBWAY,
        RAIL,
        BUS,
        FERRY,
        CABLE_CAR,
        GONDOLA,
        FUNICULAR
    };
}
