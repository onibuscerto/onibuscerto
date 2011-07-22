package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Trip;
import java.util.Collection;

public interface TripFactory {

    public Trip createTrip(String id);

    public Collection<Trip> getAllTrips();

    public Trip getTripById(String id);
}
