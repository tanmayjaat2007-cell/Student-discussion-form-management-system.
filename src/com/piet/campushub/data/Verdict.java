package com.piet.campushub.data;

/** Advice for an attendance figure. */
public record Verdict(Level level, String text) {

    public enum Level { OK, EDGE, LOW }
}
