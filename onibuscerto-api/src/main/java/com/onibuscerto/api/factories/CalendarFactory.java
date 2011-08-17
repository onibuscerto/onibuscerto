package com.onibuscerto.api.factories;

import com.onibuscerto.api.entities.Calendar;

public interface CalendarFactory {

    public Calendar createCalendar(String serviceId);

    public Calendar getStopById(String id);
}
