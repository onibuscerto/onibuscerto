package com.onibuscerto.api;

import com.onibuscerto.api.entities.FareRule;
import com.onibuscerto.api.factories.FareRuleFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class FareRuleFactoryImpl implements FareRuleFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node fareRuleFactoryNode;

    FareRuleFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.FARE_RULES, Direction.OUTGOING);

        if (rel == null) {
            fareRuleFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    fareRuleFactoryNode, Relationships.FARE_RULES);
        } else {
            fareRuleFactoryNode = rel.getEndNode();
        }
    }

    @Override
    public FareRule createFareRule() {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            fareRuleFactoryNode.createRelationshipTo(
                    node, Relationships.FARE_RULE);
            tx.success();
            return new FareRuleImpl(node);
        } finally {
            tx.finish();
        }
    }
}
