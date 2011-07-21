package com.onibuscerto.importer;

import com.onibuscerto.api.DatabaseController;

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
}
