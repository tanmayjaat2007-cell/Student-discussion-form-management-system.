package com.piet.campushub.ui;

import javax.swing.JComponent;
import java.awt.*;

/** A circular progress ring that fills up when it first appears. */
public class RingChart extends JComponent {

    private final double percent;
    private final Color color;
    private final int size;
    private double shown;

    public RingChart(double percent, Color color, int size) {
        this.percent = percent;
        this.color = color;
        this.size = size;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
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
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int stroke = Math.max(5, size / 10);
        int d = size - stroke - 2;
        int x = (getWidth() - d) / 2, y = (getHeight() - d) / 2;
        g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Theme.line());
        g2.drawOval(x, y, d, d);
        g2.setColor(color);
        g2.drawArc(x, y, d, d, 90, (int) Math.round(-360 * Math.min(shown, 100) / 100));
        String text = Math.round(shown) + "%";
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(11, size / 5)));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Theme.ink());
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}
