package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Stop extends Location {

    public String getId();

    public String getName();

    public void setName(String name);
}
