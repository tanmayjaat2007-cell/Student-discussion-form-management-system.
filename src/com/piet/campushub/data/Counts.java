package com.piet.campushub.data;

/** Attended and held classes. */
public record Counts(int attended, int held) {

    public static final Counts ZERO = new Counts(0, 0);

    public int missed() {
        return held - attended;
    }

    public double percent() {
        return held == 0 ? 0 : attended * 100.0 / held;
    }

    public Counts plus(boolean present) {
        return new Counts(attended + (present ? 1 : 0), held + 1);
    }
}
