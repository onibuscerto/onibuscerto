package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Trip {

    public Route getRoute();

    public void setRoute(Route route);

    public String getId();

    public Collection<StopTime> getStopTimes();
}
