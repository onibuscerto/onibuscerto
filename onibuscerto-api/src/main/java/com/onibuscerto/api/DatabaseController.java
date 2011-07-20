package com.onibuscerto.api;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DatabaseController {

    private static final String DEFAULT_DATABASE_PATH = "target/db";
    protected GraphDatabaseService graphDb;
    protected StopFactory stopFactory;
    protected Transaction currentTransaction;

    public DatabaseController(String databasePath) {
        graphDb = new EmbeddedGraphDatabase(databasePath);
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
        }

        currentTransaction.finish();

        currentTransaction = null;
    }

    public StopFactory getStopFactory() {
        if (stopFactory == null) {
            stopFactory = new StopFactoryImpl(graphDb);
        }

        return stopFactory;
    }
}
