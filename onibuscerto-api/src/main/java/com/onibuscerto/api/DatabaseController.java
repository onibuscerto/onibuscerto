package com.onibuscerto.api;

import com.onibuscerto.api.entities.Connection;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.factories.ConnectionFactory;
import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.ShapePointFactory;
import com.onibuscerto.api.factories.LocationFactory;
import com.onibuscerto.api.factories.StopTimeFactory;
import com.onibuscerto.api.factories.TripFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public final class DatabaseController {

    private static final String DEFAULT_DATABASE_PATH = "target/db";
    protected final GraphDatabaseService graphDb;
    protected final LocationFactory locationFactory;
    protected final RouteFactory routeFactory;
    protected final TripFactory tripFactory;
    protected final StopTimeFactory stopTimeFactory;
    protected final ConnectionFactory connectionFactory;
    protected final ShapePointFactory shapePointFactory;
    protected Transaction currentTransaction;

    public DatabaseController(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        beginTransaction();
        locationFactory = new LocationFactoryImpl(this);
        routeFactory = new RouteFactoryImpl(this);
        tripFactory = new TripFactoryImpl(this);
        stopTimeFactory = new StopTimeFactoryImpl(this);
        connectionFactory = new ConnectionFactoryImpl(this);
        shapePointFactory = new ShapePointFactoryImpl(this);
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

    public Collection<Connection> getShortestPath(Stop source, Stop target, int time) {
        HashMap<Stop, Integer> d = new HashMap<Stop, Integer>();
        HashMap<Stop, Connection> p = new HashMap<Stop, Connection>();
        HashSet<Stop> in = new HashSet<Stop>();
        Collection<Connection> path = new LinkedList<Connection>();
        Collection<Stop> allStops = getLocationFactory().getAllStops();

        for (Stop stop : allStops) {
            d.put(stop, 0x3f3f3f3f);
        }
        d.put(source, time);

        while (!in.contains(target)) {
            Stop stop = null;
            int best = 0x3f3f3f3f;
            for (Stop s : allStops) {
                if (!in.contains(s) && best > d.get(s)) {
                    best = d.get(stop = s);
                }
            }

            if (stop == null) {
                return null;
            }

            in.add(stop);
            time = d.get(stop);

            for (Connection c : stop.getOutgoingConnections()) {
                int waitingTime = 0;

                if (c instanceof TransportConnection) {
                    waitingTime = ((TransportConnection) c).getDepartureTime() - time;
                    if (waitingTime <= 0) {
                        continue;
                    }
                }

                if (!in.contains((Stop) c.getTarget()) && d.get((Stop) c.getTarget()) > time + c.getTimeCost()) {
                    d.put((Stop) c.getTarget(), time + c.getTimeCost());
                    p.put((Stop) c.getTarget(), c);
                }
            }
        }

        for (Stop stop = target;; stop = (Stop) p.get(stop).getSource()) {
            if (!p.containsKey(stop)) {
                break;
            }
            path.add(p.get(stop));
        }
        Collections.reverse((LinkedList) path);

        return path;
    }

    public LocationFactory getLocationFactory() {
        return locationFactory;
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

    public ShapePointFactory getShapePointFactory() {
        return shapePointFactory;
    }
}
