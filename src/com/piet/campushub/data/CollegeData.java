package com.piet.campushub.data;

import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fixed college data: sign-in details, subjects, timetable, holidays and notices.
 * Subjects, teachers and college hours are real. Marks, period order and some dates are samples.
 * Change the values here to use real data.
 */
public final class CollegeData {

    private CollegeData() {
    }

    public static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    public static final String USERNAME = "Riya Suthar";
    public static final String PASSWORD = "9216@riya";
    public static final String COLLEGE = "Poornima Institute of Engineering & Technology";
    public static final LocalDate SEM_START = LocalDate.of(2026, 8, 3);

    public static final List<Subject> SUBJECTS = List.of(
            new Subject("DSA", "Data Structures & Algorithms", "Vivek Saxena", new Color(0x6366f1), 18, 17, 9),
            new Subject("JAVA", "Java Programming", "Shruti Gupta", new Color(0xf97316), 17, 16, 9),
            new Subject("UIUX", "UI/UX Design", "Udbhav Ojha", new Color(0xec4899), 16, 18, 9),
            new Subject("TC", "Technical Communication", "Prince", new Color(0x14b8a6), 17, 18, 9),
            new Subject("DSLD", "Digital System & Logic Design", "Abhishek Dadhich", new Color(0xf59e0b), 13, 12, 7),
            new Subject("MATHS", "Mathematics", "Varsha", new Color(0x0ea5e9), 14, 15, 8));

    /** College hours 8:00 AM to 2:50 PM. Lunch is 12:00 to 12:50 PM. */
    private static final LocalTime[][] SLOTS = {
            {LocalTime.of(8, 0), LocalTime.of(9, 0)},
            {LocalTime.of(9, 0), LocalTime.of(10, 0)},
            {LocalTime.of(10, 0), LocalTime.of(11, 0)},
            {LocalTime.of(11, 0), LocalTime.of(12, 0)},
            {LocalTime.of(12, 50), LocalTime.of(13, 50)},
            {LocalTime.of(13, 50), LocalTime.of(14, 50)}
    };

    public static final Map<LocalDate, String> HOLIDAYS = Map.of(
            LocalDate.of(2026, 8, 15), "Independence Day",
            LocalDate.of(2026, 10, 2), "Gandhi Jayanti",
            LocalDate.of(2026, 10, 20), "Dussehra",
            LocalDate.of(2026, 11, 8), "Diwali");

    public static final Map<LocalDate, String> LEAVE_DAYS = Map.of(
            LocalDate.of(2026, 9, 14), "Medical leave",
            LocalDate.of(2026, 9, 15), "Medical leave",
            LocalDate.of(2026, 9, 26), "Event leave");

    public static final String[][] NOTICES = {
            {"15 Apr 2025", "Special seminar on Artificial Intelligence"},
            {"22 Apr 2025", "NSS plantation drive"},
            {"10 Mar 2025", "Blood donation camp"}
    };

    public static final List<CalendarEvent> COLLEGE_EVENTS = List.of(
            new CalendarEvent(-1, LocalDate.of(2026, 10, 20), "Dussehra (holiday)", CalendarEvent.Kind.HOLIDAY, true),
            new CalendarEvent(-2, LocalDate.of(2026, 10, 29), "Mid-term tests begin", CalendarEvent.Kind.ACADEMIC, false),
            new CalendarEvent(-3, LocalDate.of(2026, 10, 30), "Assignment 2 due", CalendarEvent.Kind.ACADEMIC, true),
            new CalendarEvent(-4, LocalDate.of(2026, 10, 31), "Fee balance due", CalendarEvent.Kind.ACADEMIC, true),
            new CalendarEvent(-5, LocalDate.of(2026, 11, 8), "Diwali (holiday)", CalendarEvent.Kind.HOLIDAY, true),
            new CalendarEvent(-6, LocalDate.of(2026, 11, 14), "Technical fest", CalendarEvent.Kind.ACADEMIC, true));

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    public static LocalTime now() {
        return LocalTime.now(ZONE);
    }

    /** Six one-hour periods a day, rotated so each subject comes once a day, with lunch after the fourth. */
    public static List<Period> timetable(DayOfWeek day) {
        List<Period> list = new ArrayList<>();
        if (day == DayOfWeek.SUNDAY) {
            return list;
        }
        int d = day.getValue();
        for (int k = 0; k < SLOTS.length; k++) {
            list.add(new Period(SLOTS[k][0], SLOTS[k][1], SUBJECTS.get((d - 1 + k) % SUBJECTS.size())));
            if (k == 3) {
                list.add(new Period(LocalTime.of(12, 0), LocalTime.of(12, 50), null));
            }
        }
        return list;
    }

    public static int classCount(DayOfWeek day) {
        return (int) timetable(day).stream().filter(p -> !p.isLunch()).count();
    }
}
