package com.onibuscerto.api;

import com.onibuscerto.api.entities.Calendar;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.ShapePoint;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Trip;
import java.util.Collection;
import java.util.LinkedList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;

class TripImpl implements Trip {

    private final Node underlyingNode;
    static final String KEY_ID = "trip_id";

    TripImpl(Node underlyingNode, String id) {
        this(underlyingNode);
        setId(id);
    }

    TripImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public Route getRoute() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.ROUTE_TO_TRIP, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new RouteImpl(rel.getStartNode());
        }
    }

    @Override
    public void setRoute(Route route) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.ROUTE_TO_TRIP, Direction.INCOMING);
        RouteImpl routeImpl = (RouteImpl) route;

        if (rel != null) {
            rel.delete();
        }

        routeImpl.getUnderlyingNode().createRelationshipTo(underlyingNode,
                Relationships.ROUTE_TO_TRIP);
    }

    @Override
    public Calendar getCalendar() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.CALENDAR_TO_TRIP, Direction.INCOMING);

        if (rel == null) {
            return null;
        } else {
            return new CalendarImpl(rel.getStartNode());
        }
    }

    @Override
    public void setCalendar(Calendar calendar) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.CALENDAR_TO_TRIP, Direction.INCOMING);
        CalendarImpl calendarImpl = (CalendarImpl) calendar;

        if (rel != null) {
            rel.delete();
        }

        calendarImpl.getUnderlyingNode().createRelationshipTo(underlyingNode,
                Relationships.CALENDAR_TO_TRIP);
    }

    @Override
    public String getId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    private void setId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    private Collection<Node> getStopTimeNodes() {
        Traverser traverser = underlyingNode.traverse(
                Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                Relationships.TRIP_TO_STOPTIME, Direction.OUTGOING);
        return traverser.getAllNodes();
    }

    @Override
    public Collection<StopTime> getStopTimes() {
        Collection<Node> stopTimeNodes = getStopTimeNodes();
        Collection<StopTime> stopTimes = new LinkedList<StopTime>();

        for (Node node : stopTimeNodes) {
            stopTimes.add(new StopTimeImpl(node));
        }

        return stopTimes;
    }

    @Override
    public ShapePoint getShape() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.TRIP_TO_SHAPE_POINT, Direction.OUTGOING);

        if (rel == null) {
            return null;
        } else {
            return new ShapePointImpl(rel.getEndNode());
        }
    }

    @Override
    public void setShape(ShapePoint shapeFirstPoint) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.TRIP_TO_SHAPE_POINT, Direction.OUTGOING);
        ShapePointImpl shapePoint = (ShapePointImpl) shapeFirstPoint;

        if (rel != null) {
            rel.delete();
        }

        getUnderlyingNode().createRelationshipTo(shapePoint.getUnderlyingNode(),
                Relationships.TRIP_TO_SHAPE_POINT);
    }

    @Override
    public StopTime getFirstStopTime() {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.TRIP_TO_FIRST_STOPTIME, Direction.OUTGOING);

        if (rel == null) {
            return null;
        } else {
            return new StopTimeImpl(rel.getEndNode());
        }
    }

    @Override
    public void setFirstStopTime(StopTime firstStopTime) {
        Relationship rel = underlyingNode.getSingleRelationship(
                Relationships.TRIP_TO_FIRST_STOPTIME, Direction.OUTGOING);
        StopTimeImpl stopTime = (StopTimeImpl) firstStopTime;

        if (rel != null) {
            rel.delete();
        }

        getUnderlyingNode().createRelationshipTo(stopTime.getUnderlyingNode(),
                Relationships.TRIP_TO_FIRST_STOPTIME);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof TripImpl) {
            return getUnderlyingNode().equals(
                    ((TripImpl) object).getUnderlyingNode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingNode().hashCode();
    }
}
