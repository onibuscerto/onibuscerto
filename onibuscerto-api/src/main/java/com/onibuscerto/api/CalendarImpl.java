/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.onibuscerto.api;

import com.onibuscerto.api.entities.Calendar;
import java.util.Collection;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Bruno
 */
public class CalendarImpl implements Calendar {

    private final Node underlyingNode;
    static final String KEY_ID = "calendar_service_id";
    private static final String KEY_DAYS_OF_WORK = "calendar_days_of_work";
    private static final String KEY_START_DATE = "calendar_start_date";
    private static final String KEY_END_DATE = "calendar_end_date";

    CalendarImpl(Node underlyingNode, String id) {
        this(underlyingNode);
        setServiceId(id);
    }

    CalendarImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    @Override
    public String getServiceId() {
        return (String) underlyingNode.getProperty(KEY_ID);
    }

    @Override
    public void setServiceId(String id) {
        underlyingNode.setProperty(KEY_ID, id);
    }

    @Override
    public String getDaysOfWork(String day) {
        return (String) underlyingNode.getProperty(day);
    }

    @Override
    public void setDaysOfWork(String day, String value){
        underlyingNode.setProperty(day, value);
    }

    @Override
    public String getStartDate() {
        return (String) underlyingNode.getProperty(KEY_START_DATE);
    }

    @Override
    public void setStartDate(String startDate) {
        underlyingNode.setProperty(KEY_START_DATE, startDate);
    }

    @Override
    public String getEndDate() {
        return (String) underlyingNode.getProperty(KEY_END_DATE);
    }

    @Override
    public void setEndDate(String endDate) {
        underlyingNode.setProperty(KEY_END_DATE, endDate);
    }
}
