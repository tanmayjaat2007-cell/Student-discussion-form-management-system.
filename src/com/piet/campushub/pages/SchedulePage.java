package com.piet.campushub.pages;

import com.piet.campushub.data.CollegeData;
import com.piet.campushub.data.Period;
import com.piet.campushub.data.Subject;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/** Weekly timetable with the class running now marked. */
public class SchedulePage extends Page {

    private DayOfWeek day;

    public SchedulePage(AppFrame app) {
        super(app);
        DayOfWeek today = CollegeData.today().getDayOfWeek();
        day = today == DayOfWeek.SUNDAY ? DayOfWeek.MONDAY : today;
    }

    @Override
    protected void build() {
        DayOfWeek today = CollegeData.today().getDayOfWeek();
        section(UI.title("Class schedule"));
        section(UI.muted("College hours 8:00 AM to 2:50 PM. Lunch break 12:00 to 12:50 PM. Period order is a sample."));

        JPanel days = UI.flow(8);
        for (int d = 1; d <= 6; d++) {
            DayOfWeek dw = DayOfWeek.of(d);
            String label = dw.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + (dw == today ? " (today)" : "");
            days.add(UI.chip(label, dw == day, () -> { day = dw; rebuild(); }));
        }
        section(days);

        RoundedPanel card = UI.card(Theme.SKY);
        UI.addTo(card, UI.h3(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH)));
        card.add(UI.gap(6));
        List<Period> periods = CollegeData.timetable(day);
        for (Period p : periods) {
            boolean now = day == today && p.isRunningAt(CollegeData.now());
            UI.addTo(card, UI.divider(Theme.SKY));
            UI.addTo(card, row(p, now));
        }
        section(card);

        RoundedPanel faculty = UI.card(Theme.VIOLET);
        UI.addTo(faculty, UI.h3("Faculty"));
        faculty.add(UI.gap(6));
        for (Subject s : CollegeData.SUBJECTS) {
            UI.addTo(faculty, UI.divider(Theme.VIOLET));
            JPanel r = UI.row(UI.flow(6, UI.dot(s.color()), UI.bold(s.name())), UI.text(s.faculty()));
            r.setBorder(new EmptyBorder(8, 0, 8, 0));
            UI.addTo(faculty, r);
        }
        section(faculty);
    }

    private JComponent row(Period p, boolean now) {
        JPanel row = now
                ? new RoundedPanel(new BorderLayout(12, 0), Theme.mix(Theme.accent(), Theme.surface(), 0.25), null, 14)
                : new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(9, now ? 10 : 0, 9, now ? 10 : 0));
        JLabel time = UI.label(p.label(), Theme.MONO, now ? Theme.accent() : Theme.muted());
        time.setPreferredSize(new Dimension(110, 20));
        row.add(time, BorderLayout.WEST);
        if (p.isLunch()) {
            row.add(UI.label("Lunch break", Theme.BODY.deriveFont(Font.ITALIC), Theme.muted()), BorderLayout.CENTER);
        } else {
            JPanel what = UI.column();
            JPanel name = UI.flow(6, UI.dot(p.subject().color()), UI.bold(p.subject().name()));
            name.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel teacher = UI.muted(p.subject().faculty());
            teacher.setBorder(new EmptyBorder(0, 18, 0, 0));
            what.add(name);
            what.add(teacher);
            row.add(what, BorderLayout.CENTER);
            if (now) row.add(UI.pill("NOW", Theme.accent()), BorderLayout.EAST);
        }
        return row;
    }
}
