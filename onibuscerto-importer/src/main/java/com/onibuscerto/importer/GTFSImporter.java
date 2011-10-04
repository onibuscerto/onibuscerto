package com.onibuscerto.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.StopTimeImpl;
import com.onibuscerto.api.entities.Calendar;
import com.onibuscerto.api.entities.FareAttribute;
import com.onibuscerto.api.entities.FareRule;
import com.onibuscerto.api.entities.Route;
import com.onibuscerto.api.entities.ShapePoint;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.TransportConnection;
import com.onibuscerto.api.entities.Trip;
import com.onibuscerto.api.entities.WalkingConnection;
import com.onibuscerto.api.factories.CalendarFactory;
import com.onibuscerto.api.factories.ConnectionFactory;
import com.onibuscerto.api.factories.FareAttributeFactory;
import com.onibuscerto.api.factories.FareRuleFactory;
import com.onibuscerto.api.factories.RouteFactory;
import com.onibuscerto.api.factories.ShapePointFactory;
import com.onibuscerto.api.factories.LocationFactory;
import com.onibuscerto.api.factories.StopTimeFactory;
import com.onibuscerto.api.factories.TripFactory;
import com.onibuscerto.api.utils.GlobalPosition;
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
    private static final int MAX_WALKING_DISTANCE = 1000;

    public GTFSImporter(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public void importData(String transitFeedPath) {
        databaseController.beginTransaction();
        try {
            importCalendars(transitFeedPath + "/calendar.txt");
            importFareAttributes(transitFeedPath + "/fare_attributes.txt");
            importShapes(transitFeedPath + "/shapes.txt");
            importStops(transitFeedPath + "/stops.txt");
            importRoutes(transitFeedPath + "/routes.txt");
            importTrips(transitFeedPath + "/trips.txt");
            importStopTimes(transitFeedPath + "/stop_times.txt");
            importFareRules(transitFeedPath + "/fare_rules.txt");
            linkStopTimes();
            assignShapes();
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
            stop.setZoneId(hashMap.get("zone_id"));

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
            if (hashMap.containsKey("route_color")
                    && !hashMap.get("route_color").isEmpty()) {
                route.setRouteColor(hashMap.get("route_color"));
            }
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
            if (hashMap.containsKey("shape_id")
                    && !hashMap.get("shape_id").isEmpty()) {
                trip.setShape(databaseController.getShapePointFactory().getShapeById(
                        hashMap.get("shape_id")));
            }

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

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri ShapePoint da shape " + shapePoint.getShapeId()
                    + " numero de sequencia " + shapePoint.getSequence());

            hashMap.clear();

        }

        for (Entry<String, ArrayList<ShapePoint>> entry : shapes.entrySet()) {
            linkShapePoints(entry.getValue());
            shapePointFactory.setShapeFirstPoint(entry.getValue().get(0));
        }
    }

    private void importCalendars(String calendarFile)
            throws IOException {
        CalendarFactory calendarFactory = databaseController.getCalendarFactory();
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
                calendar.setDaysOfWork(day[i], hashMap.get(day[i]));
            }
            calendar.setStartDate(hashMap.get("start_date"));
            calendar.setEndDate(hashMap.get("end_date"));

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri calendar " + calendar.getServiceId());

            hashMap.clear();
        }
    }

    private void importFareAttributes(String fareAttributesFile)
            throws IOException {
        FareAttributeFactory fareAttributeFactory = databaseController.getFareAttributeFactory();
        CSVReader reader = new CSVReader(new FileReader(fareAttributesFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();

        while ((lineValues = reader.readNext()) != null) {
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            FareAttribute fareAttribute = fareAttributeFactory.createFareAttribute(hashMap.get("fare_id"));
            fareAttribute.setPrice(Double.parseDouble(hashMap.get("price")));
            fareAttribute.setCurrencyType(hashMap.get("currency_type"));
            fareAttribute.setPaymentMethod(Integer.parseInt(hashMap.get("payment_method")));
            if (hashMap.get("transfers").isEmpty()) {
                fareAttribute.setTransfers(-1);
            } else {
                fareAttribute.setTransfers(Integer.parseInt(hashMap.get("transfers")));
            }
            if (hashMap.containsKey("transfer_duration")
                    && !hashMap.get("transfer_duration").isEmpty()) {
                fareAttribute.setTransferDuration(
                        Integer.parseInt(hashMap.get("transfer_duration")));
            }

            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                    "Inseri fare " + fareAttribute.getFareId());

            hashMap.clear();
        }
    }

    private void importFareRules(String fareRulesFile) throws IOException {
        FareRuleFactory fareRuleFactory = databaseController.getFareRuleFactory();
        CSVReader reader = new CSVReader(new FileReader(fareRulesFile));
        String columnNames[] = reader.readNext();
        String lineValues[];
        HashMap<String, String> hashMap = new HashMap<String, String>();
        Collection<Trip> allTrips;
        Collection<StopTime> allstopTimes;
        Collection<Stop> allstops = databaseController.getLocationFactory().getAllStops();
        String fareRuleId;
        if (reader.readNext() == null) {
            for (Stop stop : allstops) {
                FareRule fareRule = fareRuleFactory.createFareRule(stop.getId());
            }
        }
        while ((lineValues = reader.readNext()) != null) {
            FareRule fareRule;
            fareRuleId = "";
            for (int i = 0; i < lineValues.length; i++) {
                hashMap.put(columnNames[i], lineValues[i]);
            }

            if (hashMap.containsKey("route_id")
                    && !hashMap.get("route_id").isEmpty()) {

                Route route = databaseController.getRouteFactory().getRouteById(
                        hashMap.get("route_id"));
                allTrips = route.getTrips();
                for (Trip trip : allTrips) {
                    allstopTimes = trip.getStopTimes();
                    for (StopTime stopTime : allstopTimes) {
                        if (databaseController.getFareRuleFactory().getFareRuleById(
                                stopTime.getStop().getId()) == null) {
                            fareRule = fareRuleFactory.createFareRule(stopTime.getStop().getId());
                            fareRule.setFareAttribute(databaseController.getFareAttributeFactory().getFareAttributeById(hashMap.get("fare_id")));
                            Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                                    "Inseri FareRule ByRoute"
                                    + fareRule.getFareAttribute().getFareId());
                        }
                    }
                }

            } else {

                if (hashMap.containsKey("origin_id")
                        && !hashMap.get("origin_id").isEmpty()
                        && hashMap.containsKey("destination_id")
                        && !hashMap.get("destination_id").isEmpty()) {
                    for (Stop stop : allstops) {
                        if (stop.getZoneId().equals(hashMap.get("origin_id"))) {
                            fareRuleId += stop.getId();
                        }
                    }
                    for (Stop stop : allstops) {
                        if (stop.getZoneId().equals(hashMap.get("destination_id"))) {
                            fareRuleId += stop.getId();
                        }
                    }
                    //fareRule = fareRuleFactory.createFareRule(fareRuleId);

                } else if (hashMap.containsKey("origin_id")
                        && !hashMap.get("origin_id").isEmpty()) {
                    for (Stop stop : allstops) {
                        if (stop.getZoneId().equals(hashMap.get("origin_id"))) {
                            fareRuleId += stop.getId();
                        }
                    }

                } else if (hashMap.containsKey("destination_id")
                        && !hashMap.get("destination_id").isEmpty()) {
                    for (Stop stop : allstops) {
                        if (stop.getZoneId().equals(hashMap.get("destination_id"))) {
                            fareRuleId += stop.getId();
                        }
                    }
                } else if (hashMap.containsKey("contains_id")
                        && !hashMap.get("contains_id").isEmpty()) {
                }

                fareRule = fareRuleFactory.createFareRule(fareRuleId);
                fareRule.setFareAttribute(databaseController.getFareAttributeFactory().getFareAttributeById(hashMap.get("fare_id")));

                Logger.getLogger(GTFSImporter.class.getName()).log(Level.INFO,
                        "Inseri FareRule Origin/Dest"
                        + fareRule.getFareAttribute().getFareId());
            }

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
            trip.setFirstStopTime(stopTimes.getFirst());
            while (stopTimes.isEmpty() == false) {
                StopTime stopTime = stopTimes.pop();
                stopTime.setNext(stopTimes.peekFirst());
            }
        }
    }

    private void assignShapes() {
        Collection<Trip> allTrips = databaseController.getTripFactory().getAllTrips();
        HashMap<StopTime, Integer> hashMap;

        for (Trip trip : allTrips) {
            ShapePoint shapeFirstPoint = trip.getShape();
            if (shapeFirstPoint == null) {
                continue;
            }
            StopTime firstStopTime = trip.getFirstStopTime();
            hashMap = new HashMap<StopTime, Integer>();


            if (firstStopTime.hasShapeDistTraveled()) {
                StopTime currentStopTime = firstStopTime;
                StopTime nextStopTime = firstStopTime.getNext();
                ShapePoint currentShapePoint = shapeFirstPoint.getNext();
                hashMap.put(firstStopTime, shapeFirstPoint.getSequence());
                while (nextStopTime != null && currentShapePoint != null) {
                    if (currentShapePoint.getSequence() > currentStopTime.getShapeDistTraveled()) {
                        currentStopTime = nextStopTime;
                        nextStopTime = nextStopTime.getNext();
                        hashMap.put(currentStopTime, currentShapePoint.getSequence());
                    }
                    currentShapePoint = currentShapePoint.getNext();
                }
            } else {
                StopTime currentStopTime = firstStopTime;
                while (currentStopTime != null) {
                    ShapePoint closestShapePoint = shapeFirstPoint;
                    ShapePoint currentShapePoint = shapeFirstPoint.getNext();
                    double bestDistance = closestShapePoint.getGlobalPosition().
                            getDistanceTo(currentStopTime.getStop().getGlobalPosition());

                    while (currentShapePoint != null) {
                        double distance = currentShapePoint.getGlobalPosition().
                                getDistanceTo(currentStopTime.getStop().getGlobalPosition());
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            closestShapePoint = currentShapePoint;
                        }
                        currentShapePoint = currentShapePoint.getNext();
                    }

                    hashMap.put(currentStopTime, closestShapePoint.getSequence());
                    currentStopTime.setShape(closestShapePoint);
                    currentStopTime = currentStopTime.getNext();
                }
            }

            StopTime currentStopTime = firstStopTime;
            StopTime nextStopTime = firstStopTime.getNext();
            int length = 0;
            ShapePoint currentShapePoint = shapeFirstPoint;

            while (currentShapePoint.getSequence() < hashMap.get(currentStopTime)) {
                currentShapePoint = currentShapePoint.getNext();
            }

            while (nextStopTime != null && currentShapePoint != null) {
                if (hashMap.get(nextStopTime) < currentShapePoint.getSequence()) {
                    length++;
                    currentShapePoint = currentShapePoint.getNext();
                } else {
                    currentStopTime.setShapeLength(length);
                    length = 0;
                    currentStopTime = nextStopTime;
                    nextStopTime = nextStopTime.getNext();
                }
            }
            length = 0;
            while (currentShapePoint != null) {
                currentShapePoint = currentShapePoint.getNext();
                length++;
            }
            currentStopTime.setShapeLength(length);
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
        double walkingDistance;
        GlobalPosition gp;
        GlobalPosition gp2;
        Collection<Trip> allTrips = databaseController.getTripFactory().getAllTrips();
        ConnectionFactory connectionFactory = databaseController.getConnectionFactory();
        LinkedList<StopTime> stopTimes;

        for (Trip trip : allTrips) {
            //FIXME: seria mais economico pegar apenas o primeiro stoptime da trip e iterar
            // por ele, agora tem um metodo pra isso
            stopTimes = new LinkedList<StopTime>(trip.getStopTimes());

            for (StopTime stopTime : stopTimes) {
                if (stopTime.hasNext()) {
                    TransportConnection connection = connectionFactory.createTransportConnection(
                            stopTime.getTrip(), stopTime.getStop(), stopTime.getNext().getStop());
                    connection.setDepartureTime(stopTime.getDepartureTime());

                    // FIXME: esse calculo talvez esteja errado
                    connection.setTimeCost(stopTime.getNext().getArrivalTime() - stopTime.getDepartureTime());

                    //WalkingConnection entre stops com no max 1km de distância
                    gp = new GlobalPosition(
                            stopTime.getStop().getLatitude(), stopTime.getStop().getLatitude());
                    gp2 = new GlobalPosition(
                            stopTime.getNext().getStop().getLatitude(), stopTime.getNext().getStop().getLatitude());
                    walkingDistance = gp.getDistanceTo(gp2);

                    if (walkingDistance <= MAX_WALKING_DISTANCE) {
                        WalkingConnection wConnection = connectionFactory.createWalkingConnection(
                                stopTime.getStop(), stopTime.getNext().getStop());
                        wConnection.setWalkingDistance(walkingDistance);
                    }
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
