package com.onibuscerto.api;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class StopFactoryImpl implements StopFactory {

    private final GraphDatabaseService graphDb;
    private final Node stopFactoryNode;

    public StopFactoryImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.STOPS, Direction.OUTGOING);

        if (rel == null) {
            stopFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    stopFactoryNode, Relationships.STOPS);
        } else {
            stopFactoryNode = rel.getEndNode();
        }
    }

    @Override
    public Stop createStop() {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            stopFactoryNode.createRelationshipTo(node, Relationships.STOP);
            tx.success();
            return new StopImpl(node);
        } finally {
            tx.finish();
        }
    }
}
