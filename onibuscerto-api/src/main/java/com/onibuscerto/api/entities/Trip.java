package com.onibuscerto.api.entities;

import java.util.Collection;

public interface Trip {

    public Route getRoute();

    public void setRoute(Route route);

    public Calendar getCalendar();

    public void setCalendar(Calendar calendar);

    public String getId();

    public Collection<StopTime> getStopTimes();

    public ShapePoint getShape();

    public void setShape(ShapePoint shapeFirstPoint);

    public StopTime getFirstStopTime();

    public void setFirstStopTime(StopTime stopTime);
}
