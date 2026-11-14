package com.piet.campushub.ui;

import com.piet.campushub.data.CollegeData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** The side menu with the campus photo, the student's name, the pages and Log out. */
public class Sidebar extends JPanel {

    public Sidebar(AppFrame app, String active) {
        super(new BorderLayout());
        setBackground(Theme.surface());
        setPreferredSize(new Dimension(240, 100));
        setBorder(new MatteBorder(0, 0, 0, 1, Theme.line()));

        JPanel head = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new LinearGradientPaint(0, 0, getWidth(), getHeight(), new float[]{0f, .55f, 1f},
                        new Color[]{Theme.SAP1, Theme.SAP2, Theme.SAP3}));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(143, 163, 255, 45));
                g2.fillOval(-40, getHeight() - 60, 130, 130);
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(getWidth() - 90, -40, 140, 140);
                g2.fillOval(-30, getHeight() - 50, 90, 90);
                g2.dispose();
            }
        };
        head.setPreferredSize(new Dimension(240, 150));
        head.setBorder(new EmptyBorder(0, 16, 16, 16));
        JPanel who = new JPanel(new BorderLayout(12, 0));
        who.setOpaque(false);
        Avatar photo = new Avatar(58, Color.WHITE);
        photo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        photo.setToolTipText("Click for Settings, double-click for Home");
        photo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { app.showPage(e.getClickCount() >= 2 ? "home" : "settings"); }
        });
        JPanel photoBox = new JPanel(new GridBagLayout());
        photoBox.setOpaque(false);
        photoBox.add(photo);
        who.add(photoBox, BorderLayout.WEST);
        JPanel names = UI.column();
        names.add(UI.label(CollegeData.USERNAME, Theme.H3, new Color(0xeeece6)));
        names.add(UI.label("B.Tech CSE · Sem III", Theme.SMALL, new Color(0xe3e7f3)));
        who.add(names, BorderLayout.CENTER);
        head.add(who, BorderLayout.SOUTH);
        add(head, BorderLayout.NORTH);

        JPanel nav = UI.column();
        nav.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (Object[] m : AppFrame.MENU) {
            String key = (String) m[0];
            NavItem item = new NavItem((String) m[1], (Color) m[2], key.equals(active), () -> app.showPage(key));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            nav.add(item);
            nav.add(UI.gap(2));
        }
        add(nav, BorderLayout.CENTER);

        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        foot.setBorder(new CompoundLine());
        NavItem out = new NavItem("Log out", Theme.bad(), false, app::logout);
        out.setForeground(Theme.bad());
        foot.add(out, BorderLayout.CENTER);
        add(foot, BorderLayout.SOUTH);
    }

    /** Top line plus padding for the Log out area. */
    private static class CompoundLine extends javax.swing.border.CompoundBorder {
        CompoundLine() {
            super(new MatteBorder(1, 0, 0, 0, Theme.line()), new EmptyBorder(10, 10, 10, 10));
        }
    }

    /** One menu entry: a coloured dot and a label, highlighted when it is the open page. */
    static class NavItem extends JComponent {
        private final String text;
        private final Color color;
        private final boolean selected;
        private boolean hover;

        NavItem(String text, Color color, boolean selected, Runnable action) {
            this.text = text;
            this.color = color;
            this.selected = selected;
            setForeground(Theme.ink());
            setPreferredSize(new Dimension(210, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusable(true);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { action.run(); }
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (selected || hover) {
                g2.setColor(Theme.mix(color, Theme.surface(), selected ? 0.22 : 0.12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
            g2.setColor(color);
            g2.fillOval(14, getHeight() / 2 - 6, 12, 12);
            g2.setFont(selected ? Theme.BOLD : Theme.BODY);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, 38, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}
