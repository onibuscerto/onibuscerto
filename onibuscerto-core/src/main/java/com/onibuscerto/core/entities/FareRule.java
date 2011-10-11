package com.onibuscerto.core.entities;

import java.util.Collection;

public interface FareRule {

    public String getId();

    public void setId(String id);

    public FareAttribute getFareAttribute();

    public void setFareAttribute(FareAttribute fare);

    public Stop getSource();

    public void setSource(Stop source);

    public Stop getTarget();

    public void setTarget(Stop target);

    public Collection<Stop> getStopsFromFare();

    public void setStopsFromFare(Collection<Stop> stops);
}
