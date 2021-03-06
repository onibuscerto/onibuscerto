package com.onibuscerto.core;

import com.onibuscerto.core.entities.Location;
import com.onibuscerto.core.entities.Stop;
import com.onibuscerto.core.entities.TransportConnection;
import com.onibuscerto.core.entities.Trip;
import com.onibuscerto.core.entities.WalkingConnection;
import com.onibuscerto.core.factories.ConnectionFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

class ConnectionFactoryImpl implements ConnectionFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;

    ConnectionFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();
    }

    @Override
    public TransportConnection createTransportConnection(Trip trip, Stop source, Stop target) {
        Transaction tx = graphDb.beginTx();
        try {
            Node sourceNode = ((StopImpl) source).getUnderlyingNode();
            Node targetNode = ((StopImpl) target).getUnderlyingNode();
            Relationship relationship = sourceNode.createRelationshipTo(targetNode, Relationships.TRANSPORT_CONNECTION);
            TransportConnection transportConnection = new TransportConnectionImpl(relationship, trip);
            tx.success();
            return transportConnection;
        } finally {
            tx.finish();
        }
    }

    @Override
    public WalkingConnection createWalkingConnection(Location source, Location target) {
        Transaction tx = graphDb.beginTx();
        try {
            Node sourceNode = ((LocationImpl) source).getUnderlyingNode();
            Node targetNode = ((LocationImpl) target).getUnderlyingNode();
            Relationship relationship = sourceNode.createRelationshipTo(targetNode, Relationships.WALKING_CONNECTION);
            WalkingConnection walkingConnection = new WalkingConnectionImpl(relationship);
            tx.success();
            return walkingConnection;
        } finally {
            tx.finish();
        }
    }
}
