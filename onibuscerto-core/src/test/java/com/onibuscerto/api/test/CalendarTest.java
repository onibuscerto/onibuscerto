package com.onibuscerto.api.test;

import com.onibuscerto.core.exceptions.DuplicateEntityException;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.graphdb.GraphDatabaseService;
import com.onibuscerto.core.DatabaseController;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import com.onibuscerto.core.entities.Calendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalendarTest {

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
    public void testCreateCalendar() {
        String serviceId = "service42";
        Calendar calendar = databaseController.getCalendarFactory().createCalendar(serviceId);
        assertEquals(serviceId, calendar.getServiceId());
    }

    @Test
    public void testCreateCalendarDuplicate() {
        String calendarId = "calendar42";
        Calendar calendar1 = databaseController.getCalendarFactory().createCalendar(calendarId);
        exception.expect(DuplicateEntityException.class);
        Calendar calendar2 = databaseController.getCalendarFactory().createCalendar(calendarId);
    }

    @Test
    public void testSetGetDaysOfWork() {
        Calendar calendar = databaseController.getCalendarFactory().createCalendar("service42");
        calendar.setDaysOfWork("monday", "1");
        assertEquals("1", calendar.getDaysOfWork("monday"));
    }

    @Test
    public void testSetGetStartDate() {
        Calendar calendar = databaseController.getCalendarFactory().createCalendar("service42");
        calendar.setStartDate("20070101");
        assertEquals("20070101", calendar.getStartDate());
    }

    @Test
    public void testSetGetEndDate() {
        Calendar calendar = databaseController.getCalendarFactory().createCalendar("service42");
        calendar.setEndDate("20070101");
        assertEquals("20070101", calendar.getEndDate());
    }
}
