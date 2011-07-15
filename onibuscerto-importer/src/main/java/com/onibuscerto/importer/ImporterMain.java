package com.onibuscerto.importer;

import au.com.bytecode.opencsv.CSVReader;
import com.onibuscerto.api.Stop;
import com.onibuscerto.api.StopFactory;
import com.onibuscerto.api.StopFactoryImpl;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class ImporterMain {

    private static final String DATABASE_PATH = "target/db";
    private static final String TRANSIT_FEED_PATH = "src/main/resources";

    public static void main(String args[]) {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase(DATABASE_PATH);
        Transaction tx = graphDb.beginTx();

        try {
            importStops(graphDb, TRANSIT_FEED_PATH + "/stops.txt");
            tx.success();
        } catch (IOException ex) {
            Logger.getLogger(ImporterMain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            tx.finish();
        }

        graphDb.shutdown();
    }

    private static void importStops(GraphDatabaseService graphDb, String stopsFile)
            throws IOException {
        StopFactory stopFactory = new StopFactoryImpl(graphDb);
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
