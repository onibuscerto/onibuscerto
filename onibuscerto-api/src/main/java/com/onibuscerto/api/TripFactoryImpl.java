package com.onibuscerto.api;

import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.factories.TripFactory;
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

class TripFactoryImpl implements TripFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node tripFactoryNode;
    private final Index<Node> tripIndex;
    private static final String TRIP_INDEX = "trip_index";

    TripFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.TRIPS, Direction.OUTGOING);

        if (rel == null) {
            tripFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    tripFactoryNode, Relationships.STOPS);
        } else {
            tripFactoryNode = rel.getEndNode();
        }

        tripIndex = graphDb.index().forNodes(TRIP_INDEX);
    }

    @Override
    public Trip createTrip(String id) {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            Trip trip = new TripImpl(node, id);
            tripIndex.add(node, TripImpl.KEY_ID, id);
            tripFactoryNode.createRelationshipTo(node, Relationships.TRIP);
            tx.success();
            return trip;
        } finally {
            tx.finish();
        }
    }

    private Collection<Node> getAllTripNodes() {
        Traverser traverser = tripFactoryNode.traverse(
                Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                Relationships.TRIP, Direction.OUTGOING);
        return traverser.getAllNodes();
    }

    @Override
    public Collection<Trip> getAllTrips() {
        Collection<Node> allTripNodes = getAllTripNodes();
        Collection<Trip> allTrips = new LinkedList<Trip>();

        for (Node node : allTripNodes) {
            allTrips.add(new TripImpl(node));
        }
        return allTrips;
    }

    @Override
    public Trip getTripById(String id) {
        Node node = tripIndex.get(TripImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new TripImpl(node);
    }
}
