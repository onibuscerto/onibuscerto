package com.onibuscerto.api.test;

import com.onibuscerto.api.exceptions.DuplicateEntityException;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.graphdb.GraphDatabaseService;
import com.onibuscerto.api.DatabaseController;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import com.onibuscerto.api.entities.FareAttribute;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class FareAttributeTest {

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
    public void testCreateFareAttribute() {
        String fareId = "fare42";
        FareAttribute fareAttribute = databaseController.getFareAttributeFactory().createFareAttribute(fareId);
        assertEquals(fareId, fareAttribute.getFareId());
    }

    @Test
    public void testCreateCalendarDuplicate() {
        String fareId = "fare42";
        FareAttribute fare1 = databaseController.getFareAttributeFactory().createFareAttribute(fareId);
        exception.expect(DuplicateEntityException.class);
        FareAttribute fare2 = databaseController.getFareAttributeFactory().createFareAttribute(fareId);
    }

    @Test
    public void testSetGetPrice() {
        double price = 2.00;
        FareAttribute fareAttribute = databaseController.getFareAttributeFactory().createFareAttribute("fare42");
        fareAttribute.setPrice(price);
        assertEquals(price, fareAttribute.getPrice(), 0);
    }

    @Test
    public void testSetGetCurrencyType() {
        FareAttribute fareAttribute = databaseController.getFareAttributeFactory().createFareAttribute("fare42");
        fareAttribute.setCurrencyType("USD");
        assertEquals("USD", fareAttribute.getCurrencyType());
    }

    @Test
    public void testSetGetPaymentMethod() {
        FareAttribute fareAttribute = databaseController.getFareAttributeFactory().createFareAttribute("fare42");
        fareAttribute.setPaymentMethod(0);
        assertEquals(0, fareAttribute.getPaymentMethod());
    }

    @Test
    public void testSetGetTransfers() {
        FareAttribute fareAttribute = databaseController.getFareAttributeFactory().createFareAttribute("fare42");
        fareAttribute.setTransfers(0);
        assertEquals(0, fareAttribute.getTransfers());
    }

    @Test
    public void testSetGetTransferDuration() {
        FareAttribute fareAttribute = databaseController.getFareAttributeFactory().createFareAttribute("fare42");
        fareAttribute.setTransferDuration(0);
        assertEquals(0, fareAttribute.getTransferDuration());
    }
}
