package com.piet.campushub.pages;

import com.piet.campushub.data.CollegeData;
import com.piet.campushub.data.LeaveRequest;
import com.piet.campushub.data.Session;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

/** Write and track leave requests. Requests are kept while the app is open. */
public class LeavePage extends Page {

    private String lastMessage = "";

    public LeavePage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        section(UI.title("Leave"));
        section(UI.muted("Write a leave request and keep track of it here."));
        section(form());
        section(requestList());
    }

    private JComponent form() {
        RoundedPanel card = UI.card(Theme.AMBER);
        UI.addTo(card, UI.h3("New request"));
        card.add(UI.gap(10));

        JComboBox<String> type = UI.field(new JComboBox<>(new String[]{"Medical", "Personal", "Event / Sports", "Other"}));
        String today = CollegeData.today().toString();
        JTextField from = UI.field(new JTextField(today, 10));
        JTextField to = UI.field(new JTextField(today, 10));
        JTextArea reason = UI.field(new JTextArea(3, 30));
        reason.setLineWrap(true);
        reason.setWrapStyleWord(true);
        JLabel error = UI.label(lastMessage.isEmpty() ? " " : lastMessage, Theme.SMALL,
                lastMessage.startsWith("Request") ? Theme.good() : Theme.bad());

        UI.addTo(card, UI.muted("Type"));
        UI.addTo(card, UI.flow(0, type));
        card.add(UI.gap(8));
        UI.addTo(card, UI.muted("From and to (yyyy-mm-dd)"));
        UI.addTo(card, UI.flow(8, from, UI.muted("to"), to));
        card.add(UI.gap(8));
        UI.addTo(card, UI.muted("Reason"));
        UI.addTo(card, reason);
        card.add(UI.gap(6));
        UI.addTo(card, error);
        card.add(UI.gap(6));
        UI.addTo(card, UI.flow(0, UI.primary("Save request", () -> {
            try {
                LocalDate f = LocalDate.parse(from.getText().trim());
                LocalDate t = LocalDate.parse(to.getText().trim());
                if (t.isBefore(f)) {
                    lastMessage = "The end date is before the start date.";
                } else if (reason.getText().isBlank()) {
                    lastMessage = "Write a short reason.";
                } else {
                    Session.LEAVES.add(new LeaveRequest(Session.newId(), (String) type.getSelectedItem(), f, t,
                            reason.getText().trim(), "Pending", false));
                    lastMessage = "Request saved.";
                    Toast.show(this, "Leave request saved");
                }
            } catch (DateTimeParseException ex) {
                lastMessage = "Type both dates as yyyy-mm-dd, for example 2026-10-14.";
            }
            rebuild();
        })));
        card.add(UI.gap(8));
        UI.addTo(card, UI.muted("Requests are kept while the app is open. Tell your class coordinator separately."));
        return card;
    }

    private JComponent requestList() {
        RoundedPanel card = UI.card(Theme.INDIGO);
        UI.addTo(card, UI.h3("Your requests"));
        card.add(UI.gap(6));
        List<LeaveRequest> sorted = Session.LEAVES.stream()
                .sorted(Comparator.comparing(LeaveRequest::from).reversed()).toList();
        if (sorted.isEmpty()) UI.addTo(card, UI.muted("No leave requests yet."));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d MMM");
        for (LeaveRequest l : sorted) {
            UI.addTo(card, UI.divider(Theme.INDIGO));
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(9, 0, 9, 0));
            String dates = l.from().equals(l.to()) ? f.format(l.from()) : f.format(l.from()) + " to " + f.format(l.to());
            JLabel when = UI.mono(dates);
            when.setPreferredSize(new Dimension(120, 20));
            row.add(when, BorderLayout.WEST);
            JPanel what = UI.column();
            JLabel t = UI.bold(l.type() + (l.sample() ? "  (sample)" : ""));
            t.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel r = UI.muted(l.reason());
            r.setAlignmentX(Component.LEFT_ALIGNMENT);
            what.add(t);
            what.add(r);
            if (l.isPending()) {
                JPanel w = UI.flow(0, UI.ghost("Withdraw", () -> {
                    Session.LEAVES.removeIf(x -> x.id() == l.id());
                    lastMessage = "";
                    Toast.show(this, "Request withdrawn");
                    rebuild();
                }));
                w.setAlignmentX(Component.LEFT_ALIGNMENT);
                what.add(UI.gap(4));
                what.add(w);
            }
            row.add(what, BorderLayout.CENTER);
            Color c = switch (l.status()) {
                case "Approved" -> Theme.good();
                case "Pending" -> Theme.warn();
                default -> Theme.bad();
            };
            JPanel right = new JPanel(new BorderLayout());
            right.setOpaque(false);
            right.add(UI.pill(l.status(), c), BorderLayout.NORTH);
            row.add(right, BorderLayout.EAST);
            UI.addTo(card, row);
        }
        return card;
    }
}
