package com.onibuscerto.api;

import com.onibuscerto.api.entities.ShapePoint;
import com.onibuscerto.api.factories.ShapePointFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class ShapePointFactoryImpl implements ShapePointFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node shapePointFactoryNode;

    ShapePointFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.SHAPE_POINTS, Direction.OUTGOING);

        if (rel == null) {
            shapePointFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    shapePointFactoryNode, Relationships.SHAPE_POINTS);
        } else {
            shapePointFactoryNode = rel.getEndNode();
        }
    }

    @Override
    public ShapePoint createShapePoint() {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            ShapePoint shapePoint = new ShapePointImpl(node);
            shapePointFactoryNode.createRelationshipTo(node,
                    Relationships.SHAPE_POINT);
            tx.success();
            return shapePoint;
        } finally {
            tx.finish();
        }
    }
}
