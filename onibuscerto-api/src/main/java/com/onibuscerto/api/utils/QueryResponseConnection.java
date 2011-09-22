package com.onibuscerto.api.utils;

public final class QueryResponseConnection {

    private GlobalPosition start;
    private GlobalPosition end;
    private int routeType;
    private String routeLongName;
    private String startStopName;

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
}
