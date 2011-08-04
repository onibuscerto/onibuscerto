package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.entities.WalkingConnection;

public interface ConnectionFactory {

    public TransportConnection createTransportConnection(Trip trip, Stop source, Stop target);

    public WalkingConnection createWalkingConnection(Location source, Location target);
}
