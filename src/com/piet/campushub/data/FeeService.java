package com.piet.campushub.data;

import java.time.LocalDate;

/** Sample Semester III fee statement, in rupees. */
public final class FeeService {

    private FeeService() {
    }

    public static final LocalDate DUE_DATE = LocalDate.of(2026, 10, 31);

    public static final String[] HEADS = {"Tuition fee", "Development fee", "Examination fee", "Library and lab fee", "Student activity fee"};
    public static final int[] AMOUNTS = {40000, 3500, 2500, 1500, 1000};

    public static final LocalDate[] PAID_ON = {LocalDate.of(2026, 8, 5), LocalDate.of(2026, 9, 12)};
    public static final String[] PAID_NOTE = {"Instalment 1", "Instalment 2"};
    public static final int[] PAID_AMOUNT = {30000, 10000};

    public static int total() {
        int t = 0;
        for (int a : AMOUNTS) t += a;
        return t;
    }

    public static int paid() {
        int t = 0;
        for (int a : PAID_AMOUNT) t += a;
        return t;
    }

    public static int due() {
        return Math.max(total() - paid(), 0);
    }

    public static String rupees(int amount) {
        return "₹" + String.format("%,d", amount);
    }
}
