package com.onibuscerto.api;

import org.neo4j.graphdb.RelationshipType;

enum Relationships implements RelationshipType {

    STOPS,
    STOP,
    ROUTES,
    ROUTE,
    TRIPS,
    TRIP,
    STOP_TIMES,
    STOP_TIME,
    TRIP_TO_STOP_TIME,
    STOP_TO_STOP_TIME,
    ROUTE_TO_TRIP
}
