package com.onibuscerto.api;

import com.onibuscerto.api.factories.StopFactory;
import com.onibuscerto.api.entities.Stop;
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

class StopFactoryImpl implements StopFactory {

    private final GraphDatabaseService graphDb;
    private final Node stopFactoryNode;

    StopFactoryImpl(GraphDatabaseService graphDb) {
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
    public Stop createStop(String id) {
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

    private Collection<Node> getAllStopNodes() {
        Traverser traverser = stopFactoryNode.traverse(
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
