package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.Location;
import com.onibuscerto.core.entities.Stop;
import com.onibuscerto.core.entities.TransportConnection;
import com.onibuscerto.core.entities.Trip;
import com.onibuscerto.core.entities.WalkingConnection;

public interface ConnectionFactory {

    public TransportConnection createTransportConnection(Trip trip, Stop source, Stop target);

    public WalkingConnection createWalkingConnection(Location source, Location target);
}
