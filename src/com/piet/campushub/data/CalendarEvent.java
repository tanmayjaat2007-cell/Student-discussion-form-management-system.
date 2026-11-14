package com.piet.campushub.data;

import java.time.LocalDate;

/** A calendar entry. Kind tells holidays, academic dates and the student's own events apart. */
public record CalendarEvent(long id, LocalDate date, String title, Kind kind, boolean sample) {

    public enum Kind { HOLIDAY, ACADEMIC, MINE }
}
