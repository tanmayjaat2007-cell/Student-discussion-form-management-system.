package com.piet.campushub.ui;

import javax.swing.*;
import java.awt.*;

/** A small message that slides up at the bottom of the window and fades away. */
public final class Toast {

    private Toast() {
    }

    public static void show(Component anywhere, String message) {
        JRootPane root = SwingUtilities.getRootPane(anywhere);
        if (root == null) return;
        JLayeredPane layer = root.getLayeredPane();
        float[] alpha = {0f};
        int[] offset = {20};
        JComponent toast = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, Math.min(1, alpha[0]))));
                g2.setFont(Theme.BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int w = fm.stringWidth(message) + 40, h = 44;
                int x = (getWidth() - w) / 2, y = getHeight() - h - 30 + offset[0];
                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillRoundRect(x + 2, y + 4, w, h, 16, 16);
                g2.setPaint(new GradientPaint(x, y, Theme.SAP2, x + w, y, Theme.SAP3));
                g2.fillRoundRect(x, y, w, h, 16, 16);
                g2.setColor(Color.WHITE);
                g2.drawString(message, x + 20, y + (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        toast.setBounds(0, 0, layer.getWidth(), layer.getHeight());
        layer.add(toast, JLayeredPane.POPUP_LAYER);
        Anim.run(300, k -> { alpha[0] = (float) k; offset[0] = (int) (20 * (1 - k)); toast.repaint(); });
        Timer hide = new Timer(2300, e -> Anim.run(400, k -> {
            alpha[0] = (float) (1 - k);
            toast.repaint();
            if (k >= 1) {
                layer.remove(toast);
                layer.repaint();
            }
        }));
        hide.setRepeats(false);
        hide.start();
    }
}
