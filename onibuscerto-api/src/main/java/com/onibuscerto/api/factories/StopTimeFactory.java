package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.StopTime;

public interface StopTimeFactory {

    public StopTime createStopTime(String id);

    public StopTime getStopTimeById(String id);
}
