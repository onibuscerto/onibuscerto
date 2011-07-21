package com.onibuscerto.api.test;

import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Stop;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;

public class StopTest {

    private static DatabaseController databaseController;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase();
        databaseController = new DatabaseController(graphDb);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        databaseController.close();
    }

    @Before
    public void setUp() throws Exception {
        databaseController.beginTransaction();
    }

    @After
    public void tearDown() throws Exception {
        // Faz um rollback após o teste
        databaseController.endTransaction(false);
    }

    @Test
    public void testCreateStop() {
        String stopId = "stop42";
        Stop stop = databaseController.getStopFactory().createStop(stopId);
        assertEquals(stop.getId(), stopId);
    }

    @Test
    public void testSetGetName() {
        Stop stop = databaseController.getStopFactory().createStop("stop42");
        String expResult = "alsgj42n";
        stop.setName(expResult);
        assertEquals(stop.getName(), expResult);
    }

    @Test
    public void testSetGetLatitude() {
        Stop stop = databaseController.getStopFactory().createStop("stop42");
        double expResult = 3.1415;
        stop.setLatitude(expResult);
        assertEquals(stop.getLatitude(), expResult, 1E-9);
    }

    @Test
    public void testSetGetLongitude() {
        Stop stop = databaseController.getStopFactory().createStop("stop42");
        double expResult = 3.1415;
        stop.setLongitude(expResult);
        assertEquals(stop.getLongitude(), expResult, 1E-9);
    }
}
