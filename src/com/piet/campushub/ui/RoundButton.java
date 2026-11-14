package com.piet.campushub.ui;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A flat button with rounded corners and a hover effect. */
public class RoundButton extends JButton {

    private final Color fill;
    private final Color border;
    private boolean hover;

    public RoundButton(String text, Color fill, Color fg, Color border) {
        super(text);
        this.fill = fill;
        this.border = border;
        setForeground(fg);
        setFont(Theme.BOLD);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(9, 16, 9, 16));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color f = fill;
        if (f != null && hover) f = Theme.mix(Color.WHITE, f, 0.12);
        if (f != null && getModel().isPressed()) f = Theme.mix(Color.BLACK, f, 0.15);
        if (f != null) {
            g2.setColor(f);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        }
        if (border != null) {
            g2.setColor(hover ? Theme.accent() : border);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
