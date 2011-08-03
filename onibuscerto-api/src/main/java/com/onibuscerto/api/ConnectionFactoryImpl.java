package com.onibuscerto.api;

import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.entities.WalkingConnection;
import com.onibuscerto.api.factories.ConnectionFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

class ConnectionFactoryImpl implements ConnectionFactory {

    private final GraphDatabaseService graphDb;

    ConnectionFactoryImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
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
    public WalkingConnection createWalkingConnection(Stop source, Stop target) {
        Transaction tx = graphDb.beginTx();
        try {
            Node sourceNode = ((StopImpl) source).getUnderlyingNode();
            Node targetNode = ((StopImpl) target).getUnderlyingNode();
            Relationship relationship = sourceNode.createRelationshipTo(targetNode, Relationships.WALKING_CONNECTION);
            WalkingConnection walkingConnection = new WalkingConnectionImpl(relationship);
            tx.success();
            return walkingConnection;
        } finally {
            tx.finish();
        }
    }
}
