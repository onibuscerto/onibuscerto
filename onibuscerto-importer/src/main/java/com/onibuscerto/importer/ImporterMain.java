package com.onibuscerto.importer;

import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Trip;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class ImporterMain {

    private static final String TRANSIT_FEED_PATH = "src/main/resources";
    private GTFSImporter importer;
    private DatabaseController databaseController;

    public ImporterMain() {
        databaseController = new DatabaseController();
        importer = new GTFSImporter(databaseController);
        importer.importData(TRANSIT_FEED_PATH);
    }

    public static void main(String args[]) {
        new ImporterMain();
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
}
