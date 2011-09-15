package com.onibuscerto.api;

import com.onibuscerto.api.entities.FareAttribute;
import com.onibuscerto.api.exceptions.DuplicateEntityException;
import com.onibuscerto.api.factories.FareAttributeFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

public class FareAttributeFactoryImpl implements FareAttributeFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node fareAttributeFactoryNode;
    private final Index<Node> fareAttributeIndex;
    private static final String FARE_ATTRIBUTE_INDEX = "fare_attribute_index";

    FareAttributeFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.CALENDARS, Direction.OUTGOING);

        if (rel == null) {
            fareAttributeFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    fareAttributeFactoryNode, Relationships.FARE_ATTRIBUTES);
        } else {
            fareAttributeFactoryNode = rel.getEndNode();
        }

        fareAttributeIndex = graphDb.index().forNodes(FARE_ATTRIBUTE_INDEX);
    }

    @Override
    public FareAttribute createFareAttribute(String id) {
        Transaction tx = graphDb.beginTx();
        try {
            if (getFareAttributeById(id) != null) {
                throw new DuplicateEntityException();
            }

            Node node = graphDb.createNode();
            FareAttribute fareAttribute = new FareAttributeImpl(node, id);
            fareAttributeIndex.add(node, FareAttributeImpl.KEY_ID, id);
            fareAttributeFactoryNode.createRelationshipTo(node, Relationships.FARE_ATTRIBUTE);
            tx.success();
            return fareAttribute;
        } finally {
            tx.finish();
        }
    }

    @Override
    public FareAttribute getFareAttributeById(String id) {
        Node node = fareAttributeIndex.get(FareAttributeImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new FareAttributeImpl(node);
    }
}
