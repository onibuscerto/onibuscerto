package com.onibuscerto.api;

import com.onibuscerto.api.entities.Connection;
import com.onibuscerto.api.entities.Location;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.factories.CalendarFactory;
import com.onibuscerto.api.factories.ConnectionFactory;
import com.onibuscerto.api.factories.FareAttributeFactory;
import com.onibuscerto.api.factories.FareRuleFactory;
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
    protected final CalendarFactory calendarFactory;
    protected final FareAttributeFactory fareAttributeFactory;
    protected final FareRuleFactory fareRuleFactory;
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
        calendarFactory = new CalendarFactoryImpl(this);
        fareAttributeFactory = new FareAttributeFactoryImpl(this);
        fareRuleFactory = new FareRuleFactoryImpl(this);
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

    public Collection<Connection> getShortestPath(Location source, Location target, int time) {
        HashMap<Location, Integer> d = new HashMap<Location, Integer>();
        HashMap<Location, Connection> p = new HashMap<Location, Connection>();
        HashSet<Location> in = new HashSet<Location>();
        Collection<Connection> path = new LinkedList<Connection>();
        Collection<Location> allLocations = (Collection) getLocationFactory().getAllStops();

        if (!(source instanceof Stop)) {
            allLocations.add(source);
        }

        if (!(target instanceof Stop)) {
            allLocations.add(target);
        }

        for (Location location : allLocations) {
            d.put(location, 0x3f3f3f3f);
        }
        d.put(source, time);

        while (!in.contains(target)) {
            Location location = null;
            int best = 0x3f3f3f3f;
            for (Location l : allLocations) {
                if (!in.contains(l) && best > d.get(l)) {
                    best = d.get(location = l);
                }
            }

            if (location == null) {
                return null;
            }

            in.add(location);
            time = d.get(location);

            for (Connection c : location.getOutgoingConnections()) {
                int waitingTime = 0;

                if (c instanceof TransportConnection) {
                    waitingTime = ((TransportConnection) c).getDepartureTime() - time;
                    if (waitingTime < 0) {
                        continue;
                    }
                }

                if (!in.contains(c.getTarget()) && d.get(c.getTarget()) > time + waitingTime + c.getTimeCost()) {
                    d.put(c.getTarget(), time + waitingTime + c.getTimeCost());
                    p.put(c.getTarget(), c);
                }
            }
        }

        for (Location location = target;; location = p.get(location).getSource()) {
            if (!p.containsKey(location)) {
                break;
            }
            path.add(p.get(location));
        }
        Collections.reverse((LinkedList) path);

        /* DEBUG NERVOSO {{{
        for (Location location : allLocations) {
            if (location instanceof Stop) {
                Stop stop = (Stop) location;
                System.out.println("d[" + stop.getId() + "] = " + d.get(location));
            }
            if (location.equals(source)) {
                System.out.println("d[origem] = " + d.get(location));
            }
            if (location.equals(target)) {
                System.out.println("d[destino] = " + d.get(location));
            }
        }

        for (Connection connection : path) {
            if (connection instanceof TransportConnection) {
                TransportConnection tc = (TransportConnection) connection;
                System.out.print("TransportConnection ");
                System.out.print(((Stop) tc.getSource()).getId() + " -> ");
                System.out.println(((Stop) tc.getTarget()).getId() + " (departure: " + tc.getDepartureTime() + ")");
            }
        }
        }}} */

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

    public CalendarFactory getCalendarFactory() {
        return calendarFactory;
    }

    public FareAttributeFactory getFareAttributeFactory() {
        return fareAttributeFactory;
    }

    public FareRuleFactory getFareRuleFactory() {
        return fareRuleFactory;
    }
}
