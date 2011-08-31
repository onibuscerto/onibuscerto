package com.onibuscerto.api.test;

import com.onibuscerto.api.entities.Calendar;
import com.onibuscerto.api.exceptions.DuplicateEntityException;
import com.onibuscerto.api.entities.StopTime;
import com.onibuscerto.api.entities.Route;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.graphdb.GraphDatabaseService;
import com.onibuscerto.api.DatabaseController;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import com.onibuscerto.api.entities.Trip;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

public class TripTest {

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
    public void testCreateTrip() {
        String tripId = "trip42";
        Trip trip = databaseController.getTripFactory().createTrip(tripId);
        assertEquals(trip.getId(), tripId);
    }

    @Test
    public void testCreateTripDuplicate() {
        String tripId = "trip42";
        Trip trip1 = databaseController.getTripFactory().createTrip(tripId);
        exception.expect(DuplicateEntityException.class);
        Trip trip2 = databaseController.getTripFactory().createTrip(tripId);
    }

    @Test
    public void testGetByIdNotFound() {
        String tripId = "trip42";
        Trip tripById = databaseController.getTripFactory().getTripById(tripId);
        assertEquals(null, tripById);
    }

    @Test
    public void testGetById() {
        String tripId = "trip42";
        Trip trip = databaseController.getTripFactory().createTrip(tripId);
        Trip tripById = databaseController.getTripFactory().getTripById(tripId);
        assertEquals(trip, tripById);
    }

    @Test
    public void testSetGetRoute() {
        String routeId = "route42";
        Route route = databaseController.getRouteFactory().createRoute(routeId);
        Trip trip = databaseController.getTripFactory().createTrip("trip42");
        trip.setRoute(route);
        assertEquals(route, trip.getRoute());
    }

    @Test
    public void testSetGetCalendar() {
        String calendarId = "calendar42";
        Calendar calendar = databaseController.getCalendarFactory().createCalendar(calendarId);
        Trip trip = databaseController.getTripFactory().createTrip("trip42");
        trip.setCalendar(calendar);
        assertEquals(calendar, trip.getCalendar());
    }

    @Test
    public void testGetStopTimes() {
        Trip trip = databaseController.getTripFactory().createTrip("trip42");
        StopTime st1 = databaseController.getStopTimeFactory().createStopTime();
        StopTime st2 = databaseController.getStopTimeFactory().createStopTime();
        st1.setTrip(trip);
        st2.setTrip(trip);
        Collection<StopTime> result = trip.getStopTimes();
        assertEquals(2, result.size());
        assertThat(result, hasItems(st1, st2));
    }
}
