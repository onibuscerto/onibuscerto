package com.onibuscerto.api;

import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.factories.TripFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

class TripFactoryImpl implements TripFactory {

    private final GraphDatabaseService graphDb;
    private final Node tripFactoryNode;

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
    }

    @Override
    public Trip createTrip() {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            tripFactoryNode.createRelationshipTo(node, Relationships.TRIP);
            tx.success();
            return new TripImpl(node);
        } finally {
            tx.finish();
        }
    }
}
