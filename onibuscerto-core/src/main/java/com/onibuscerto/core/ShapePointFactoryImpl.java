package com.onibuscerto.core;

import com.onibuscerto.core.entities.ShapePoint;
import com.onibuscerto.core.factories.ShapePointFactory;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

public class ShapePointFactoryImpl implements ShapePointFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node shapePointFactoryNode;
    private final Node shapeStartNode;

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

        rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.SHAPES, Direction.OUTGOING);

        if (rel == null) {
            shapeStartNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    shapeStartNode, Relationships.SHAPES);
        } else {
            shapeStartNode = rel.getEndNode();
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

    public void setShapeFirstPoint(ShapePoint shapePoint) {
        Relationship rel = ((ShapePointImpl) shapePoint).getUnderlyingNode().getSingleRelationship(
                Relationships.SHAPE_FIRST_POINT, Direction.INCOMING);

        if (rel != null) {
            rel.delete();
        }

        if (shapePoint != null) {
            shapeStartNode.createRelationshipTo(
                    ((ShapePointImpl) shapePoint).getUnderlyingNode(),
                    Relationships.SHAPE_FIRST_POINT);
        }
    }

    @Override
    public ShapePoint getShapeById(String id) {
        final String shapeId = id;
        ReturnableEvaluator evaluator = new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition position) {
                if (position.isStartNode()) {
                    return false;
                } else {
                    if (position.currentNode().getProperty(ShapePointImpl.KEY_SHAPE_ID).equals(shapeId)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };
        Iterator iterator = shapeStartNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE,
                evaluator, Relationships.SHAPE_FIRST_POINT, Direction.OUTGOING).iterator();

        if (iterator.hasNext() == false) {
            return null;
        }

        return new ShapePointImpl((Node) iterator.next());
    }
}
