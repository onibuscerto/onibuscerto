package com.onibuscerto.api;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class StopFactoryImpl implements StopFactory {

    private final GraphDatabaseService graphDb;
    private final Node stopFactoryNode;

    public StopFactoryImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.STOP_FACTORY, Direction.OUTGOING);

        if (rel == null) {
            stopFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    stopFactoryNode, Relationships.STOP_FACTORY);
        } else {
            stopFactoryNode = rel.getEndNode();
        }
    }

    @Override
    public Stop createStop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
