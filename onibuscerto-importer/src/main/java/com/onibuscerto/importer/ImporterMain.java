package com.onibuscerto.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.StopFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

public class ImporterMain {

    private static final String TRANSIT_FEED_PATH = "src/main/resources";

    public static void main(String args[]) {
        DatabaseController databaseController = new DatabaseController();

        databaseController.beginTransaction();

        try {
            importStops(databaseController, TRANSIT_FEED_PATH + "/stops.txt");
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

        while ((lineValues = reader.readNext()) != null) {
            Stop stop = stopFactory.createStop(null);

            for (int i = 0; i < lineValues.length; i++) {
                if (columnNames[i].equals("stop_id")) {
                    stop.setId(lineValues[i]);
                } else if (columnNames[i].equals("stop_name")) {
                    stop.setName(lineValues[i]);
                } else if (columnNames[i].equals("stop_lat")) {
                    stop.setLatitude(Double.parseDouble(lineValues[i]));
                } else if (columnNames[i].equals("stop_lon")) {
                    stop.setLongitude(Double.parseDouble(lineValues[i]));
                }
            }

            Logger.getLogger(ImporterMain.class.getName()).log(Level.INFO,
                    "Inseri stop " + stop.getName() + " (" + stop.getId() + ")");
        }
    }

    private static void importRoutes(DatabaseController databaseController,
            String routesFile) throws IOException {
        RouteFactory routeFactory = databaseController.getRouteFactory();
        CSVReader reader = new CSVReader(new FileReader(routesFile));
        String columnNames[] = reader.readNext();
        String lineValues[];

        while ((lineValues = reader.readNext()) != null) {
            Route route = routeFactory.createRoute(null);

            for (int i = 0; i < lineValues.length; i++) {
                if (columnNames[i].equals("route_id")) {
                    route.setId(lineValues[i]);
                } else if (columnNames[i].equals("route_short_name")) {
                    route.setShortname(lineValues[i]);
                } else if (columnNames[i].equals("route_long_name")) {
                    route.setLongName(lineValues[i]);
                } else if (columnNames[i].equals("route_type")) {
                    route.setType(getTypeFromCode(lineValues[i]));
                }
            }
            Logger.getLogger(ImporterMain.class.getName()).log(Level.INFO,
                    "Inseri route " + route.getShortName() + " (" + route.getId() + ")");
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
