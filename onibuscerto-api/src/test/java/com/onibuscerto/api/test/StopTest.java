package com.onibuscerto.api.test;

import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Stop;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;

public class StopTest {

    private DatabaseController databaseController;

    @Before
    public void setUp() throws Exception {
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase();
        databaseController = new DatabaseController(graphDb);
        databaseController.beginTransaction();
    }

    @After
    public void tearDown() throws Exception {
        // Faz um rollback após o teste
        databaseController.endTransaction(false);
        databaseController.close();
    }

    @Test
    public void testSetGetId() {
        Stop stop = databaseController.getStopFactory().createStop();
        String expResult = "alsgj42n";
        stop.setId(expResult);
        assertEquals(stop.getId(), expResult);
    }

    @Test
    public void testSetGetName() {
        Stop stop = databaseController.getStopFactory().createStop();
        String expResult = "alsgj42n";
        stop.setName(expResult);
        assertEquals(stop.getName(), expResult);
    }

    @Test
    public void testSetGetLatitude() {
        Stop stop = databaseController.getStopFactory().createStop();
        double expResult = 3.1415;
        stop.setLatitude(expResult);
        assertEquals(stop.getLatitude(), expResult, 1E-9);
    }

    @Test
    public void testSetGetLongitude() {
        Stop stop = databaseController.getStopFactory().createStop();
        double expResult = 3.1415;
        stop.setLongitude(expResult);
        assertEquals(stop.getLongitude(), expResult, 1E-9);
    }
}
