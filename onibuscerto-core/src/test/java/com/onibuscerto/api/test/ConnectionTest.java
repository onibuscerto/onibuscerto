package com.onibuscerto.api.test;

import com.onibuscerto.core.DatabaseController;
import com.onibuscerto.core.entities.Connection;
import com.onibuscerto.core.entities.Stop;
import com.onibuscerto.core.entities.Trip;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;

public class ConnectionTest {

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
    public void testCreateTransportConnection() {
        Stop s1 = databaseController.getLocationFactory().createStop("stop1");
        Stop s2 = databaseController.getLocationFactory().createStop("stop2");
        Trip t = databaseController.getTripFactory().createTrip("trip1");
        Connection c = databaseController.getConnectionFactory().createTransportConnection(t, s1, s2);

        // Testa getIncomingConnections
        assertEquals(0, s1.getIncomingConnections().size());
        assertEquals(1, s2.getIncomingConnections().size());
        assertThat(s2.getIncomingConnections(), hasItem(c));

        // Testa getOutgoingConnections
        assertEquals(1, s1.getOutgoingConnections().size());
        assertEquals(0, s2.getOutgoingConnections().size());
        assertThat(s1.getOutgoingConnections(), hasItem(c));
    }

    @Test
    public void testCreateWalkingConnection() {
        Stop s1 = databaseController.getLocationFactory().createStop("stop1");
        Stop s2 = databaseController.getLocationFactory().createStop("stop2");
        Connection c = databaseController.getConnectionFactory().createWalkingConnection(s1, s2);

        // Testa getIncomingConnections
        assertEquals(0, s1.getIncomingConnections().size());
        assertEquals(1, s2.getIncomingConnections().size());
        assertThat(s2.getIncomingConnections(), hasItem(c));

        // Testa getOutgoingConnections
        assertEquals(1, s1.getOutgoingConnections().size());
        assertEquals(0, s2.getOutgoingConnections().size());
        assertThat(s1.getOutgoingConnections(), hasItem(c));
    }
}
