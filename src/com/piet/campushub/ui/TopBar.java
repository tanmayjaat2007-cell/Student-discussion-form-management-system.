package com.piet.campushub.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** The bar above each page: page title, light/dark switch and the initials badge (opens Settings). */
public class TopBar extends JPanel {

    public TopBar(AppFrame app, String title) {
        super(new BorderLayout());
        setBackground(Theme.bg());
        setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Theme.line()), new EmptyBorder(10, 24, 10, 20)));
        add(UI.h2(title), BorderLayout.CENTER);

        RoundButton theme = UI.ghost(Theme.dark ? "Light mode" : "Dark mode", app::toggleTheme);
        Avatar avatar = new Avatar(38);
        avatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatar.setToolTipText("Click for Settings, double-click for Home");
        avatar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { app.showPage(e.getClickCount() >= 2 ? "home" : "settings"); }
        });
        add(UI.flow(10, theme), BorderLayout.EAST);
    }
}
