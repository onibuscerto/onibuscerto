package com.onibuscerto.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.StopFactory;
import com.onibuscerto.api.factories.TripFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImporterMain {

    private static final String TRANSIT_FEED_PATH = "src/main/resources";

    public static void main(String args[]) {
        DatabaseController databaseController = new DatabaseController();

        databaseController.beginTransaction();

        try {
            importStops(databaseController, TRANSIT_FEED_PATH + "/stops.txt");
            importRoutes(databaseController, TRANSIT_FEED_PATH + "/routes.txt");
            importTrips(databaseController, TRANSIT_FEED_PATH + "/trips.txt");
            databaseController.endTransaction(true);
        } catch (IOException ex) {
            Logger.getLogger(ImporterMain.class.getName()).log(Level.SEVERE, null, ex);
            databaseController.endTransaction(false);
        }

        databaseController.close();
    }

    private static void importStops(DatabaseController databaseController, String stopsFile)
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

            Logger.getLogger(ImporterMain.class.getName()).log(Level.INFO,
                    "Inseri stop " + stop.getName() + " (" + stop.getId() + ")");

            hashMap.clear();
        }
    }

    private static void importRoutes(DatabaseController databaseController,
            String routesFile) throws IOException {
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
            route.setType(getTypeFromCode(hashMap.get("route_type")));

            Logger.getLogger(ImporterMain.class.getName()).log(Level.INFO,
                    "Inseri route " + route.getShortName() + " (" + route.getId() + ")");

            hashMap.clear();
        }
    }

    private static void importTrips(DatabaseController databaseController,
            String tripsFile) throws IOException {
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

            Logger.getLogger(ImporterMain.class.getName()).log(Level.INFO,
                    "Inseri trip " + trip.getId());

            hashMap.clear();
        }
    }

    private static Route.Type getTypeFromCode(String code) {
        Route.Type type = null;
        switch (Integer.parseInt(code)) {
            case 0:
                type = Route.Type.STREETCAR;
                break;
            case 1:
                type = Route.Type.SUBWAY;
                break;
            case 2:
                type = Route.Type.RAIL;
                break;
            case 3:
                type = Route.Type.BUS;
                break;
            case 4:
                type = Route.Type.FERRY;
                break;
            case 5:
                type = Route.Type.CABLE_CAR;
                break;
            case 6:
                type = Route.Type.GONDOLA;
                break;
            case 7:
                type = Route.Type.FUNICULAR;
        }
        return type;
    }
}
