package com.piet.campushub.data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Sample day-by-day attendance from the semester start up to yesterday, with totals. */
public final class AttendanceService {

    private AttendanceService() {
    }

    private static LocalDate cacheDay;
    private static List<AttendanceDay> cache;

    public static DayStatus status(LocalDate date) {
        if (date.isBefore(CollegeData.SEM_START)) return DayStatus.beforeSemester();
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) return DayStatus.off("Sunday");
        String holiday = CollegeData.HOLIDAYS.get(date);
        if (holiday != null) return DayStatus.off(holiday + " holiday");
        if (!date.isBefore(CollegeData.today())) return DayStatus.notYet();
        return DayStatus.classes(buildDay(date));
    }

    private static AttendanceDay buildDay(LocalDate date) {
        long n = ChronoUnit.DAYS.between(CollegeData.SEM_START, date);
        String leave = CollegeData.LEAVE_DAYS.get(date);
        List<PeriodMark> marks = new ArrayList<>();
        int k = 0;
        for (Period p : CollegeData.timetable(date.getDayOfWeek())) {
            if (p.isLunch()) continue;
            int si = CollegeData.SUBJECTS.indexOf(p.subject());
            boolean absent = leave != null
                    || (n * 5 + k * 7) % 9 == 0
                    || (si == 4 && (n + k) % 7 == 0)
                    || (si == 5 && (n + k) % 6 == 0);
            marks.add(new PeriodMark(p, !absent));
            k++;
        }
        return new AttendanceDay(date, leave, marks);
    }

    /** Every class day from the semester start up to yesterday. */
    public static List<AttendanceDay> allDays() {
        LocalDate today = CollegeData.today();
        if (cache == null || !today.equals(cacheDay)) {
            List<AttendanceDay> days = new ArrayList<>();
            for (LocalDate d = CollegeData.SEM_START; d.isBefore(today); d = d.plusDays(1)) {
                DayStatus s = status(d);
                if (s.type() == DayStatus.Type.CLASSES) days.add(s.day());
            }
            cache = days;
            cacheDay = today;
        }
        return cache;
    }

    public static Counts overall() {
        Counts c = Counts.ZERO;
        for (AttendanceDay day : allDays()) {
            for (PeriodMark m : day.marks()) c = c.plus(m.present());
        }
        return c;
    }

    public static Counts forSubject(int index) {
        Subject s = CollegeData.SUBJECTS.get(index);
        Counts c = Counts.ZERO;
        for (AttendanceDay day : allDays()) {
            for (PeriodMark m : day.marks()) {
                if (m.period().subject().equals(s)) c = c.plus(m.present());
            }
        }
        return c;
    }

    public static Map<YearMonth, Counts> byMonth() {
        Map<YearMonth, Counts> months = new LinkedHashMap<>();
        for (AttendanceDay day : allDays()) {
            YearMonth ym = YearMonth.from(day.date());
            for (PeriodMark m : day.marks()) {
                months.put(ym, months.getOrDefault(ym, Counts.ZERO).plus(m.present()));
            }
        }
        return months;
    }

    /** Present (true) or absent (false) for the last n classes of a subject, oldest first. */
    public static List<Boolean> lastClasses(int index, int n) {
        Subject s = CollegeData.SUBJECTS.get(index);
        List<AttendanceDay> days = allDays();
        LinkedList<Boolean> out = new LinkedList<>();
        for (int i = days.size() - 1; i >= 0 && out.size() < n; i--) {
            List<PeriodMark> marks = days.get(i).marks();
            for (int j = marks.size() - 1; j >= 0 && out.size() < n; j--) {
                if (marks.get(j).period().subject().equals(s)) out.addFirst(marks.get(j).present());
            }
        }
        return out;
    }

    public static LocalDate lastClassDay() {
        List<AttendanceDay> days = allDays();
        return days.isEmpty() ? CollegeData.today() : days.get(days.size() - 1).date();
    }

    /** How many classes can be missed, or must be attended in a row, to stay at the target. */
    public static Verdict verdict(Counts c, int target) {
        double f = target / 100.0;
        double p = c.held() == 0 ? 0 : (double) c.attended() / c.held();
        if (p >= f) {
            int skip = (int) Math.floor(c.attended() / f - c.held() + 1e-9);
            if (skip > 0) {
                return new Verdict(Verdict.Level.OK, "You can miss " + skip + " more class" + (skip > 1 ? "es" : ""));
            }
            return new Verdict(Verdict.Level.EDGE, "Right on target. Do not miss the next class");
        }
        int need = (int) Math.ceil((f * c.held() - c.attended()) / (1 - f) - 1e-9);
        return new Verdict(Verdict.Level.LOW, "Attend the next " + need + " class" + (need > 1 ? "es" : "")
                + " in a row to reach " + target + "%");
    }
}
