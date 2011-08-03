package com.onibuscerto.api;

import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.factories.StopTimeFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class StopTimeFactoryImpl implements StopTimeFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node stopTimeFactoryNode;

    StopTimeFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.STOPTIMES, Direction.OUTGOING);

        if (rel == null) {
            stopTimeFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    stopTimeFactoryNode, Relationships.STOPTIMES);
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
                    node, Relationships.STOPTIME);
            tx.success();
            return new StopTimeImpl(node);
        } finally {
            tx.finish();
        }
    }
}
