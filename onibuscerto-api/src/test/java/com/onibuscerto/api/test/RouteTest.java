package com.onibuscerto.api.test;

import com.onibuscerto.api.exceptions.DuplicateEntityException;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.graphdb.GraphDatabaseService;
import com.onibuscerto.api.DatabaseController;
import com.onibuscerto.api.entities.Route;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import com.onibuscerto.api.entities.Route.Type;
import com.onibuscerto.api.entities.Trip;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

public class RouteTest {

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
    public void testCreateRoute() {
        String routeId = "route42";
        Route route = databaseController.getRouteFactory().createRoute(routeId);
        assertEquals(routeId, route.getId());
    }

    @Test
    public void testCreateRouteDuplicate() {
        String routeId = "route42";
        Route route1 = databaseController.getRouteFactory().createRoute(routeId);
        exception.expect(DuplicateEntityException.class);
        Route route2 = databaseController.getRouteFactory().createRoute(routeId);
    }

    @Test
    public void testGetByIdNotFound() {
        String routeId = "route42";
        Route routeById = databaseController.getRouteFactory().getRouteById(routeId);
        assertEquals(null, routeById);
    }

    @Test
    public void testGetById() {
        String routeId = "route42";
        Route route = databaseController.getRouteFactory().createRoute(routeId);
        Route routeById = databaseController.getRouteFactory().getRouteById(routeId);
        assertEquals(route, routeById);
    }

    @Test
    public void testSetGetShortName() {
        Route route = databaseController.getRouteFactory().createRoute("route42");
        String expResult = "alsgj42n";
        route.setShortName(expResult);
        assertEquals(expResult, route.getShortName());
    }

    @Test
    public void testSetGetLongName() {
        Route route = databaseController.getRouteFactory().createRoute("route42");
        String expResult = "alsgj42n";
        route.setLongName(expResult);
        assertEquals(expResult, route.getLongName());
    }

    @Test
    public void testSetGetType() {
        Route route = databaseController.getRouteFactory().createRoute("route42");
        route.setType(Type.BUS);
        assertEquals(Type.BUS, route.getType());
    }

    @Test
    public void testGetTrips() {
        Trip trip1 = databaseController.getTripFactory().createTrip("trip42");
        Trip trip2 = databaseController.getTripFactory().createTrip("trip43");
        Route route = databaseController.getRouteFactory().createRoute("route42");
        trip1.setRoute(route);
        trip2.setRoute(route);
        Collection<Trip> result = route.getTrips();
        assertEquals(2, result.size());
        assertThat(result, hasItems(trip1, trip2));
    }
}
