package com.piet.campushub.pages;

import com.piet.campushub.data.*;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/** Attendance with three tabs: by date, by month and by subject. */
public class AttendancePage extends Page {

    private String tab = "date";
    private LocalDate selected = AttendanceService.lastClassDay();

    public AttendancePage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        int target = Session.target;
        Counts all = AttendanceService.overall();
        Verdict v = AttendanceService.verdict(all, target);

        section(UI.title("Attendance"));
        section(UI.muted("Semester III so far · target " + target + "%"));

        JPanel stats = UI.grid(3, 12);
        stats.add(stat(String.format("%.1f%%", all.percent()), "Overall", Theme.INDIGO));
        stats.add(stat(String.valueOf(all.attended()), "Attended", Theme.GREEN));
        stats.add(stat(String.valueOf(all.missed()), "Missed of " + all.held(), Theme.RED));
        section(stats);
        section(UI.label(v.text() + " overall.", Theme.BODY, UI.verdictColor(v)));

        section(UI.flow(8,
                UI.chip("Date", tab.equals("date"), () -> setTab("date")),
                UI.chip("Month", tab.equals("month"), () -> setTab("month")),
                UI.chip("Subject", tab.equals("subject"), () -> setTab("subject"))));

        switch (tab) {
            case "month" -> section(monthTab(target));
            case "subject" -> subjectTab(target);
            default -> section(dateTab());
        }
        section(UI.muted("Sample attendance records. Teachers update attendance after each class. Change your target in Settings."));
    }

    private void setTab(String t) {
        tab = t;
        rebuild();
    }

    private void go(LocalDate d) {
        LocalDate today = CollegeData.today();
        if (d.isBefore(CollegeData.SEM_START)) d = CollegeData.SEM_START;
        if (d.isAfter(today)) d = today;
        selected = d;
        rebuild();
    }

    private JComponent stat(String value, String label, Color color) {
        RoundedPanel p = UI.card(color);
        UI.addTo(p, UI.count(value, Theme.BIG, color));
        UI.addTo(p, UI.muted(label));
        return p;
    }

    private JComponent dateTab() {
        RoundedPanel card = UI.card(Theme.VIOLET);
        LocalDate today = CollegeData.today();

        JTextField field = UI.field(new JTextField(selected.toString(), 10));
        field.setToolTipText("Type a date as yyyy-mm-dd and press Enter");
        Runnable open = () -> {
            try {
                go(LocalDate.parse(field.getText().trim()));
            } catch (DateTimeParseException ex) {
                UI.message(this, "Date", "Type the date as yyyy-mm-dd, for example 2026-10-12.");
            }
        };
        field.addActionListener(e -> open.run());
        UI.addTo(card, UI.muted("Choose a date (yyyy-mm-dd)"));
        card.add(UI.gap(6));
        UI.addTo(card, UI.flow(8,
                UI.ghost("‹", () -> go(selected.minusDays(1))),
                field,
                UI.primary("Show", open),
                UI.ghost("›", () -> go(selected.plusDays(1))),
                UI.chip("Today", selected.equals(today), () -> go(today)),
                UI.chip("Yesterday", selected.equals(today.minusDays(1)), () -> go(today.minusDays(1)))));
        card.add(UI.gap(14));

        DayStatus s = AttendanceService.status(selected);
        String heading = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy").format(selected);
        if (s.type() == DayStatus.Type.CLASSES) {
            AttendanceDay day = s.day();
            Color c = day.attended() == day.held() ? Theme.good() : Theme.bad();
            UI.addTo(card, UI.row(UI.h3(heading), UI.pill(day.attended() + " of " + day.held(), c)));
            UI.addTo(card, UI.muted((day.leaveNote() != null ? day.leaveNote() + " · " : "")
                    + "Attended " + day.attended() + ", missed " + (day.held() - day.attended())));
            card.add(UI.gap(6));
            for (PeriodMark m : day.marks()) {
                UI.addTo(card, UI.divider(Theme.VIOLET));
                UI.addTo(card, periodRow(m));
            }
        } else {
            UI.addTo(card, UI.h3(heading));
            card.add(UI.gap(4));
            String text = switch (s.type()) {
                case BEFORE_SEMESTER -> "The semester had not started yet.";
                case OFF -> s.name() + ". No classes.";
                default -> selected.equals(today) ? "Today's attendance is added after classes end." : "This day has not happened yet.";
            };
            UI.addTo(card, UI.label(text, Theme.BODY, Theme.muted()));
        }
        return card;
    }

    private JComponent periodRow(PeriodMark m) {
        Subject s = m.period().subject();
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        JLabel time = UI.mono(m.period().label());
        time.setPreferredSize(new Dimension(110, 20));
        row.add(time, BorderLayout.WEST);
        JPanel what = UI.column();
        JPanel name = UI.flow(6, UI.dot(s.color()), UI.bold(s.name()));
        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel teacher = UI.muted(s.faculty());
        teacher.setBorder(new EmptyBorder(0, 18, 0, 0));
        what.add(name);
        what.add(teacher);
        row.add(what, BorderLayout.CENTER);
        row.add(UI.pill(m.present() ? "Present" : "Absent", m.present() ? Theme.good() : Theme.bad()), BorderLayout.EAST);
        return row;
    }

    private JComponent monthTab(int target) {
        RoundedPanel card = UI.card(Theme.SKY);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MMMM yyyy");
        boolean first = true;
        for (Map.Entry<YearMonth, Counts> e : AttendanceService.byMonth().entrySet()) {
            Counts c = e.getValue();
            boolean low = c.percent() < target;
            if (!first) UI.addTo(card, UI.divider(Theme.SKY));
            first = false;
            card.add(UI.gap(8));
            UI.addTo(card, UI.row(UI.bold(f.format(e.getKey())),
                    UI.label(String.format("%.1f%%", c.percent()), Theme.BOLD, low ? Theme.bad() : Theme.ink())));
            card.add(UI.gap(4));
            UI.addTo(card, new ProgressBar(c.percent(), low ? Theme.bad() : Theme.accent(), low ? Theme.bad() : Theme.PINK));
            UI.addTo(card, UI.mono(c.attended() + " attended · " + c.missed() + " missed · " + c.held() + " held"));
            card.add(UI.gap(8));
        }
        return card;
    }

    private void subjectTab(int target) {
        section(UI.muted("Move a slider to see what happens if you skip classes."));
        for (int i = 0; i < CollegeData.SUBJECTS.size(); i++) {
            Subject s = CollegeData.SUBJECTS.get(i);
            Counts c = AttendanceService.forSubject(i);
            Verdict v = AttendanceService.verdict(c, target);

            RoundedPanel card = new RoundedPanel(new BorderLayout(16, 0), Theme.tint(s.color()), Theme.edge(s.color()), 26).hoverable(s.color());
            card.setBorder(new EmptyBorder(16, 18, 16, 18));
            card.add(new RingChart(c.percent(), s.color(), 70), BorderLayout.WEST);

            JPanel info = UI.column();
            add(info, UI.h3(s.name()));
            add(info, UI.muted(s.faculty()));
            add(info, UI.mono(c.attended() + " attended · " + c.missed() + " missed · " + c.held() + " held"));
            add(info, UI.label(v.text(), Theme.BODY, UI.verdictColor(v)));
            info.add(UI.gap(10));
            add(info, UI.muted("Last 10 classes, oldest first"));
            info.add(UI.gap(4));
            add(info, history(AttendanceService.lastClasses(i, 10)));
            info.add(UI.gap(10));

            JLabel skipLabel = UI.text("If I skip the next 0 classes");
            JLabel result = UI.text(" ");
            JSlider slider = new JSlider(0, 15, 0);
            slider.setOpaque(false);
            slider.setForeground(Theme.ink());
            slider.addChangeListener(e -> {
                int n = slider.getValue();
                double p = c.attended() * 100.0 / (c.held() + n);
                skipLabel.setText("If I skip the next " + n + " classes");
                result.setText(String.format("Your attendance would be %.1f%%", p) + (p >= target ? " (still on target)" : " (below " + target + "%)"));
                result.setForeground(p >= target ? Theme.good() : Theme.bad());
            });
            slider.setValue(1);
            slider.setValue(0);
            add(info, skipLabel);
            add(info, slider);
            add(info, result);
            card.add(info, BorderLayout.CENTER);
            section(card);
        }
    }

    private static void add(JPanel box, JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(c);
    }

    private static JComponent history(List<Boolean> list) {
        JComponent h = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 0; i < list.size(); i++) {
                    g2.setColor(list.get(i) ? Theme.good() : Theme.bad());
                    g2.fillRoundRect(i * 24, 0, 18, 18, 6, 6);
                }
                g2.dispose();
            }
        };
        h.setPreferredSize(new Dimension(Math.max(1, list.size() * 24), 18));
        h.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return h;
    }
}
