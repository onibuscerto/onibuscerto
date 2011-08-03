package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Stop {

    public String getId();

    public String getName();

    public void setName(String name);

    public double getLatitude();

    public void setLatitude(double latitude);

    public double getLongitude();

    public void setLongitude(double longitude);

    public Collection<Connection> getOutgoingConnections();

    public Collection<Connection> getIncomingConnections();
}
