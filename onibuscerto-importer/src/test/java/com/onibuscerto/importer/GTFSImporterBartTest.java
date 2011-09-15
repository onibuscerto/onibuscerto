package com.onibuscerto.importer;

import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Stop;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;
import static org.junit.Assert.*;

public class GTFSImporterBartTest {
    
    private static final String TRANSIT_FEED_PATH = "src/test/resources/bart";
    private static DatabaseController databaseController;
    private static GTFSImporter importer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase();
        databaseController = new DatabaseController(graphDb);
        importer = new GTFSImporter(databaseController);
        importer.importData(TRANSIT_FEED_PATH);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        databaseController.close();
    }

    @Before
    public void setUp() {
        databaseController.beginTransaction();
    }

    @After
    public void tearDown() {
        databaseController.endTransaction(false);
    }
    
    @Test
    public void testImportedStops() {
        Collection<Stop> allStops = databaseController.getLocationFactory().getAllStops();
        assertEquals(allStops.size(), 47);
    }
}
