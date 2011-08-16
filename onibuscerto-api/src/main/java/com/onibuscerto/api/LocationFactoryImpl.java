package com.onibuscerto.api;

import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.factories.LocationFactory;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.exceptions.DuplicateEntityException;
import java.util.Collection;
import java.util.LinkedList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.index.Index;

class LocationFactoryImpl implements LocationFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node locationFactoryNode;
    private final Index<Node> stopIndex;
    private static final String STOP_INDEX = "stop_index";

    LocationFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.LOCATIONS, Direction.OUTGOING);

        if (rel == null) {
            locationFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    locationFactoryNode, Relationships.LOCATIONS);
        } else {
            locationFactoryNode = rel.getEndNode();
        }

        stopIndex = graphDb.index().forNodes(STOP_INDEX);
    }

    @Override
    public Stop createStop(String id) {
        Transaction tx = graphDb.beginTx();
        try {
            if (getStopById(id) != null) {
                throw new DuplicateEntityException();
            }

            Node node = graphDb.createNode();
            Stop stop = new StopImpl(node, id);
            stopIndex.add(node, StopImpl.KEY_ID, id);
            locationFactoryNode.createRelationshipTo(node, Relationships.STOP);
            tx.success();
            return stop;
        } finally {
            tx.finish();
        }
    }

    @Override
    public Location createLocation() {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            Location location = new LocationImpl(node);
            tx.success();
            return location;
        } finally {
            tx.finish();
        }
    }

    private Collection<Node> getAllStopNodes() {
        Traverser traverser = locationFactoryNode.traverse(
                Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                Relationships.STOP, Direction.OUTGOING);
        return traverser.getAllNodes();
    }

    @Override
    public Collection<Stop> getAllStops() {
        Collection<Node> allStopNodes = getAllStopNodes();
        Collection<Stop> allStops = new LinkedList<Stop>();

        for (Node node : allStopNodes) {
            allStops.add(new StopImpl(node));
        }

        return allStops;
    }

    @Override
    public Stop getStopById(String id) {
        Node node = stopIndex.get(StopImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new StopImpl(node);
    }
}
