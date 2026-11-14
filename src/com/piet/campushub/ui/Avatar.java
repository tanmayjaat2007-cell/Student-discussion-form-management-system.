package com.piet.campushub.ui;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/** The student's round profile photo with a coloured ring. Glows when the mouse is over it. */
public class Avatar extends JComponent {

    private final int size;
    private final Color ring;
    private boolean hover;

    public Avatar(int size) {
        this(size, Theme.accent());
    }

    public Avatar(int size, Color ring) {
        this.size = size;
        this.ring = ring;
        setPreferredSize(new Dimension(size + 8, size + 8));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
        if (hover && getMouseListeners().length > 1) {
            g2.setColor(Theme.withAlpha(ring, 90));
            g2.fillOval(x - 4, y - 4, size + 8, size + 8);
        }
        g2.setColor(ring);
        g2.fillOval(x - 2, y - 2, size + 4, size + 4);
        BufferedImage photo = CampusImage.profile();
        if (photo != null) {
            Shape old = g2.getClip();
            g2.clip(new Ellipse2D.Float(x, y, size, size));
            int side = Math.min(photo.getWidth(), photo.getHeight());
            int sx = (photo.getWidth() - side) / 2, sy = (int) ((photo.getHeight() - side) * 0.4);
            g2.drawImage(photo, x, y, x + size, y + size, sx, sy, sx + side, sy + side, null);
            g2.setClip(old);
        } else {
            g2.setColor(Theme.accent());
            g2.fillOval(x, y, size, size);
            g2.setColor(Theme.accentInk());
            g2.setFont(new Font("SansSerif", Font.BOLD, size / 3));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("RS", x + (size - fm.stringWidth("RS")) / 2, y + (size + fm.getAscent() - fm.getDescent()) / 2);
        }
        g2.dispose();
    }
}
