package com.onibuscerto.api.utils;

public final class QueryResponseConnection {

    private GlobalPosition start;
    private GlobalPosition end;
    private int routeType;

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
}
