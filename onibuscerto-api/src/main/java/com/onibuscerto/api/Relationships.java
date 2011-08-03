package com.onibuscerto.api;

import org.neo4j.graphdb.RelationshipType;

enum Relationships implements RelationshipType {

    STOPS,
    STOP,
    ROUTES,
    ROUTE,
    TRIPS,
    TRIP,
    STOPTIMES,
    STOPTIME,
    TRIP_TO_STOPTIME,
    STOP_TO_STOPTIME,
    ROUTE_TO_TRIP,
    NEXT_STOPTIME,
    TRANSPORT_CONNECTION,
    WALKING_CONNECTION,
}
