package com.piet.campushub.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data for the signed-in student while the app is open: attendance target,
 * leave requests and the student's own calendar events. Kept in memory only.
 */
public final class Session {

    private Session() {
    }

    public static int target = 75;
    public static final List<LeaveRequest> LEAVES = new ArrayList<>();
    public static final List<CalendarEvent> EVENTS = new ArrayList<>();
    private static long nextId = 100;

    static {
        resetSampleData();
    }

    public static long newId() {
        return nextId++;
    }

    public static void resetSampleData() {
        LEAVES.clear();
        EVENTS.clear();
        LEAVES.add(new LeaveRequest(1, "Medical", LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 15),
                "Fever and rest advised", "Approved", true));
        LEAVES.add(new LeaveRequest(2, "Event / Sports", LocalDate.of(2026, 9, 26), LocalDate.of(2026, 9, 26),
                "Inter-college event", "Approved", true));
    }
}
