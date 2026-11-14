package com.piet.campushub.pages;

import com.piet.campushub.data.CollegeData;
import com.piet.campushub.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Sign-in screen over a blurred campus photo.
 * Press Enter in the username box to jump to the password box, and Enter again to sign in.
 */
public class LoginPage extends JPanel {

    private final AppFrame app;
    private final JTextField username = UI.field(new JTextField(22));
    private final JPasswordField password = UI.field(new JPasswordField(22));
    private final JLabel error = UI.label(" ", Theme.SMALL, Theme.bad());
    private final JLabel caps = UI.label(" ", Theme.SMALL, Theme.warn());
    private final GridBagConstraints cardPos = new GridBagConstraints();
    private final RoundedPanel card;
    private float cardAlpha = 0f;

    public LoginPage(AppFrame app) {
        super(new GridBagLayout());
        this.app = app;

        card = new RoundedPanel(null, Theme.withAlpha(Theme.surface(), 235), Theme.line(), 28) {
            @Override
            protected void paintChildren(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cardAlpha));
                super.paintChildren(g2);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26, 30, 24, 30));

        Avatar photo = new Avatar(84);
        JPanel photoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        photoRow.setOpaque(false);
        photoRow.add(photo);
        UI.addTo(card, photoRow);
        card.add(UI.gap(12));
        JLabel hello = UI.title("Welcome back");
        hello.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel college = UI.muted(CollegeData.COLLEGE);
        college.setHorizontalAlignment(SwingConstants.CENTER);
        UI.addTo(card, centered(hello));
        UI.addTo(card, centered(college));
        card.add(UI.gap(20));

        UI.addTo(card, UI.label("Username", Theme.BOLD, Theme.muted()));
        card.add(UI.gap(6));
        UI.addTo(card, username);
        card.add(UI.gap(14));

        UI.addTo(card, UI.label("Password", Theme.BOLD, Theme.muted()));
        card.add(UI.gap(6));
        password.setEchoChar('•');
        UI.addTo(card, password);
        JCheckBox show = new JCheckBox("Show password");
        show.setOpaque(false);
        show.setForeground(Theme.muted());
        show.setFont(Theme.SMALL);
        show.setFocusable(false);
        show.addActionListener(e -> password.setEchoChar(show.isSelected() ? (char) 0 : '•'));
        UI.addTo(card, UI.row(caps, show));
        UI.addTo(card, error);
        card.add(UI.gap(10));

        RoundButton signIn = UI.primary("Sign in", this::submit);
        signIn.setBorder(new EmptyBorder(12, 16, 12, 16));
        JPanel full = new JPanel(new BorderLayout());
        full.setOpaque(false);
        full.add(signIn);
        UI.addTo(card, full);
        card.add(UI.gap(14));
        UI.addTo(card, centered(UI.muted("Press Enter to move to the next box.")));

        // Enter in the username box moves to the password box; Enter in the password box signs in.
        username.addActionListener(e -> {
            if (username.getText().isBlank()) {
                fail("Enter your username.");
            } else {
                error.setText(" ");
                password.requestFocusInWindow();
            }
        });
        password.addActionListener(e -> submit());

        KeyAdapter capsCheck = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    caps.setText(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK) ? "Caps Lock is on." : " ");
                } catch (UnsupportedOperationException ignored) {
                    caps.setText(" ");
                }
            }
        };
        username.addKeyListener(capsCheck);
        password.addKeyListener(capsCheck);
        highlightOnFocus(username);
        highlightOnFocus(password);

        RoundButton theme = UI.ghost(Theme.dark ? "Light mode" : "Dark mode", app::toggleTheme);
        GridBagConstraints t = new GridBagConstraints();
        t.gridx = 0; t.gridy = 0; t.anchor = GridBagConstraints.NORTHEAST; t.weightx = 1; t.insets = new Insets(16, 0, 0, 20);
        add(theme, t);

        cardPos.gridx = 0; cardPos.gridy = 1; cardPos.weighty = 1; cardPos.insets = new Insets(0, 0, 50, 0);
        add(card, cardPos);

        Anim.run(500, 80, k -> { cardAlpha = (float) k; card.repaint(); });
        SwingUtilities.invokeLater(username::requestFocusInWindow);
    }

    private static JPanel centered(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    /** Gives the box a coloured border while you type in it. */
    private static void highlightOnFocus(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new javax.swing.border.CompoundBorder(
                        new javax.swing.border.LineBorder(Theme.accent(), 2, true), new EmptyBorder(7, 9, 7, 9)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                UI.field(field);
            }
        });
    }

    private void submit() {
        String name = username.getText().trim().replaceAll("\\s+", " ");
        String pass = new String(password.getPassword());
        if (name.isEmpty()) {
            fail("Enter your username.");
            username.requestFocusInWindow();
        } else if (pass.isEmpty()) {
            fail("Enter your password.");
            password.requestFocusInWindow();
        } else if (name.equalsIgnoreCase(CollegeData.USERNAME) && pass.equals(CollegeData.PASSWORD)) {
            app.signedIn();
        } else {
            password.setText("");
            password.requestFocusInWindow();
            fail("Username or password is incorrect. Check capital letters in the password.");
        }
    }

    /** Shows the error and shakes the card. */
    private void fail(String message) {
        error.setText(message);
        final int[] step = {0};
        int[] offsets = {-12, 12, -9, 9, -5, 5, 0};
        Timer timer = new Timer(35, null);
        timer.addActionListener(e -> {
            int dx = offsets[step[0]++];
            cardPos.insets = new Insets(0, Math.max(0, dx), 50, Math.max(0, -dx));
            ((GridBagLayout) getLayout()).setConstraints(card, cardPos);
            revalidate();
            if (step[0] >= offsets.length) timer.stop();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        CampusImage.paintBlurred(g2, getWidth(), getHeight());
        g2.dispose();
    }
}
