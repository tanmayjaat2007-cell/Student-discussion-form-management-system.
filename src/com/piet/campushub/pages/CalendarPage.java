package com.piet.campushub.pages;

import com.piet.campushub.data.CalendarEvent;
import com.piet.campushub.data.CollegeData;
import com.piet.campushub.data.Session;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Month calendar with college dates and the student's own events. */
public class CalendarPage extends Page {

    private YearMonth month = YearMonth.from(CollegeData.today());
    private LocalDate selected = CollegeData.today();

    public CalendarPage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        section(UI.title("Calendar"));

        RoundedPanel card = UI.card(Theme.ORANGE);
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(UI.ghost("Prev", () -> { month = month.minusMonths(1); rebuild(); }), BorderLayout.WEST);
        JLabel title = UI.h3(DateTimeFormatter.ofPattern("MMMM yyyy").format(month));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        head.add(title, BorderLayout.CENTER);
        head.add(UI.ghost("Next", () -> { month = month.plusMonths(1); rebuild(); }), BorderLayout.EAST);
        UI.addTo(card, head);
        card.add(UI.gap(10));

        JPanel grid = UI.grid(7, 6);
        for (String d : new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}) {
            JLabel l = UI.mono(d);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            grid.add(l);
        }
        int blanks = month.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < blanks; i++) grid.add(new JLabel());
        for (int d = 1; d <= month.lengthOfMonth(); d++) grid.add(new DayCell(month.atDay(d)));
        UI.addTo(card, grid);
        card.add(UI.gap(10));
        UI.addTo(card, UI.flow(14, legend("Holiday", Theme.good()), legend("Academic", Theme.accent()), legend("My event", Theme.warn())));
        section(card);

        RoundedPanel day = UI.card(Theme.INDIGO);
        UI.addTo(day, UI.h3(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy").format(selected)));
        day.add(UI.gap(6));
        List<CalendarEvent> events = eventsOn(selected);
        if (events.isEmpty()) UI.addTo(day, UI.muted("No events on this day."));
        for (CalendarEvent e : events) {
            UI.addTo(day, UI.divider(Theme.INDIGO));
            JComponent right = e.kind() == CalendarEvent.Kind.MINE
                    ? UI.ghost("Remove", () -> { Session.EVENTS.removeIf(x -> x.id() == e.id()); rebuild(); })
                    : (e.sample() ? UI.mono("SAMPLE") : new JLabel());
            JPanel r = UI.row(UI.flow(6, UI.dot(colorOf(e.kind())), UI.text(e.title())), right);
            r.setBorder(new EmptyBorder(8, 0, 8, 0));
            UI.addTo(day, r);
        }
        day.add(UI.gap(10));
        JTextField title2 = UI.field(new JTextField(22));
        Runnable add = () -> {
            String t = title2.getText().trim();
            if (!t.isEmpty()) {
                Session.EVENTS.add(new CalendarEvent(Session.newId(), selected, t, CalendarEvent.Kind.MINE, false));
                rebuild();
                Toast.show(this, "Event added");
            }
        };
        title2.addActionListener(e -> add.run());
        UI.addTo(day, UI.flow(8, title2, UI.primary("Add event", add)));
        section(day);
        section(UI.muted("Dates marked SAMPLE are examples. Confirm with the official academic calendar."));
    }

    private static List<CalendarEvent> eventsOn(LocalDate date) {
        List<CalendarEvent> list = new ArrayList<>();
        for (CalendarEvent e : CollegeData.COLLEGE_EVENTS) if (e.date().equals(date)) list.add(e);
        for (CalendarEvent e : Session.EVENTS) if (e.date().equals(date)) list.add(e);
        return list;
    }

    private static Color colorOf(CalendarEvent.Kind k) {
        return switch (k) {
            case HOLIDAY -> Theme.good();
            case ACADEMIC -> Theme.accent();
            case MINE -> Theme.warn();
        };
    }

    private static JComponent legend(String text, Color c) {
        return UI.flow(6, UI.dot(c), UI.muted(text));
    }

    /** One day square with its number and coloured dots for events. */
    private class DayCell extends JComponent {
        private final LocalDate date;
        private boolean hover;

        DayCell(LocalDate date) {
            this.date = date;
            setPreferredSize(new Dimension(60, 52));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { selected = date; rebuild(); }
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean isSel = date.equals(selected), isToday = date.equals(CollegeData.today());
            g2.setColor(isSel || hover ? Theme.mix(Theme.accent(), Theme.surface(), 0.25) : Theme.bg());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(isToday ? Theme.accent() : Theme.line());
            g2.setStroke(new BasicStroke(isToday ? 2f : 1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(Theme.ink());
            g2.setFont(Theme.BODY);
            g2.drawString(String.valueOf(date.getDayOfMonth()), 8, 18);
            int x = 8;
            for (CalendarEvent e : eventsOn(date)) {
                g2.setColor(colorOf(e.kind()));
                g2.fillOval(x, getHeight() - 14, 8, 8);
                x += 11;
            }
            g2.dispose();
        }
    }
}
