package com.piet.campushub.pages;

import com.piet.campushub.data.CollegeData;
import com.piet.campushub.data.FeeService;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** Fee statement: balance, structure and payments. */
public class FeesPage extends Page {

    public FeesPage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        int total = FeeService.total(), paid = FeeService.paid(), due = FeeService.due();
        long daysLeft = ChronoUnit.DAYS.between(CollegeData.today(), FeeService.DUE_DATE);
        String dueText = DateTimeFormatter.ofPattern("d MMM yyyy").format(FeeService.DUE_DATE);
        String note = due == 0 ? "All dues are cleared."
                : daysLeft >= 0 ? daysLeft + " day" + (daysLeft == 1 ? "" : "s") + " left to pay by " + dueText
                : "Overdue since " + dueText;

        section(UI.title("Fees"));
        section(UI.muted("Semester III fee statement. These are sample amounts."));

        RoundedPanel balance = UI.card(Theme.GREEN);
        UI.addTo(balance, UI.muted("Balance due"));
        UI.addTo(balance, UI.count(FeeService.rupees(due), new Font("SansSerif", Font.BOLD, 34), daysLeft < 0 && due > 0 ? Theme.bad() : Theme.ink()));
        UI.addTo(balance, UI.muted(note));
        balance.add(UI.gap(10));
        UI.addTo(balance, new ProgressBar(paid * 100.0 / total, Theme.GREEN, Theme.TEAL));
        balance.add(UI.gap(10));
        JPanel three = UI.grid(3, 10);
        three.add(figure(FeeService.rupees(total), "Total"));
        three.add(figure(FeeService.rupees(paid), "Paid"));
        three.add(figure(FeeService.rupees(due), "Due"));
        UI.addTo(balance, three);
        balance.add(UI.gap(12));
        UI.addTo(balance, UI.flow(0, UI.primary("Pay balance", () -> UI.message(this, "Pay balance",
                "Online payment is not connected in this app.\nPay at the college accounts office or on the official college payment page."))));
        section(balance);

        RoundedPanel structure = UI.card(Theme.TEAL);
        UI.addTo(structure, UI.h3("Fee structure"));
        structure.add(UI.gap(6));
        for (int i = 0; i < FeeService.HEADS.length; i++) {
            UI.addTo(structure, UI.divider(Theme.TEAL));
            UI.addTo(structure, line(UI.text(FeeService.HEADS[i]), UI.text(FeeService.rupees(FeeService.AMOUNTS[i]))));
        }
        UI.addTo(structure, UI.divider(Theme.TEAL));
        UI.addTo(structure, line(UI.bold("Total"), UI.bold(FeeService.rupees(total))));
        section(structure);

        RoundedPanel payments = UI.card(Theme.INDIGO);
        UI.addTo(payments, UI.h3("Payments"));
        payments.add(UI.gap(6));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d MMM yyyy");
        for (int i = 0; i < FeeService.PAID_ON.length; i++) {
            UI.addTo(payments, UI.divider(Theme.INDIGO));
            UI.addTo(payments, line(UI.text(f.format(FeeService.PAID_ON[i]) + "   " + FeeService.PAID_NOTE[i]),
                    UI.bold(FeeService.rupees(FeeService.PAID_AMOUNT[i]))));
        }
        section(payments);
        section(UI.muted("Check your real fee statement with the college accounts office."));
    }

    private static JComponent figure(String value, String label) {
        JPanel p = UI.column();
        p.add(UI.label(value, Theme.H3, Theme.ink()));
        p.add(UI.muted(label));
        return p;
    }

    private static JPanel line(JComponent left, JComponent right) {
        JPanel r = UI.row(left, right);
        r.setBorder(new EmptyBorder(8, 0, 8, 0));
        return r;
    }
}
