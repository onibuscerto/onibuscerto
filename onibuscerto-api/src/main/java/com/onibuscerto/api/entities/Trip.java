package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Trip {

    public Route getRoute();

    public String getId();

    public void setId(String id);

    public Collection<StopTime> getStopTimes();
}
