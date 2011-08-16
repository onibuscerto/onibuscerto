package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.entities.Stop;
import java.util.Collection;

public interface LocationFactory {

    public Stop createStop(String id);

    public Location createLocation();

    public Collection<Stop> getAllStops();

    public Stop getStopById(String id);
}
