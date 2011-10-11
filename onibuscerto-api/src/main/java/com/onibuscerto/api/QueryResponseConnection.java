package com.onibuscerto.api;

import com.onibuscerto.api.GlobalPosition;

public final class QueryResponseConnection {

    private GlobalPosition start;
    private GlobalPosition end;
    private int routeType;
    private String routeColor;
    private String routeLongName;
    private String startStopName;
    private int departureTime;

    public GlobalPosition getEnd() {
        return end;
    }

    public void setEnd(GlobalPosition end) {
        this.end = end;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public GlobalPosition getStart() {
        return start;
    }

    public void setStart(GlobalPosition start) {
        this.start = start;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getStartStopName() {
        return startStopName;
    }

    public void setStartStopName(String startStopName) {
        this.startStopName = startStopName;
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }
}
