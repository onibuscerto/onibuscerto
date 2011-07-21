package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Stop;
import java.util.Collection;

public interface StopFactory {

    public Stop createStop();

    public Collection<Stop> getAllStops();

    public Stop getStopById(String id);
}
