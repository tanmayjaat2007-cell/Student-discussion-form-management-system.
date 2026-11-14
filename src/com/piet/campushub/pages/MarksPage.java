package com.piet.campushub.pages;

import com.piet.campushub.data.CollegeData;
import com.piet.campushub.data.Subject;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Internal marks for Semester III. */
public class MarksPage extends Page {

    public MarksPage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        int got = 0;
        for (Subject s : CollegeData.SUBJECTS) got += s.total();
        double avg = got * 100.0 / (CollegeData.SUBJECTS.size() * 50);

        section(UI.title("Internal marks"));
        section(UI.muted(String.format("Semester III so far: %.1f%%. These are sample marks.", avg)));

        RoundedPanel card = UI.card(Theme.VIOLET);
        JPanel table = new JPanel(new GridBagLayout());
        table.setOpaque(false);
        String[] head = {"SUBJECT", "TEST 1 /20", "TEST 2 /20", "ASSIGN /10", "TOTAL /50", "SCORE"};
        for (int c = 0; c < head.length; c++) cell(table, UI.mono(head[c]), c, 0);
        int r = 1;
        for (Subject s : CollegeData.SUBJECTS) {
            JPanel name = UI.column();
            JPanel n = UI.flow(6, UI.dot(s.color()), UI.bold(s.name()));
            n.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel f = UI.muted(s.faculty());
            f.setBorder(new EmptyBorder(0, 18, 0, 0));
            name.add(n);
            name.add(f);
            cell(table, name, 0, r);
            cell(table, UI.text(String.valueOf(s.test1())), 1, r);
            cell(table, UI.text(String.valueOf(s.test2())), 2, r);
            cell(table, UI.text(String.valueOf(s.assignment())), 3, r);
            cell(table, UI.bold(String.valueOf(s.total())), 4, r);
            cell(table, new ProgressBar(s.percent(), s.color(), s.color()), 5, r);
            r++;
        }
        UI.addTo(card, table);
        section(card);
        section(UI.muted("Replace these in CollegeData.java when the department publishes the real marks."));
    }

    private static void cell(JPanel table, JComponent c, int col, int row) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = col;
        g.gridy = row;
        g.weightx = col == 0 ? 1 : (col == 5 ? 0.6 : 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(8, col == 0 ? 0 : 14, 8, 0);
        table.add(c, g);
    }
}
