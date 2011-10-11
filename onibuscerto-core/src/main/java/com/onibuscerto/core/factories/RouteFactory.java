package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.Route;
import java.util.Collection;

public interface RouteFactory {

    public Route createRoute(String id);

    public Route getRouteById(String id);

    public Collection<Route> getAllRoutes();
}
