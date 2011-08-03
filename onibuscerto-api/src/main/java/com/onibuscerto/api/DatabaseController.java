package com.onibuscerto.api;

import com.onibuscerto.api.factories.ConnectionFactory;
import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.StopFactory;
import com.onibuscerto.api.factories.StopTimeFactory;
import com.onibuscerto.api.factories.TripFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public final class DatabaseController {

    private static final String DEFAULT_DATABASE_PATH = "target/db";
    protected final GraphDatabaseService graphDb;
    protected final StopFactory stopFactory;
    protected final RouteFactory routeFactory;
    protected final TripFactory tripFactory;
    protected final StopTimeFactory stopTimeFactory;
    protected final ConnectionFactory connectionFactory;
    protected Transaction currentTransaction;

    public DatabaseController(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        beginTransaction();
        stopFactory = new StopFactoryImpl(this);
        routeFactory = new RouteFactoryImpl(this);
        tripFactory = new TripFactoryImpl(this);
        stopTimeFactory = new StopTimeFactoryImpl(this);
        connectionFactory = new ConnectionFactoryImpl(this);
        endTransaction(true);
    }

    public DatabaseController(String databasePath) {
        this(new EmbeddedGraphDatabase(databasePath));
    }

    public DatabaseController() {
        this(DEFAULT_DATABASE_PATH);
    }

    public void close() {
        graphDb.shutdown();
    }

    GraphDatabaseService getGraphDatabaseService() {
        return graphDb;
    }

    public void beginTransaction() {
        if (currentTransaction != null) {
            // TODO: colocar uma mensagem nessa exceção
            throw new RuntimeException();
        }

        currentTransaction = graphDb.beginTx();
    }

    public void endTransaction(boolean wasSuccessful) {
        if (wasSuccessful) {
            currentTransaction.success();
        } else {
            currentTransaction.failure();
        }

        currentTransaction.finish();

        currentTransaction = null;
    }

    public StopFactory getStopFactory() {
        return stopFactory;
    }

    public RouteFactory getRouteFactory() {
        return routeFactory;
    }

    public TripFactory getTripFactory() {
        return tripFactory;
    }

    public StopTimeFactory getStopTimeFactory() {
        return stopTimeFactory;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
