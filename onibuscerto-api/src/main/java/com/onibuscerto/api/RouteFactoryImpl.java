package com.onibuscerto.api;

import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.factories.RouteFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

class RouteFactoryImpl implements RouteFactory {

    private final GraphDatabaseService graphDb;
    private final Node routeFactoryNode;
    private final Index<Node> routeIndex;
    private static final String ROUTE_INDEX = "route_index";

    RouteFactoryImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

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

    @Override
    public Route getRouteById(String id) {
        Node node = routeIndex.get(RouteImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new RouteImpl(node);
    }
}
