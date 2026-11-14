package com.piet.campushub.ui;

import javax.swing.JComponent;
import java.awt.*;

/** A thin rounded progress bar that grows when it first appears. */
public class ProgressBar extends JComponent {

    private final double percent;
    private final Color from;
    private final Color to;
    private double shown;

    public ProgressBar(double percent, Color from, Color to) {
        this.percent = percent;
        this.from = from;
        this.to = to;
        setPreferredSize(new Dimension(80, 10));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        shown = percent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int h = 8, y = (getHeight() - h) / 2;
        g2.setColor(Theme.line());
        g2.fillRoundRect(0, y, getWidth(), h, h, h);
        int w = (int) Math.round(getWidth() * Math.min(shown, 100) / 100);
        if (w > 0) {
            g2.setPaint(new GradientPaint(0, 0, from, Math.max(w, 1), 0, to));
            g2.fillRoundRect(0, y, w, h, h, h);
        }
        g2.dispose();
    }
}
