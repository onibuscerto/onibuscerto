package com.onibuscerto.api;

import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.factories.RouteFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class RouteFactoryImpl implements RouteFactory {
    
    private final GraphDatabaseService graphDb;
    private final Node routeFactoryNode;
    
    RouteFactoryImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
        
        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.ROUTES, Direction.OUTGOING);
        
        if(rel == null) {
            routeFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    routeFactoryNode, Relationships.ROUTES);
        } else {
            routeFactoryNode = rel.getEndNode();
        }
                
    }

    @Override
    public Route createRoute() {
        Transaction tx = graphDb.beginTx();
        try {
            Node node = graphDb.createNode();
            routeFactoryNode.createRelationshipTo(node, Relationships.ROUTE);
            tx.success();
            return new RouteImpl(node);
        } finally {
            tx.finish();
        }
    }
    
}
