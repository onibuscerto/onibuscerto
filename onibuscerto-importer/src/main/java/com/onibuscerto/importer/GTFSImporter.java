package com.onibuscerto.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Calendar;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.ShapePoint;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.factories.CalendarFactory;
import com.onibuscerto.api.factories.ConnectionFactory;
import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.ShapePointFactory;
import com.onibuscerto.api.factories.LocationFactory;
import com.onibuscerto.api.factories.StopTimeFactory;
import com.onibuscerto.api.factories.TripFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
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
            importCalendars(transitFeedPath + "/calendar.txt");
            importShapes(transitFeedPath + "/shapes.txt");
            importStops(transitFeedPath + "/stops.txt");
            importRoutes(transitFeedPath + "/routes.txt");
            importTrips(transitFeedPath + "/trips.txt");
            importStopTimes(transitFeedPath + "/stop_times.txt");
            linkStopTimes();
            createConnections();
            databaseController.endTransaction(true);
        } catch (IOException ex) {
            Logger.getLogger(GTFSImporter.class.getName()).log(Level.SEVERE, null, ex);
            databaseController.endTransaction(false);
        }
    }

    private void importStops(String stopsFile)
            throws IOException {
        LocationFactory locationFactory = databaseController.getLocationFactory();
        CSVReader reader = new CSVReader(new FileReader(stopsFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            Stop stop = locationFactory.createStop(hashMap.get("stop_id"));
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
            route.setShortName(hashMap.get("route_short_name"));
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
            trip.setCalendar(databaseController.getCalendarFactory().getCalendarById(
                    hashMap.get("service_id")));

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
            stopTime.setStop(databaseController.getLocationFactory().getStopById(
                    hashMap.get("stop_id")));
            stopTime.setSequence(Integer.parseInt(hashMap.get("stop_sequence")));
            if (hashMap.containsKey("shape_dist_traveled")
                    && !hashMap.get("shape_dist_traveled").isEmpty()) {
                double dist = Double.parseDouble(hashMap.get("shape_dist_traveled"));
                stopTime.setShapeDistTraveled(dist);
            }

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri StopTime da trip " + stopTime.getTrip().getId()
                    + " numero de sequencia " + stopTime.getSequence());

            hashMap.clear();
        }
    }

    private void importShapes(String shapesFile) throws IOException {
        ShapePointFactory shapePointFactory = databaseController.getShapePointFactory();
        CSVReader reader = new CSVReader(new FileReader(shapesFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();
        HashMap<String, ArrayList<ShapePoint>> shapes = new HashMap<String, ArrayList<ShapePoint>>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            ShapePoint shapePoint = shapePointFactory.createShapePoint();
            shapePoint.setShapeId(hashMap.get("shape_id"));
            shapePoint.setLatitude(Double.parseDouble(hashMap.get("shape_pt_lat")));
            shapePoint.setLongitude(Double.parseDouble(hashMap.get("shape_pt_lon")));
            shapePoint.setSequence(Integer.parseInt(hashMap.get("shape_pt_sequence")));
            if (hashMap.containsKey("shape_dist_traveled")) {
                shapePoint.setShapeDistTraveled(Double.parseDouble(hashMap.get(
                        "shape_dist_traveled")));
            }

            if (shapes.containsKey(shapePoint.getShapeId()) == false) {
                ArrayList<ShapePoint> shapePoints = new ArrayList<ShapePoint>();
                shapePoints.add(shapePoint);
                shapes.put(shapePoint.getShapeId(), shapePoints);
            } else {
                shapes.get((String) shapePoint.getShapeId()).add(shapePoint);
            }

            for (Entry<String, ArrayList<ShapePoint>> entry : shapes.entrySet()) {
                linkShapePoints(entry.getValue());
                shapePointFactory.setShapeFirstPoint(entry.getValue().get(0));
            }

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri ShapePoint da shape " + shapePoint.getShapeId()
                    + " numero de sequencia " + shapePoint.getSequence());

            hashMap.clear();

        }
    }

    private void importCalendars(String calendarFile)
            throws IOException {
        CalendarFactory calendarFactory = databaseController.getCalendarFactory();
        Collection<String> days = null;
        CSVReader reader = new CSVReader(new FileReader(calendarFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        String[] day = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        HashMap<String, String> hashMap = new HashMap<String, String>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            Calendar calendar = calendarFactory.createCalendar(hashMap.get("service_id"));
            for (int i = 0; i < day.length; i++) {
                //days.add(hashMap.get(day[i]));
                calendar.setDaysOfWork(day[i], hashMap.get(day[i]));
            }
            //calendar.setDaysOfWork(days);
            calendar.setStartDate(hashMap.get("start_date"));
            calendar.setEndDate(hashMap.get("end_date"));

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri calendar " + calendar.getServiceId());

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
                stopTime.setNext(stopTimes.peekFirst());
            }
        }
    }

    private void linkShapePoints(ArrayList<ShapePoint> shape) {
        Collections.sort(shape, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                ShapePoint sp1 = (ShapePoint) o1;
                ShapePoint sp2 = (ShapePoint) o2;
                return ((Integer) sp1.getSequence()).compareTo(sp2.getSequence());
            }
        });
        for (int i = 0; i < shape.size() - 1; i++) {
            shape.get(i).setNext(shape.get(i + 1));
        }

    }

    private void createConnections() {
        Collection<Trip> allTrips = databaseController.getTripFactory().getAllTrips();
        ConnectionFactory connectionFactory = databaseController.getConnectionFactory();
        LinkedList<StopTime> stopTimes;

        for (Trip trip : allTrips) {
            stopTimes = new LinkedList<StopTime>(trip.getStopTimes());

            for (StopTime stopTime : stopTimes) {
                if (stopTime.hasNext()) {
                    TransportConnection connection = connectionFactory.createTransportConnection(
                            stopTime.getTrip(), stopTime.getStop(), stopTime.getNext().getStop());
                    connection.setDepartureTime(stopTime.getDepartureTime());

                    // FIXME: esse calculo talvez esteja errado
                    connection.setTimeCost(stopTime.getNext().getArrivalTime() - stopTime.getDepartureTime());
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
