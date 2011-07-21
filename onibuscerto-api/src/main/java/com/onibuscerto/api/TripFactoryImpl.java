package com.onibuscerto.api;

import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.factories.TripFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

class TripFactoryImpl implements TripFactory {

    private final GraphDatabaseService graphDb;
    private final Node tripFactoryNode;
    private final Index<Node> tripIndex;
    private static final String TRIP_INDEX = "trip_index";

    TripFactoryImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

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

    @Override
    public Trip getTripById(String id) {
        Node node = tripIndex.get(TripImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new TripImpl(node);
    }
}
