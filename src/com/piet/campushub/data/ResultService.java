package com.piet.campushub.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Sample semester results on a 10-point grade scale. */
public final class ResultService {

    private ResultService() {
    }

    private static final Map<String, Integer> GRADE_POINTS = Map.of(
            "O", 10, "A+", 9, "A", 8, "B+", 7, "B", 6, "C", 5, "F", 0);

    private static final Map<String, List<ResultRow>> SEMESTERS = new LinkedHashMap<>();

    static {
        SEMESTERS.put("I", List.of(
                row("Engineering Mathematics-I", 4, "A"),
                row("Engineering Physics", 4, "B+"),
                row("Programming for Problem Solving", 3, "A+"),
                row("Basic Electrical Engineering", 3, "B+"),
                row("Communication Skills", 2, "A"),
                row("Engineering Graphics", 2, "A")));
        SEMESTERS.put("II", List.of(
                row("Engineering Mathematics-II", 4, "A"),
                row("Engineering Chemistry", 4, "B+"),
                row("Object Oriented Concepts", 3, "A+"),
                row("Basic Electronics", 3, "A"),
                row("Environmental Studies", 2, "A+"),
                row("Workshop Practice", 2, "O")));
    }

    private static ResultRow row(String subject, int credits, String grade) {
        return new ResultRow(subject, credits, grade, GRADE_POINTS.get(grade));
    }

    public static boolean isDeclared(String sem) {
        return SEMESTERS.containsKey(sem);
    }

    public static List<String> declaredSemesters() {
        return List.copyOf(SEMESTERS.keySet());
    }

    public static List<ResultRow> rows(String sem) {
        return SEMESTERS.getOrDefault(sem, List.of());
    }

    public static int credits(String sem) {
        return rows(sem).stream().mapToInt(ResultRow::credits).sum();
    }

    public static double sgpa(String sem) {
        int c = 0, p = 0;
        for (ResultRow r : rows(sem)) {
            c += r.credits();
            p += r.credits() * r.points();
        }
        return c == 0 ? 0 : (double) p / c;
    }

    public static double cgpa() {
        int c = 0, p = 0;
        for (List<ResultRow> list : SEMESTERS.values()) {
            for (ResultRow r : list) {
                c += r.credits();
                p += r.credits() * r.points();
            }
        }
        return c == 0 ? 0 : (double) p / c;
    }

    public static boolean passed(String sem) {
        return rows(sem).stream().noneMatch(r -> "F".equals(r.grade()));
    }
}
