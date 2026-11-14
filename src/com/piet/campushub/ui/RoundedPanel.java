package com.piet.campushub.ui;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * A panel with rounded corners. When it has a hover colour, it lights up and lifts
 * slightly while the mouse is anywhere over it (including over its children).
 */
public class RoundedPanel extends JPanel {

    private Color fill;
    private final Color border;
    private final int arc;
    private Color hoverColor;
    private boolean hover;

    private static RoundedPanel hovered;
    private static boolean listening;

    public RoundedPanel(LayoutManager layout, Color fill, Color border, int arc) {
        super(layout);
        this.fill = fill;
        this.border = border;
        this.arc = arc;
        setOpaque(false);
        installHoverTracking();
    }

    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    /** Turns on the hover light-up in the given colour. */
    public RoundedPanel hoverable(Color color) {
        this.hoverColor = color;
        return this;
    }

    private void setHover(boolean h) {
        if (hover != h) {
            hover = h;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean lit = hover && hoverColor != null;
        if (lit) {
            g2.setColor(Theme.withAlpha(Theme.SAP2, Theme.dark ? 90 : 35));
            g2.fillRoundRect(2, 5, getWidth() - 4, getHeight() - 5, arc, arc);
        }
        int lift = lit ? 2 : 0;
        if (fill != null) {
            g2.setColor(lit ? Theme.mix(Theme.accent(), fill, 0.04) : fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1 - lift, arc, arc);
        }
        Color b = lit ? Theme.mix(hoverColor, Theme.line(), 0.55) : border;
        if (b != null) {
            g2.setColor(b);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1 - lift, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    /** One listener for the whole app finds which hoverable panel the mouse is over. */
    private static void installHoverTracking() {
        if (listening) return;
        listening = true;
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (!(e instanceof MouseEvent me)) return;
            int id = me.getID();
            if (id != MouseEvent.MOUSE_MOVED && id != MouseEvent.MOUSE_ENTERED && id != MouseEvent.MOUSE_EXITED
                    && id != MouseEvent.MOUSE_DRAGGED) return;
            RoundedPanel target = null;
            Component c = me.getComponent();
            if (id != MouseEvent.MOUSE_EXITED || c instanceof Window) {
                Component at = c;
                if (c != null && c.isShowing()) {
                    Window w = SwingUtilities.getWindowAncestor(c);
                    if (w != null) {
                        Point p = SwingUtilities.convertPoint(c, me.getPoint(), w);
                        at = SwingUtilities.getDeepestComponentAt(w, p.x, p.y);
                    }
                }
                for (Component x = at; x != null; x = x.getParent()) {
                    if (x instanceof RoundedPanel rp && rp.hoverColor != null) {
                        target = rp;
                        break;
                    }
                }
            }
            if (target != hovered) {
                if (hovered != null) hovered.setHover(false);
                hovered = target;
                if (hovered != null) hovered.setHover(true);
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
}
