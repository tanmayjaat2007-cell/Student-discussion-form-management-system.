package com.piet.campushub.pages;

import com.piet.campushub.data.CollegeData;
import com.piet.campushub.data.Session;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Theme, attendance target, profile, reset and log out. */
public class SettingsPage extends Page {

    private String note = "";

    public SettingsPage(AppFrame app) {
        super(app);
    }

    @Override
    protected void build() {
        section(UI.title("Settings"));
        if (!note.isEmpty()) section(UI.label(note, Theme.BODY, Theme.good()));

        RoundedPanel prefs = UI.card(Theme.INDIGO);
        JComboBox<String> theme = UI.field(new JComboBox<>(new String[]{"Dark", "Light"}));
        theme.setSelectedIndex(Theme.dark ? 0 : 1);
        JSpinner target = new JSpinner(new SpinnerNumberModel(Session.target, 50, 100, 1));
        UI.field((JComponent) target.getEditor().getComponent(0));
        UI.addTo(prefs, line("Theme", theme));
        UI.addTo(prefs, UI.divider(Theme.INDIGO));
        UI.addTo(prefs, line("Attendance target (%)", target));
        prefs.add(UI.gap(10));
        UI.addTo(prefs, UI.flow(0, UI.primary("Save settings", () -> {
            Session.target = (Integer) target.getValue();
            boolean wantDark = theme.getSelectedIndex() == 0;
            if (wantDark != Theme.dark) {
                app.toggleTheme();
            } else {
                note = "Settings saved.";
                rebuild();
                Toast.show(this, "Settings saved");
            }
        })));
        section(prefs);

        RoundedPanel profile = UI.card(Theme.TEAL);
        UI.addTo(profile, UI.h3("Profile"));
        profile.add(UI.gap(6));
        String[][] rows = {
                {"Name", CollegeData.USERNAME},
                {"Course", "B.Tech, Computer Science & Engineering"},
                {"Semester", "III"},
                {"College", CollegeData.COLLEGE}};
        for (String[] r : rows) {
            UI.addTo(profile, UI.divider(Theme.TEAL));
            UI.addTo(profile, line(r[0], UI.text(r[1])));
        }
        section(profile);

        RoundedPanel data = UI.card(Theme.AMBER);
        UI.addTo(data, UI.h3("Leave and calendar data"));
        data.add(UI.gap(4));
        UI.addTo(data, UI.muted("Your leave requests and calendar events are kept while the app is open."));
        data.add(UI.gap(10));
        UI.addTo(data, UI.flow(0, UI.danger("Reset to sample data", () -> {
            Session.resetSampleData();
            note = "Sample data restored.";
            rebuild();
            Toast.show(this, "Sample data restored");
        })));
        section(data);

        section(UI.flow(0, UI.danger("Log out", app::logout)));
    }

    private static JPanel line(String label, JComponent control) {
        JPanel r = UI.row(UI.text(label), control);
        r.setBorder(new EmptyBorder(8, 0, 8, 0));
        return r;
    }
}
