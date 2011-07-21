package com.onibuscerto.api;

import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.StopFactory;
import com.onibuscerto.api.factories.TripFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public final class DatabaseController {

    private static final String DEFAULT_DATABASE_PATH = "target/db";
    protected GraphDatabaseService graphDb;
    protected StopFactory stopFactory;
    protected RouteFactory routeFactory;
    protected TripFactory tripFactory;
    protected Transaction currentTransaction;

    public DatabaseController(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        beginTransaction();
        stopFactory = new StopFactoryImpl(graphDb);
        routeFactory = new RouteFactoryImpl(graphDb);
        tripFactory = new TripFactoryImpl(graphDb);
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
}
