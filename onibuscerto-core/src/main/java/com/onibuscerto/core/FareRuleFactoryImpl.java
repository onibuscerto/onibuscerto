package com.onibuscerto.core;

import com.onibuscerto.core.entities.FareRule;
import com.onibuscerto.core.exceptions.DuplicateEntityException;
import com.onibuscerto.core.factories.FareRuleFactory;
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
import org.neo4j.graphdb.index.Index;

public class FareRuleFactoryImpl implements FareRuleFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node fareRuleFactoryNode;
    private final Index<Node> fareRuleIndex;
    private static final String FARE_RULE_INDEX = "fare_rule_index";

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
        fareRuleIndex = graphDb.index().forNodes(FARE_RULE_INDEX);
    }

    private Collection<Node> getAllFareRuleNodes() {
        Traverser traverser = fareRuleFactoryNode.traverse(
                Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                Relationships.FARE_RULE, Direction.OUTGOING);
        return traverser.getAllNodes();
    }

    @Override
    public Collection<FareRule> getAllFareRules() {
        Collection<Node> allFareRuleNodes = getAllFareRuleNodes();
        Collection<FareRule> allFareRules = new LinkedList<FareRule>();

        for (Node node : allFareRuleNodes) {
            allFareRules.add(new FareRuleImpl(node));
        }
        return allFareRules;
    }

    @Override
    public FareRule createFareRule(String id) {
        Transaction tx = graphDb.beginTx();
        try {
            if (getFareRuleById(id) != null) {
                throw new DuplicateEntityException();
            }

            Node node = graphDb.createNode();
            FareRule fare = new FareRuleImpl(node, id);
            fareRuleIndex.add(node, FareRuleImpl.KEY_ID, id);
            fareRuleFactoryNode.createRelationshipTo(
                    node, Relationships.FARE_RULE);
            tx.success();
            return fare;
        } finally {
            tx.finish();
        }
    }

    @Override
    public FareRule getFareRuleById(String id) {
        Node node = fareRuleIndex.get(FareRuleImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new FareRuleImpl(node);
    }
}
