package com.piet.campushub.data;

/** What happened on a date. */
public record DayStatus(Type type, String name, AttendanceDay day) {

    public enum Type { BEFORE_SEMESTER, OFF, NOT_YET, CLASSES }

    public static DayStatus beforeSemester() { return new DayStatus(Type.BEFORE_SEMESTER, null, null); }
    public static DayStatus off(String name) { return new DayStatus(Type.OFF, name, null); }
    public static DayStatus notYet() { return new DayStatus(Type.NOT_YET, null, null); }
    public static DayStatus classes(AttendanceDay day) { return new DayStatus(Type.CLASSES, null, day); }
}
