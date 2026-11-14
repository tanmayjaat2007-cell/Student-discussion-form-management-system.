package com.piet.campushub.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/** Loads the campus photo, its blurred copy and the profile photo once. */
public final class CampusImage {

    private CampusImage() {
    }

    private static BufferedImage campus, blurred, profile;
    private static boolean loaded;

    private static void load() {
        if (loaded) return;
        loaded = true;
        campus = read("campus.jpg");
        blurred = read("campus_blur.jpg");
        profile = read("profile.jpg");
    }

    private static BufferedImage read(String name) {
        try (InputStream in = CampusImage.class.getResourceAsStream("/com/piet/campushub/img/" + name)) {
            return in == null ? null : ImageIO.read(in);
        } catch (Exception e) {
            return null;
        }
    }

    public static BufferedImage profile() {
        load();
        return profile;
    }

    /** Draws the campus photo to cover the area, with a dark fade at the bottom for readable text. */
    public static void paintCover(Graphics2D g2, int w, int h, int arc) {
        paint(g2, w, h, arc, false);
    }

    /** Same as paintCover but with the blurred photo, used behind the sign-in card. */
    public static void paintBlurred(Graphics2D g2, int w, int h) {
        paint(g2, w, h, 0, true);
    }

    private static void paint(Graphics2D g2, int w, int h, int arc, boolean blur) {
        load();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        Shape oldClip = g2.getClip();
        if (arc > 0) g2.clip(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        BufferedImage img = blur && blurred != null ? blurred : campus;
        if (img != null) {
            double scale = Math.max((double) w / img.getWidth(), (double) h / img.getHeight());
            int iw = (int) (img.getWidth() * scale), ih = (int) (img.getHeight() * scale);
            g2.drawImage(img, (w - iw) / 2, (int) ((h - ih) * 0.45), iw, ih, null);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(0x1b2150), 0, h, new Color(0x7c3aed)));
            g2.fillRect(0, 0, w, h);
        }
        if (blur) {
            g2.setColor(new Color(9, 12, 32, Theme.dark ? 120 : 70));
            g2.fillRect(0, 0, w, h);
        } else {
            g2.setPaint(new GradientPaint(0, h * 0.25f, new Color(9, 12, 32, 20), 0, h, new Color(9, 12, 32, 225)));
            g2.fillRect(0, 0, w, h);
        }
        g2.setClip(oldClip);
    }
}
