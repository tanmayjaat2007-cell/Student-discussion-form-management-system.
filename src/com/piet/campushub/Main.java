package com.piet.campushub;

import com.piet.campushub.ui.AppFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * PIET Campus Hub - student portal desktop app.
 * Run this class in IntelliJ (green Run button next to main).
 * Sign in with: Riya Suthar / 9216@riya
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // the default look and feel is fine
        }
        SwingUtilities.invokeLater(() -> new AppFrame().setVisible(true));
    }
}
