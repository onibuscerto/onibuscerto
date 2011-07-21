package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Trip;

public interface TripFactory {

    public Trip createTrip(String id);

    public Trip getTripById(String id);
}
