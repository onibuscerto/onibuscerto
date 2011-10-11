package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.Trip;
import java.util.Collection;

public interface TripFactory {

    public Trip createTrip(String id);

    public Collection<Trip> getAllTrips();

    public Trip getTripById(String id);
}
