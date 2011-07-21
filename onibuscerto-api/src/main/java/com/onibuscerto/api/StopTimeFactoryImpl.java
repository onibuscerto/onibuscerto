package com.onibuscerto.api;

import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.factories.StopTimeFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class StopTimeFactoryImpl implements StopTimeFactory {

    private final GraphDatabaseService graphDb;
    private final Node stopTimeFactoryNode;

    StopTimeFactoryImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.STOP_TIMES, Direction.OUTGOING);

        if (rel == null) {
            stopTimeFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    stopTimeFactoryNode, Relationships.STOP_TIMES);
        } else {
            stopTimeFactoryNode = rel.getEndNode();
        }
    }

    @Override
    public StopTime createStopTime() {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            stopTimeFactoryNode.createRelationshipTo(
                    node, Relationships.STOP_TIME);
            tx.success();
            return new StopTimeImpl(node);
        } finally {
            tx.finish();
        }
    }
}
