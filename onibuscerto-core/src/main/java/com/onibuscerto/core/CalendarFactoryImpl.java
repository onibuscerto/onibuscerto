package com.onibuscerto.core;

import com.onibuscerto.core.entities.Calendar;
import com.onibuscerto.core.exceptions.DuplicateEntityException;
import com.onibuscerto.core.factories.CalendarFactory;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

public class CalendarFactoryImpl implements CalendarFactory {

    private final DatabaseController databaseController;
    private final GraphDatabaseService graphDb;
    private final Node calendarFactoryNode;
    private final Index<Node> calendarIndex;
    private static final String CALENDAR_INDEX = "calendar_index";

    CalendarFactoryImpl(DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.graphDb = this.databaseController.getGraphDatabaseService();

        Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
                Relationships.CALENDARS, Direction.OUTGOING);

        if (rel == null) {
            calendarFactoryNode = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo(
                    calendarFactoryNode, Relationships.CALENDARS);
        } else {
            calendarFactoryNode = rel.getEndNode();
        }

        calendarIndex = graphDb.index().forNodes(CALENDAR_INDEX);
    }

    @Override
    public Calendar createCalendar(String serviceId) {
        Transaction tx = graphDb.beginTx();
        try {
            if (getCalendarById(serviceId) != null) {
                throw new DuplicateEntityException();
            }

            Node node = graphDb.createNode();
            Calendar calendar = new CalendarImpl(node, serviceId);
            calendarIndex.add(node, CalendarImpl.KEY_ID, serviceId);
            calendarFactoryNode.createRelationshipTo(node, Relationships.CALENDAR);
            tx.success();
            return calendar;
        } finally {
            tx.finish();
        }
    }

    @Override
    public Calendar getCalendarById(String id) {
        Node node = calendarIndex.get(CalendarImpl.KEY_ID, id).getSingle();

        if (node == null) {
            return null;
        }

        return new CalendarImpl(node);
    }
}
