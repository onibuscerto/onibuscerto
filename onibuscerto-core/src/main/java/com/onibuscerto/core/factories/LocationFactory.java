package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.Location;
import com.onibuscerto.core.entities.Stop;
import java.util.Collection;

public interface LocationFactory {

    public Stop createStop(String id);

    public Location createLocation();

    public Collection<Stop> getAllStops();

    public Stop getStopById(String id);
}
