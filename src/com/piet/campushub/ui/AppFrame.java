package com.piet.campushub.ui;

import com.piet.campushub.pages.*;

import javax.swing.*;
import java.awt.*;

/**
 * The main window. Shows the sign-in screen first, then the side menu, top bar and one page at a time.
 * After every sign-in the Home page opens.
 */
public class AppFrame extends JFrame {

    /** Menu entries: page key, label, colour. */
    public static final Object[][] MENU = {
            {"home", "Home", Theme.INDIGO},
            {"attendance", "Attendance", Theme.TEAL},
            {"schedule", "Class Schedule", Theme.SKY},
            {"marks", "Internal Marks", Theme.VIOLET},
            {"results", "Results", Theme.PINK},
            {"fees", "Fees", Theme.GREEN},
            {"leave", "Leave", Theme.AMBER},
            {"calendar", "Calendar", Theme.ORANGE},
            {"settings", "Settings", Theme.SLATE}
    };

    private String current = "home";
    private Page page;
    private boolean signedIn;

    public AppFrame() {
        super("PIET Campus Hub");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1120, 780);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);
        showLogin();
    }

    /** Fades the window content in, used when a page opens. */
    public void fadeIn() {
        float[] a = {1f};
        Color bg = Theme.bg();
        JComponent glass = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, a[0])));
                g2.setColor(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        setGlassPane(glass);
        glass.setVisible(true);
        Anim.run(280, k -> {
            a[0] = (float) (1 - k);
            glass.repaint();
            if (k >= 1) glass.setVisible(false);
        });
    }

    public void showLogin() {
        closePage();
        signedIn = false;
        applyDefaults();
        setContentPane(new LoginPage(this));
        revalidate();
        repaint();
        fadeIn();
    }

    /** Called by the sign-in page when the username and password are right. */
    public void signedIn() {
        signedIn = true;
        showPage("home");
        Toast.show(getContentPane(), "Welcome back, Riya");
    }

    public void showPage(String key) {
        closePage();
        current = key;
        applyDefaults();
        page = createPage(key);
        page.rebuild();

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(Theme.bg());
        right.add(new TopBar(this, titleOf(key)), BorderLayout.NORTH);
        right.add(UI.scroll(page), BorderLayout.CENTER);

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Theme.bg());
        shell.add(new Sidebar(this, key), BorderLayout.WEST);
        shell.add(right, BorderLayout.CENTER);
        setContentPane(shell);
        revalidate();
        repaint();
        Page opened = page;
        Anim.run(380, k -> {
            opened.setBorder(new javax.swing.border.EmptyBorder(18 + (int) Math.round(22 * (1 - k)), 24, 32, 24));
            opened.revalidate();
        });
    }

    public void toggleTheme() {
        Theme.dark = !Theme.dark;
        if (signedIn) showPage(current); else showLogin();
    }

    public void logout() {
        showLogin();
    }

    private void closePage() {
        if (page != null) page.dispose();
        page = null;
    }

    private Page createPage(String key) {
        return switch (key) {
            case "attendance" -> new AttendancePage(this);
            case "schedule" -> new SchedulePage(this);
            case "marks" -> new MarksPage(this);
            case "results" -> new ResultsPage(this);
            case "fees" -> new FeesPage(this);
            case "leave" -> new LeavePage(this);
            case "calendar" -> new CalendarPage(this);
            case "settings" -> new SettingsPage(this);
            default -> new HomePage(this);
        };
    }

    public static String titleOf(String key) {
        if ("home".equals(key)) return "PIET Campus Hub";
        for (Object[] m : MENU) if (m[0].equals(key)) return (String) m[1];
        return "PIET Campus Hub";
    }

    /** Colours for standard Swing parts such as drop-downs and message boxes. */
    private static void applyDefaults() {
        UIManager.put("ComboBox.background", Theme.bg());
        UIManager.put("ComboBox.foreground", Theme.ink());
        UIManager.put("ComboBox.selectionBackground", Theme.accent());
        UIManager.put("ComboBox.selectionForeground", Theme.accentInk());
        UIManager.put("ComboBox.disabledForeground", Theme.muted());
        UIManager.put("List.background", Theme.bg());
        UIManager.put("List.foreground", Theme.ink());
        UIManager.put("Spinner.background", Theme.bg());
        UIManager.put("FormattedTextField.background", Theme.bg());
        UIManager.put("FormattedTextField.foreground", Theme.ink());
        UIManager.put("OptionPane.background", Theme.surface());
        UIManager.put("Panel.background", Theme.surface());
        UIManager.put("OptionPane.messageForeground", Theme.ink());
        UIManager.put("Slider.background", Theme.surface());
        UIManager.put("ScrollBar.background", Theme.bg());
        UIManager.put("ScrollBar.thumb", Theme.line());
        UIManager.put("ScrollBar.track", Theme.bg());
        UIManager.put("CheckBox.background", Theme.surface());
        UIManager.put("CheckBox.foreground", Theme.ink());
    }
}
