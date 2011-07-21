package com.onibuscerto.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.factories.StopFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            Stop stop = stopFactory.createStop();

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
}