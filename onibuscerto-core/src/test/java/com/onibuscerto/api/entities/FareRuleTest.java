/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.onibuscerto.api.entities;

import com.onibuscerto.core.entities.FareAttribute;
import com.onibuscerto.core.entities.Stop;
import com.onibuscerto.core.entities.FareRule;
import com.onibuscerto.core.exceptions.DuplicateEntityException;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.graphdb.GraphDatabaseService;
import com.onibuscerto.core.DatabaseController;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bruno
 */
public class FareRuleTest {

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
    public void testCreateFareRule() {
        String fareRuleId = "fare42";
        FareRule fare = databaseController.getFareRuleFactory().createFareRule(fareRuleId);
        assertEquals(fareRuleId, fare.getId());
    }

    @Test
    public void testCreateFareRuleDuplicate() {
        String fareId = "fare42";
        FareRule fare1 = databaseController.getFareRuleFactory().createFareRule(fareId);
        exception.expect(DuplicateEntityException.class);
        FareRule fare2 = databaseController.getFareRuleFactory().createFareRule(fareId);
    }

    @Test
    public void testGetByIdNotFound() {
        String fareId = "fare42";
        FareRule fareById = databaseController.getFareRuleFactory().getFareRuleById(fareId);
        assertEquals(null, fareById);
    }

    @Test
    public void testGetById() {
        String fareId = "fare42";
        FareRule fare = databaseController.getFareRuleFactory().createFareRule(fareId);
        FareRule fareById = databaseController.getFareRuleFactory().getFareRuleById(fareId);
        assertEquals(fare, fareById);
    }

    @Test
    public void testSetGetFareAttribute() {
        String fareId = "fare42";
        FareAttribute fare = databaseController.getFareAttributeFactory().createFareAttribute(fareId);
        FareRule fareRule = databaseController.getFareRuleFactory().createFareRule("fareRule42");
        fareRule.setFareAttribute(fare);
        assertEquals(fare, fareRule.getFareAttribute());
    }

    @Test
    public void testSetGetSource() {
        String sourceId = "source42";
        Stop source = databaseController.getLocationFactory().createStop(sourceId);
        FareRule fareRule = databaseController.getFareRuleFactory().createFareRule("fareRule42");
        fareRule.setSource(source);
        assertEquals(source, fareRule.getSource());
    }

    @Test
    public void testSetGetTarget() {
        String targetId = "target42";
        Stop target = databaseController.getLocationFactory().createStop(targetId);
        FareRule fareRule = databaseController.getFareRuleFactory().createFareRule("fareRule42");
        fareRule.setTarget(target);
        assertEquals(target, fareRule.getTarget());
    }
}
