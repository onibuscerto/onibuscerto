package com.onibuscerto.api.test;

import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.graphdb.GraphDatabaseService;
import com.onibuscerto.api.DatabaseController;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import com.onibuscerto.api.entities.Stop;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Trip;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class StopTimeTest {

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
    public void testCreateStopTime() {
        String tripId = "trip42";
        Trip trip = databaseController.getTripFactory().createTrip(tripId);
        assertEquals(tripId, trip.getId());
    }

    @Test
    public void testSetGetTrip() {
        StopTime st = databaseController.getStopTimeFactory().createStopTime();
        Trip trip = databaseController.getTripFactory().createTrip("trip42");
        st.setTrip(trip);
        assertEquals(trip, st.getTrip());
    }

    @Test
    public void testSetGetStop() {
        StopTime st = databaseController.getStopTimeFactory().createStopTime();
        Stop stop = databaseController.getStopFactory().createStop("stop42");
        st.setStop(stop);
        assertEquals(stop, st.getStop());
    }

    @Test
    public void testSetGetArrivalTime() {
        StopTime st = databaseController.getStopTimeFactory().createStopTime();
        st.setArrivalTime(10);
        assertEquals(10, st.getArrivalTime());
    }

    @Test
    public void testSetGetDepartureTime() {
        StopTime st = databaseController.getStopTimeFactory().createStopTime();
        st.setDepartureTime(10);
        assertEquals(10, st.getDepartureTime());
    }

    @Test
    public void testSetGetSequence() {
        StopTime st = databaseController.getStopTimeFactory().createStopTime();
        st.setSequence(1);
        assertEquals(1, st.getSequence());
    }

    @Test
    public void testSetGetNext() {
        StopTime st1 = databaseController.getStopTimeFactory().createStopTime();
        StopTime st2 = databaseController.getStopTimeFactory().createStopTime();
        st1.setNext(st2);
        assertEquals(st2, st1.getNext());
        assertEquals(true, st1.hasNext());
        assertEquals(false, st2.hasNext());
    }

    @Test
    public void testSetGetPrevious() {
        StopTime st1 = databaseController.getStopTimeFactory().createStopTime();
        StopTime st2 = databaseController.getStopTimeFactory().createStopTime();
        st2.setPrevious(st1);
        assertEquals(st1, st2.getPrevious());
        assertEquals(true, st2.hasPrevious());
        assertEquals(false, st1.hasPrevious());
    }
}
