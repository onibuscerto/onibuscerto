package com.onibuscerto.api;

import java.util.Collection;
import org.neo4j.graphdb.Node;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.Trip;
import java.util.LinkedList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;

class RouteImpl implements Route {

    private final Node underlyingNode;
    static final String KEY_ID = "route_id";
    private static final String KEY_SHORT_NAME = "route_short_name";
    private static final String KEY_LONG_NAME = "route_long_name";
    private static final String KEY_TYPE = "route_type";

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

    private final void setId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    @Override
    public String getShortName() {
        return (String) underlyingNode.getProperty(KEY_SHORT_NAME);
    }

    @Override
    public void setShortname(String shortName) {
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
        return (Type) underlyingNode.getProperty(KEY_TYPE);
    }

    @Override
    public void setType(Type type) {
        underlyingNode.setProperty(KEY_TYPE, type);
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
