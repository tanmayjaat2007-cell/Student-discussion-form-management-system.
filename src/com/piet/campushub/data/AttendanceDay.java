package com.piet.campushub.data;

import java.time.LocalDate;
import java.util.List;

/** Attendance for one college day. leaveNote is set when the student was on leave. */
public record AttendanceDay(LocalDate date, String leaveNote, List<PeriodMark> marks) {

    public int attended() {
        return (int) marks.stream().filter(PeriodMark::present).count();
    }

    public int held() {
        return marks.size();
    }
}
