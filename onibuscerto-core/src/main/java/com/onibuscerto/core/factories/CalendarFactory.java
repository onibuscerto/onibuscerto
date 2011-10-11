package com.onibuscerto.core.factories;

import com.onibuscerto.core.entities.Calendar;

public interface CalendarFactory {

    public Calendar createCalendar(String serviceId);

    public Calendar getCalendarById(String id);
}
