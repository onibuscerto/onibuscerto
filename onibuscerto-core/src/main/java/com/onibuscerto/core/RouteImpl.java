package com.onibuscerto.core;

import com.onibuscerto.core.entities.FareAttribute;
import com.onibuscerto.core.entities.FareRule;
import java.util.Collection;
import org.neo4j.graphdb.Node;
import com.onibuscerto.core.entities.Route;
import com.onibuscerto.core.entities.Trip;
import java.util.LinkedList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;

class RouteImpl implements Route {

    private final Node underlyingNode;
    static final String KEY_ID = "route_id";
    private static final String KEY_SHORT_NAME = "route_short_name";
    private static final String KEY_LONG_NAME = "route_long_name";
    private static final String KEY_TYPE = "route_type";
    private static final String KEY_ROUTE_COLOR = "route_color";

    RouteImpl(Node underlyingNode, String id) {
        this(underlyingNode);
        setId(id);
    }

    RouteImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public String getId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    private void setId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    @Override
    public String getShortName() {
        return (String) underlyingNode.getProperty(KEY_SHORT_NAME);
    }

    @Override
    public void setShortName(String shortName) {
        underlyingNode.setProperty(KEY_SHORT_NAME, shortName);
    }

    @Override
    public String getLongName() {
        return (String) underlyingNode.getProperty(KEY_LONG_NAME);
    }

    @Override
    public void setLongName(String longName) {
        underlyingNode.setProperty(KEY_LONG_NAME, longName);
    }

    @Override
    public Type getType() {
        return Type.fromInt((Integer) underlyingNode.getProperty(KEY_TYPE));
    }

    @Override
    public void setType(Type type) {
        underlyingNode.setProperty(KEY_TYPE, type.toInt());
    }

    private Collection<Node> getTripNodes() {
        Traverser traverser = underlyingNode.traverse(
                Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                Relationships.ROUTE_TO_TRIP, Direction.OUTGOING);
        return traverser.getAllNodes();
    }

    @Override
    public Collection<Trip> getTrips() {
        Collection<Node> tripNodes = getTripNodes();
        Collection<Trip> trips = new LinkedList<Trip>();

        for (Node node : tripNodes) {
            trips.add(new TripImpl(node));
        }

        return trips;
    }

    @Override
    public void setRouteColor(String color) {
        underlyingNode.setProperty(KEY_ROUTE_COLOR, color);
    }

    @Override
    public String getRouteColor() {
        return (String) underlyingNode.getProperty(KEY_ROUTE_COLOR);
    }

    @Override
    public boolean hasRouteColor() {
        return underlyingNode.hasProperty(KEY_ROUTE_COLOR);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof RouteImpl) {
            return getUnderlyingNode().equals(
                    ((RouteImpl) object).getUnderlyingNode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
