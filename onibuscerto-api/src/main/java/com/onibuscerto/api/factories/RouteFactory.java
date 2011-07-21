package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Route;

public interface RouteFactory {

    public Route createRoute(String id);

    public Route getRouteById(String id);
}
