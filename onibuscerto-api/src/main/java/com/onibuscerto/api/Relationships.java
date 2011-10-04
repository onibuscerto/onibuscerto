package com.onibuscerto.api;

import org.neo4j.graphdb.RelationshipType;

enum Relationships implements RelationshipType {

    LOCATIONS,
    STOP,
    ROUTES,
    ROUTE,
    TRIPS,
    TRIP,
    STOPTIMES,
    STOPTIME,
    STOPTIME_TO_SHAPE,
    TRIP_TO_STOPTIME,
    TRIP_TO_FIRST_STOPTIME,
    STOP_TO_STOPTIME,
    STOP_TO_FARE,
    ROUTE_TO_TRIP,
    NEXT_STOPTIME,
    TRANSPORT_CONNECTION,
    WALKING_CONNECTION,
    SHAPE_POINTS,
    SHAPE_POINT,
    NEXT_SHAPE_POINT,
    SHAPES,
    SHAPE_FIRST_POINT,
    TRIP_TO_SHAPE_POINT,
    CALENDARS,
    CALENDAR,
    CALENDAR_TO_TRIP,
    FARE_ATTRIBUTES,
    FARE_ATTRIBUTE,
    FARE_RULES,
    FARE_RULE,
    FARE_RULE_TO_FARE_ATTRIBUTE,
    FARE_RULE_TO_SOURCE,
    FARE_RULE_TO_TARGET
}
