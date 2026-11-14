package com.piet.campushub.pages;

import com.piet.campushub.data.*;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Home: campus photo, next test countdown, quick actions, alerts and notices. */
public class HomePage extends Page {

    private Timer timer;

    public HomePage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        section(hero());
        CalendarEvent next = CollegeData.COLLEGE_EVENTS.stream()
                .filter(e -> !e.date().isBefore(CollegeData.today()))
                .filter(e -> e.title().toLowerCase().contains("test"))
                .findFirst().orElse(null);
        if (next != null) section(countdown(next));
        section(quickActions());
        section(UI.h3("Needs attention"));
        section(alerts());
        section(notices());
    }

    @Override
    public void dispose() {
        if (timer != null) timer.stop();
    }

    private JComponent hero() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                CampusImage.paintCover(g2, getWidth(), getHeight(), 26);
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setPreferredSize(new Dimension(600, 270));
        hero.setBorder(new EmptyBorder(0, 22, 20, 22));

        int hour = CollegeData.now().getHour();
        String greeting = hour < 12 ? "GOOD MORNING" : hour < 17 ? "GOOD AFTERNOON" : "GOOD EVENING";
        JPanel text = UI.column();
        text.add(UI.label(greeting + ", RIYA", Theme.MONO, new Color(0xc9cdff)));
        text.add(UI.gap(4));
        text.add(UI.label(CollegeData.COLLEGE, new Font("SansSerif", Font.BOLD, 24), Color.WHITE));
        text.add(UI.gap(4));
        text.add(UI.label("Sitapura, Jaipur · 8:00 AM to 2:50 PM", Theme.SMALL, new Color(0xe6e9f4)));
        text.add(UI.gap(12));
        Counts all = AttendanceService.overall();
        JPanel chips = UI.flow(8,
                chip(Math.round(all.percent()) + "% attendance", Theme.TEAL, "attendance"),
                chip("CGPA " + String.format("%.2f", ResultService.cgpa()), Theme.PINK, "results"),
                chip(CollegeData.classCount(CollegeData.today().getDayOfWeek()) + " classes today", Theme.SKY, "schedule"));
        ((FlowLayout) chips.getLayout()).setHgap(8);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Component c : text.getComponents()) ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(chips);
        hero.add(text, BorderLayout.SOUTH);
        return hero;
    }

    private JComponent chip(String label, Color color, String page) {
        RoundButton b = new RoundButton(label, new Color(255, 255, 255, 40), Color.WHITE, new Color(255, 255, 255, 90));
        b.setFont(Theme.SMALL.deriveFont(Font.BOLD));
        b.setBorder(new EmptyBorder(5, 12, 5, 12));
        b.addActionListener(e -> app.showPage(page));
        return b;
    }

    private JComponent countdown(CalendarEvent next) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new LinearGradientPaint(0, 0, getWidth(), getHeight(), new float[]{0f, .55f, 1f},
                        new Color[]{Theme.SAP3, Theme.SAP2, Theme.SAP1}));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 160, -80, 260, 200);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { app.showPage("calendar"); }
        });

        long days = ChronoUnit.DAYS.between(CollegeData.today(), next.date());
        JPanel left = UI.column();
        left.add(UI.label(next.title(), Theme.SMALL, new Color(255, 255, 255, 210)));
        left.add(UI.label(DateTimeFormatter.ofPattern("EEEE d MMM").format(next.date()), Theme.H2, Color.WHITE));
        JLabel tick = UI.label(" ", Theme.MONO, Color.WHITE);
        left.add(UI.gap(6));
        left.add(tick);

        JPanel right = UI.column();
        JLabel big = UI.count(days == 0 ? "Today" : String.valueOf(days), new Font("SansSerif", Font.BOLD, 44), Color.WHITE);
        big.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel sub = UI.label(days == 1 ? "day left" : "days left", Theme.SMALL, new Color(255, 255, 255, 210));
        sub.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(big);
        right.add(sub);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        LocalDateTime start = next.date().atTime(8, 0);
        Runnable update = () -> {
            Duration d = Duration.between(LocalDateTime.now(CollegeData.ZONE), start);
            if (d.isNegative()) {
                tick.setText("Starting now");
            } else {
                tick.setText(String.format("%dd %02dh %02dm %02ds", d.toDays(), d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart()));
            }
        };
        update.run();
        timer = new Timer(1000, e -> update.run());
        timer.start();
        return card;
    }

    private JComponent quickActions() {
        int due = FeeService.due();
        JPanel grid = UI.grid(2, 12);
        grid.add(tile("View attendance", Theme.TEAL, "attendance"));
        grid.add(tile("Apply leave", Theme.AMBER, "leave"));
        grid.add(tile(due > 0 ? "Pay " + FeeService.rupees(due) : "Fees paid", Theme.GREEN, "fees"));
        grid.add(tile("See results", Theme.PINK, "results"));
        return grid;
    }

    private JComponent tile(String label, Color color, String page) {
        RoundedPanel p = new RoundedPanel(new BorderLayout(12, 0), Theme.tint(color), Theme.edge(color), 22).hoverable(color);
        p.setBorder(new EmptyBorder(14, 14, 14, 14));
        JComponent icon = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.mix(color, Theme.surface(), 0.3));
                g2.fillRoundRect(0, 0, 40, 40, 12, 12);
                g2.setColor(color);
                g2.fillOval(13, 13, 14, 14);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(40, 40));
        p.add(icon, BorderLayout.WEST);
        p.add(UI.bold(label), BorderLayout.CENTER);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { app.showPage(page); }
        });
        return p;
    }

    private JComponent alerts() {
        int target = Session.target;
        List<int[]> low = new ArrayList<>();
        for (int i = 0; i < CollegeData.SUBJECTS.size(); i++) {
            Counts c = AttendanceService.forSubject(i);
            if (c.percent() < target) low.add(new int[]{i});
        }
        low.sort(Comparator.comparingDouble(a -> AttendanceService.forSubject(a[0]).percent()));

        JPanel box = UI.column();
        if (low.isEmpty()) {
            RoundedPanel ok = UI.card(Theme.GREEN);
            UI.addTo(ok, UI.text("Every subject is at or above your " + target + "% target."));
            ok.setAlignmentX(Component.LEFT_ALIGNMENT);
            box.add(ok);
        }
        for (int[] a : low) {
            Subject s = CollegeData.SUBJECTS.get(a[0]);
            Counts c = AttendanceService.forSubject(a[0]);
            RoundedPanel card = UI.card(Theme.RED);
            UI.addTo(card, UI.bold(s.name() + " attendance " + Math.round(c.percent()) + "%"));
            UI.addTo(card, UI.muted(AttendanceService.verdict(c, target).text() + "."));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { app.showPage("attendance"); }
            });
            box.add(card);
            box.add(UI.gap(10));
        }
        return box;
    }

    private JComponent notices() {
        RoundedPanel card = UI.card(Theme.SKY);
        UI.addTo(card, UI.row(UI.h3("Notices"), UI.mono("FROM PIET.POORNIMA.ORG")));
        card.add(UI.gap(8));
        for (String[] n : CollegeData.NOTICES) {
            JLabel date = UI.mono(n[0]);
            date.setPreferredSize(new Dimension(110, 20));
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(8, 0, 8, 0));
            row.add(date, BorderLayout.WEST);
            row.add(UI.text(n[1]), BorderLayout.CENTER);
            UI.addTo(card, UI.divider(Theme.SKY));
            UI.addTo(card, row);
        }
        card.add(UI.gap(6));
        UI.addTo(card, UI.muted("Latest items from the college website. See piet.poornima.org for current notices."));
        return card;
    }
}
