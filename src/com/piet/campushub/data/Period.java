package com.piet.campushub.data;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** One timetable slot. A lunch break has no subject. */
public record Period(LocalTime start, LocalTime end, Subject subject) {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm");

    public boolean isLunch() {
        return subject == null;
    }

    public String label() {
        return TIME.format(start) + " - " + TIME.format(end);
    }

    public boolean isRunningAt(LocalTime time) {
        return !time.isBefore(start) && time.isBefore(end);
    }
}
