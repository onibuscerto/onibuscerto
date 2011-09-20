package com.onibuscerto.api;

import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.exceptions.DuplicateEntityException;
import com.onibuscerto.api.factories.RouteFactory;
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

class RouteFactoryImpl implements RouteFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node routeFactoryNode;
    private final Index<Node> routeIndex;
    private static final String ROUTE_INDEX = "route_index";

    RouteFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.ROUTES, Direction.OUTGOING);

        if (rel == null) {
            routeFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    routeFactoryNode, Relationships.ROUTES);
        } else {
            routeFactoryNode = rel.getEndNode();
        }

        routeIndex = graphDb.index().forNodes(ROUTE_INDEX);
    }

    @Override
    public Route createRoute(String id) {
        Transaction tx = graphDb.beginTx();
        try {
            if (getRouteById(id) != null) {
                throw new DuplicateEntityException();
            }
            Node node = graphDb.createNode();
            Route route = new RouteImpl(node, id);
            routeIndex.add(node, RouteImpl.KEY_ID, id);
            routeFactoryNode.createRelationshipTo(node, Relationships.ROUTE);
            tx.success();
            return route;
        } finally {
            tx.finish();
        }
    }

    private Collection<Node> getAllRouteNodes() {
        Traverser traverser = routeFactoryNode.traverse(
                Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE,
                Relationships.STOP, Direction.OUTGOING);
        return traverser.getAllNodes();
    }

    @Override
    public Collection<Route> getAllRoutes() {
        Collection<Node> allRouteNodes = getAllRouteNodes();
        Collection<Route> allRoutes = new LinkedList<Route>();

        for (Node node : allRouteNodes) {
            allRoutes.add(new RouteImpl(node));
        }

        return allRoutes;
    }

    @Override
    public Route getRouteById(String id) {
        Node node = routeIndex.get(RouteImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new RouteImpl(node);
    }
}
