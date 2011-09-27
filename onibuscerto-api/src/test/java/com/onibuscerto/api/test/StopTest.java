package com.onibuscerto.api.test;

import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.FareAttribute;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.exceptions.DuplicateEntityException;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;

public class StopTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
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
        Stop stop = databaseController.getLocationFactory().createStop(stopId);
        assertEquals(stopId, stop.getId());
    }

    @Test
    public void testCreateStopDuplicate() {
        String stopId = "stop42";
        Stop stop1 = databaseController.getLocationFactory().createStop(stopId);
        exception.expect(DuplicateEntityException.class);
        Stop stop2 = databaseController.getLocationFactory().createStop(stopId);
    }

    @Test
    public void testGetById() {
        String stopId = "stop42";
        Stop stop = databaseController.getLocationFactory().createStop(stopId);
        Stop stopById = databaseController.getLocationFactory().getStopById(stopId);
        assertEquals(stopById, stop);
    }

    @Test
    public void testGetAllStops() {
        String stopId = "stop-";
        int numStops = 15;

        for (int i = 1; i <= numStops; i++) {
            databaseController.getLocationFactory().createStop(stopId + i);
        }

        Collection<Stop> allStops = databaseController.getLocationFactory().getAllStops();
        assertEquals(allStops.size(), numStops);
    }

    @Test
    public void testGetByIdNotFound() {
        String stopId = "stop42";
        Stop stopById = databaseController.getLocationFactory().getStopById(stopId);
        assertEquals(stopById, null);
    }

    @Test
    public void testSetGetName() {
        Stop stop = databaseController.getLocationFactory().createStop("stop42");
        String expResult = "alsgj42n";
        stop.setName(expResult);
        assertEquals(stop.getName(), expResult);
    }

    @Test
    public void testSetGetLatitude() {
        Stop stop = databaseController.getLocationFactory().createStop("stop42");
        double expResult = 3.1415;
        stop.setLatitude(expResult);
        assertEquals(stop.getLatitude(), expResult, 1E-9);
    }

    @Test
    public void testSetGetLongitude() {
        Stop stop = databaseController.getLocationFactory().createStop("stop42");
        double expResult = 3.1415;
        stop.setLongitude(expResult);
        assertEquals(stop.getLongitude(), expResult, 1E-9);
    }

    @Test
    public void testSetGetFare() {
        FareAttribute fare = databaseController.getFareAttributeFactory().createFareAttribute("fare42");
        Stop stop = databaseController.getLocationFactory().createStop("stop42");
        stop.setFare(fare);
        assertEquals(fare, stop.getFare());
    }
}
