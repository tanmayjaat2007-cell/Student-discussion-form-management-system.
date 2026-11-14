package com.piet.campushub.pages;

import com.piet.campushub.data.ResultRow;
import com.piet.campushub.data.ResultService;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/** Semester results with SGPA, CGPA and a bar chart. */
public class ResultsPage extends Page {

    private String sem = "II";

    public ResultsPage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        section(UI.title("Results"));
        section(UI.muted("Semester results and CGPA on a 10-point scale. These are sample results."));
        section(UI.flow(8,
                UI.chip("Sem I", sem.equals("I"), () -> pick("I")),
                UI.chip("Sem II", sem.equals("II"), () -> pick("II")),
                UI.chip("Sem III", sem.equals("III"), () -> pick("III"))));

        if (ResultService.isDeclared(sem)) {
            boolean pass = ResultService.passed(sem);
            JPanel tiles = UI.grid(4, 12);
            tiles.add(tile(String.format("%.2f", ResultService.sgpa(sem)), "SGPA, Sem " + sem, Theme.INDIGO));
            tiles.add(tile(String.format("%.2f", ResultService.cgpa()), "CGPA so far", Theme.PINK));
            tiles.add(tile(String.valueOf(ResultService.credits(sem)), "Credits", Theme.SKY));
            tiles.add(tile(pass ? "Pass" : "Fail", "Result", pass ? Theme.GREEN : Theme.RED));
            section(tiles);

            RoundedPanel card = UI.card(Theme.VIOLET);
            UI.addTo(card, row(UI.mono("SUBJECT"), UI.mono("CREDITS"), UI.mono("GRADE"), UI.mono("POINTS")));
            for (ResultRow r : ResultService.rows(sem)) {
                UI.addTo(card, UI.divider(Theme.VIOLET));
                UI.addTo(card, row(UI.text(r.subject()), UI.text(String.valueOf(r.credits())), UI.bold(r.grade()), UI.text(String.valueOf(r.points()))));
            }
            UI.addTo(card, UI.divider(Theme.VIOLET));
            UI.addTo(card, row(UI.bold("SGPA"), UI.bold(String.valueOf(ResultService.credits(sem))), UI.text(""), UI.bold(String.format("%.2f", ResultService.sgpa(sem)))));
            section(card);
        } else {
            RoundedPanel card = UI.card(Theme.AMBER);
            UI.addTo(card, UI.h3("Semester " + sem));
            card.add(UI.gap(6));
            UI.addTo(card, UI.text("Results are not declared yet. Your internal marks so far are on the Internal Marks page."));
            card.add(UI.gap(10));
            UI.addTo(card, UI.flow(0, UI.ghost("Open internal marks", () -> app.showPage("marks"))));
            section(card);
        }

        RoundedPanel chart = UI.card(Theme.PINK);
        UI.addTo(chart, UI.h3("SGPA by semester"));
        chart.add(UI.gap(8));
        UI.addTo(chart, new SgpaChart(ResultService.declaredSemesters()));
        section(chart);
        section(UI.muted("Grade points: O 10, A+ 9, A 8, B+ 7, B 6, C 5, F 0. Official results come from RTU, Kota."));
    }

    private void pick(String s) {
        sem = s;
        rebuild();
    }

    private static JComponent tile(String value, String label, Color color) {
        RoundedPanel p = UI.card(color);
        UI.addTo(p, UI.count(value, Theme.BIG, color));
        UI.addTo(p, UI.muted(label));
        return p;
    }

    private static JPanel row(JComponent a, JComponent b, JComponent c, JComponent d) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 0, 8, 0));
        JComponent[] cells = {a, b, c, d};
        for (int i = 0; i < 4; i++) {
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = i;
            g.weightx = i == 0 ? 1 : 0;
            g.fill = GridBagConstraints.HORIZONTAL;
            g.insets = new Insets(0, i == 0 ? 0 : 10, 0, 0);
            cells[i].setPreferredSize(new Dimension(i == 0 ? 200 : 70, cells[i].getPreferredSize().height));
            p.add(cells[i], g);
        }
        return p;
    }

    /** Bars for each declared semester's SGPA, out of 10. */
    private static class SgpaChart extends JComponent {
        private final List<String> sems;

        SgpaChart(List<String> sems) {
            this.sems = sems;
            setPreferredSize(new Dimension(300, 180));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int base = getHeight() - 24, top = 20, h = base - top;
            g2.setFont(Theme.MONO);
            for (int v : new int[]{0, 5, 10}) {
                int y = base - h * v / 10;
                g2.setColor(Theme.line());
                g2.drawLine(30, y, getWidth(), y);
                g2.setColor(Theme.muted());
                g2.drawString(String.valueOf(v), 6, y + 4);
            }
            int n = sems.size(), bw = 70, gap = (getWidth() - 30 - n * bw) / (n + 1);
            for (int i = 0; i < n; i++) {
                double val = ResultService.sgpa(sems.get(i));
                int x = 30 + gap * (i + 1) + bw * i, bh = (int) (h * val / 10);
                g2.setPaint(new GradientPaint(0, base - bh, Theme.PINK, 0, base, Theme.accent()));
                g2.fillRoundRect(x, base - bh, bw, bh, 10, 10);
                g2.setColor(Theme.ink());
                String s = String.format("%.2f", val);
                g2.drawString(s, x + (bw - g2.getFontMetrics().stringWidth(s)) / 2, base - bh - 6);
                g2.setColor(Theme.muted());
                String lab = "Sem " + sems.get(i);
                g2.drawString(lab, x + (bw - g2.getFontMetrics().stringWidth(lab)) / 2, base + 16);
            }
            g2.dispose();
        }
    }
}
