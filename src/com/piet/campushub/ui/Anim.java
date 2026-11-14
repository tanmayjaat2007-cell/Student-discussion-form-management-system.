package com.piet.campushub.ui;

import javax.swing.Timer;
import java.util.function.DoubleConsumer;

/** Runs a short ease-out animation from 0 to 1 and calls back on every frame. */
public final class Anim {

    private Anim() {
    }

    public static Timer run(int millis, DoubleConsumer frame) {
        return run(millis, 0, frame);
    }

    /** delay is how long to wait before starting, in milliseconds. */
    public static Timer run(int millis, int delay, DoubleConsumer frame) {
        long[] start = {0};
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            long now = System.currentTimeMillis();
            if (start[0] == 0) start[0] = now;
            double k = Math.min(1, (now - start[0]) / (double) millis);
            frame.accept(1 - Math.pow(1 - k, 3));
            if (k >= 1) t.stop();
        });
        t.setInitialDelay(delay);
        frame.accept(0);
        t.start();
        return t;
    }
}
