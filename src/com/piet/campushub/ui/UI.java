package com.piet.campushub.ui;

import com.piet.campushub.data.Verdict;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/** Small factory methods for labels, sections, buttons and fields in the app's style. */
public final class UI {

    private UI() {
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JLabel title(String text) { return label(text, Theme.TITLE, Theme.ink()); }
    public static JLabel h2(String text) { return label(text, Theme.H2, Theme.ink()); }
    public static JLabel h3(String text) { return label(text, Theme.H3, Theme.ink()); }
    public static JLabel text(String text) { return label(text, Theme.BODY, Theme.ink()); }
    public static JLabel bold(String text) { return label(text, Theme.BOLD, Theme.ink()); }
    public static JLabel muted(String text) { return label(text, Theme.SMALL, Theme.muted()); }
    public static JLabel mono(String text) { return label(text, Theme.MONO, Theme.muted()); }

    /** A wrapping paragraph. */
    public static JTextArea para(String text, Font font, Color color) {
        JTextArea t = new JTextArea(text);
        t.setFont(font);
        t.setForeground(color);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setEditable(false);
        t.setFocusable(false);
        t.setOpaque(false);
        t.setBorder(null);
        return t;
    }

    public static Color verdictColor(Verdict v) {
        return switch (v.level()) {
            case OK -> Theme.good();
            case EDGE -> Theme.warn();
            case LOW -> Theme.bad();
        };
    }

    /** A rounded section tinted with the given colour. Content goes in a vertical box. */
    public static RoundedPanel card(Color hue) {
        RoundedPanel p = new RoundedPanel(null, Theme.tint(hue), Theme.edge(hue), 26).hoverable(hue);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(16, 18, 16, 18));
        return p;
    }

    /** Adds a component to a vertical card, aligned left and stretched across. */
    public static void addTo(JComponent box, JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height + (c instanceof JTextArea ? 400 : 0)));
        box.add(c);
    }

    public static Component gap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    /** A transparent row with something on the left and something on the right. */
    public static JPanel row(JComponent left, JComponent right) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        if (left != null) p.add(left, BorderLayout.CENTER);
        if (right != null) p.add(right, BorderLayout.EAST);
        return p;
    }

    /** A transparent flow of components from left to right. */
    public static JPanel flow(int gap, JComponent... items) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        p.setOpaque(false);
        for (JComponent c : items) p.add(c);
        return p;
    }

    /** A transparent grid. */
    public static JPanel grid(int cols, int gap) {
        JPanel p = new JPanel(new GridLayout(0, cols, gap, gap));
        p.setOpaque(false);
        return p;
    }

    /** A transparent vertical box. */
    public static JPanel column() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    /** A thin line between list rows. */
    public static JComponent divider(Color hue) {
        JPanel d = new JPanel();
        d.setBackground(Theme.mix(hue, Theme.line(), 0.25));
        d.setPreferredSize(new Dimension(10, 1));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        return d;
    }

    /** A small coloured square used before subject names. */
    public static JComponent dot(Color c) {
        JComponent d = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillRoundRect(0, (getHeight() - 10) / 2, 10, 10, 4, 4);
                g2.dispose();
            }
        };
        d.setPreferredSize(new Dimension(12, 14));
        return d;
    }

    /** A rounded outlined tag, such as Present or Absent. */
    public static JLabel pill(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(Theme.MONO);
        l.setForeground(color);
        l.setBorder(new EmptyBorder(2, 9, 2, 9));
        return l;
    }

    public static RoundButton primary(String text, Runnable action) {
        RoundButton b = new RoundButton(text, Theme.accent(), Theme.accentInk(), null);
        b.addActionListener(e -> action.run());
        return b;
    }

    public static RoundButton ghost(String text, Runnable action) {
        RoundButton b = new RoundButton(text, Theme.surface(), Theme.ink(), Theme.line());
        b.addActionListener(e -> action.run());
        return b;
    }

    public static RoundButton danger(String text, Runnable action) {
        RoundButton b = new RoundButton(text, null, Theme.bad(), Theme.bad());
        b.addActionListener(e -> action.run());
        return b;
    }

    /** A pill-shaped button that looks selected or not. */
    public static RoundButton chip(String text, boolean selected, Runnable action) {
        RoundButton b = selected
                ? new RoundButton(text, Theme.accent(), Theme.accentInk(), null)
                : new RoundButton(text, Theme.surface(), Theme.ink(), Theme.line());
        b.setBorder(new EmptyBorder(7, 16, 7, 16));
        b.addActionListener(e -> action.run());
        return b;
    }

    /** Styles a text field or similar input. */
    public static <T extends JComponent> T field(T c) {
        c.setFont(Theme.BODY);
        c.setForeground(Theme.ink());
        c.setBackground(Theme.bg());
        c.setBorder(new CompoundBorder(new LineBorder(Theme.line(), 1, true), new EmptyBorder(8, 10, 8, 10)));
        if (c instanceof JTextField tf) tf.setCaretColor(Theme.ink());
        if (c instanceof JTextArea ta) ta.setCaretColor(Theme.ink());
        return c;
    }

    /** Wraps a page in a dark scroll pane. */
    public static JScrollPane scroll(JComponent page) {
        JScrollPane sp = new JScrollPane(page);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.bg());
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    /** Shows a short message at the bottom of the window. */
    public static void message(Component parent, String title, String text) {
        Toast.show(parent, text);
    }

    /** A label whose number counts up from zero when it appears, for example "81.4%" or "₹8,500". */
    public static JLabel count(String text, Font font, Color color) {
        JLabel l = new JLabel(text) {
            @Override
            public void addNotify() {
                super.addNotify();
                if (true) return; // numbers show straight away, no counting
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("^(\\D*?)([\\d,]+(?:\\.\\d+)?)(.*)$").matcher(text);
                if (!m.matches()) return;
                String raw = m.group(2).replace(",", "");
                double to = Double.parseDouble(raw);
                int dec = raw.contains(".") ? raw.length() - raw.indexOf('.') - 1 : 0;
                boolean comma = m.group(2).contains(",");
                Anim.run(900, 100, k -> {
                    double v = to * k;
                    String num = comma ? String.format("%,." + dec + "f", v) : String.format("%." + dec + "f", v);
                    setText(k >= 1 ? text : m.group(1) + num + m.group(3));
                });
            }
        };
        l.setFont(font);
        l.setForeground(color);
        return l;
    }
}
