package com.onibuscerto.importer;

import com.onibuscerto.api.DatabaseController;

public class ImporterMain {

    private static final String TRANSIT_FEED_PATH = "src/test/resources/bart";

    public static void main(String args[]) {
        DatabaseController databaseController = new DatabaseController();
        GTFSImporter importer = new GTFSImporter(databaseController);
        importer.importData(TRANSIT_FEED_PATH);
    }
}
