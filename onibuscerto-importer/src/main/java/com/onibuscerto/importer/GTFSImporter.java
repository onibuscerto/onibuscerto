package com.onibuscerto.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.StopFactory;
import com.onibuscerto.api.factories.StopTimeFactory;
import com.onibuscerto.api.factories.TripFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GTFSImporter {

    private DatabaseController databaseController;

    public GTFSImporter(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public void importData(String transitFeedPath) {
        databaseController.beginTransaction();
        try {
            importStops(transitFeedPath + "/stops.txt");
            importRoutes(transitFeedPath + "/routes.txt");
            importTrips(transitFeedPath + "/trips.txt");
            importStopTimes(transitFeedPath + "/stop_times.txt");
            linkStopTimes();
            databaseController.endTransaction(true);
        } catch (IOException ex) {
            Logger.getLogger(GTFSImporter.class.getName()).log(Level.SEVERE, null, ex);
            databaseController.endTransaction(false);
        }
    }

    private void importStops(String stopsFile)
            throws IOException {
        StopFactory stopFactory = databaseController.getStopFactory();
        CSVReader reader = new CSVReader(new FileReader(stopsFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            Stop stop = stopFactory.createStop(hashMap.get("stop_id"));
            stop.setName(hashMap.get("stop_name"));
            stop.setLatitude(Double.parseDouble(hashMap.get("stop_lat")));
            stop.setLongitude(Double.parseDouble(hashMap.get("stop_lon")));

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri stop " + stop.getName() + " (" + stop.getId() + ")");

            hashMap.clear();
        }
    }

    private void importRoutes(String routesFile) throws IOException {
        RouteFactory routeFactory = databaseController.getRouteFactory();
        CSVReader reader = new CSVReader(new FileReader(routesFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            Route route = routeFactory.createRoute(hashMap.get("route_id"));
            route.setShortname(hashMap.get("route_short_name"));
            route.setLongName(hashMap.get("route_long_name"));
            route.setType(Route.Type.fromInt(Integer.parseInt(
                    hashMap.get("route_type"))));

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri route " + route.getShortName() + " (" + route.getId() + ")");

            hashMap.clear();
        }
    }

    private void importTrips(String tripsFile) throws IOException {
        TripFactory tripFactory = databaseController.getTripFactory();
        CSVReader reader = new CSVReader(new FileReader(tripsFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            Trip trip = tripFactory.createTrip(hashMap.get("trip_id"));
            trip.setRoute(databaseController.getRouteFactory().getRouteById(
                    hashMap.get("route_id")));

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri trip " + trip.getId());

            hashMap.clear();
        }
    }

    private void importStopTimes(String stopTimesFile) throws IOException {
        StopTimeFactory stopTimeFactory = databaseController.getStopTimeFactory();
        CSVReader reader = new CSVReader(new FileReader(stopTimesFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            StopTime stopTime = stopTimeFactory.createStopTime();
            stopTime.setTrip(databaseController.getTripFactory().getTripById(
                    hashMap.get("trip_id")));
            stopTime.setArrivalTime(getSecondsFromTime(hashMap.get("arrival_time")));
            stopTime.setDepartureTime(getSecondsFromTime(hashMap.get("departure_time")));
            stopTime.setStop(databaseController.getStopFactory().getStopById(
                    hashMap.get("stop_id")));
            stopTime.setSequence(Integer.parseInt(hashMap.get("stop_sequence")));

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri StopTime da trip " + stopTime.getTrip().getId()
                    + " numero de sequencia " + stopTime.getSequence());

            hashMap.clear();
        }
    }

    private void linkStopTimes() {
        Collection<Trip> allTrips = databaseController.getTripFactory().getAllTrips();
        LinkedList<StopTime> stopTimes;

        for (Trip trip : allTrips) {
            stopTimes = new LinkedList<StopTime>(trip.getStopTimes());
            Collections.sort(stopTimes, new Comparator() {

                @Override
                public int compare(Object o1, Object o2) {
                    StopTime st1 = (StopTime) o1;
                    StopTime st2 = (StopTime) o2;
                    return ((Integer) st1.getSequence()).compareTo(st2.getSequence());
                }
            });
            while (stopTimes.isEmpty() == false) {
                StopTime stopTime = stopTimes.pop();
                if (stopTimes.isEmpty() == false) {
                    stopTime.setNext(stopTimes.peekFirst());
                }
            }
        }
    }

    private int getSecondsFromTime(String time) {
        int seconds = 0;
        String[] parts = time.split(":");
        seconds += Integer.parseInt(parts[0]) * 3600;
        seconds += Integer.parseInt(parts[1]) * 60;
        seconds += Integer.parseInt(parts[2]);
        return seconds;
    }
}
