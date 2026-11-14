package com.piet.campushub.data;

import java.awt.Color;

/** A Semester III subject with its teacher, colour and internal marks. */
public record Subject(String code, String name, String faculty, Color color,
                      int test1, int test2, int assignment) {

    /** Internal total out of 50 (Test 1 /20 + Test 2 /20 + Assignment /10). */
    public int total() {
        return test1 + test2 + assignment;
    }

    public double percent() {
        return total() * 100.0 / 50;
    }
}
