package com.piet.campushub.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

/** Midnight Indigo colours (dark and light) and fonts used across the app. */
public final class Theme {

    private Theme() {
    }

    public static boolean dark = true;

    public static final Color INDIGO = new Color(0x6366f1);
    public static final Color TEAL = new Color(0x14b8a6);
    public static final Color PINK = new Color(0xec4899);
    public static final Color AMBER = new Color(0xf59e0b);
    public static final Color SKY = new Color(0x0ea5e9);
    public static final Color VIOLET = new Color(0x8b5cf6);
    public static final Color GREEN = new Color(0x22c55e);
    public static final Color ORANGE = new Color(0xf97316);
    public static final Color RED = new Color(0xef4444);
    public static final Color SLATE = new Color(0x94a3b8);

    // Sapphire & Pearl: pearl and ink in light mode, deep sapphire night in dark mode.
    public static Color bg() { return dark ? new Color(0x0a0f1f) : new Color(0xf5f3ee); }
    public static Color surface() { return dark ? new Color(0x121a31) : Color.WHITE; }
    public static Color ink() { return dark ? new Color(0xeeece6) : new Color(0x141a2e); }
    public static Color muted() { return dark ? new Color(0x9ca1b6) : new Color(0x696d7e); }
    public static Color line() { return dark ? new Color(0x222b47) : new Color(0xe7e3da); }
    public static Color accent() { return dark ? new Color(0x8fa3ff) : new Color(0x2c4bd6); }
    public static Color accentInk() { return dark ? new Color(0x0a0f1f) : Color.WHITE; }
    public static Color good() { return dark ? new Color(0x5fd09a) : new Color(0x1d7a52); }
    public static Color warn() { return dark ? new Color(0xe8b45a) : new Color(0x9a6400); }
    public static Color bad() { return dark ? new Color(0xff8f86) : new Color(0xb4232c); }

    public static final Color SAP1 = new Color(0x101a44);
    public static final Color SAP2 = new Color(0x1f3594);
    public static final Color SAP3 = new Color(0x3a5bea);

    /** Section background: calm pearl/sapphire surface. Colour stays in icons and numbers. */
    public static Color tint(Color c) { return surface(); }

    /** Section border: a fine hairline. */
    public static Color edge(Color c) { return line(); }

    /** t is how much of colour a to use (0 to 1). */
    public static Color mix(Color a, Color b, double t) {
        return new Color(
                (int) Math.round(a.getRed() * t + b.getRed() * (1 - t)),
                (int) Math.round(a.getGreen() * t + b.getGreen() * (1 - t)),
                (int) Math.round(a.getBlue() * t + b.getBlue() * (1 - t)));
    }

    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    /** An elegant serif for headings: the first one installed on this computer. */
    public static final String DISPLAY = pick("Georgia", "Cambria", "Book Antiqua", "Times New Roman", "DejaVu Serif");

    private static String pick(String... names) {
        java.util.Set<String> have = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String n : names) if (have.contains(n)) return n;
        return "Serif";
    }

    public static final Font TITLE = new Font(DISPLAY, Font.BOLD, 28);
    public static final Font H2 = new Font(DISPLAY, Font.BOLD, 20);
    public static final Font H3 = new Font(DISPLAY, Font.BOLD, 16);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font MONO = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font BIG = new Font(DISPLAY, Font.BOLD, 30);
}
