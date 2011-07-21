package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Route;

public interface RouteFactory {

    public Route createRoute();

    public Route getRouteById(String id);
}
