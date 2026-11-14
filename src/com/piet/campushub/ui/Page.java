package com.piet.campushub.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Base class for every page. Pages add sections one under another with add(...).
 * The page fills the width of the window and scrolls up and down.
 */
public abstract class Page extends JPanel implements Scrollable {

    protected final AppFrame app;
    private final GridBagConstraints gc = new GridBagConstraints();
    private int row = 0;

    protected Page(AppFrame app) {
        super(new GridBagLayout());
        this.app = app;
        setBackground(Theme.bg());
        setBorder(new EmptyBorder(18, 24, 32, 24));
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTH;
        gc.insets = new Insets(0, 0, 14, 0);
    }

    /** Builds the page content. Called once when the page is shown. */
    protected abstract void build();

    /** Stops timers when the page is closed. */
    public void dispose() {
    }

    /** Adds a section under the previous one. */
    protected void section(Component c) {
        gc.gridy = row++;
        super.add(c, gc);
    }

    /** Pushes everything to the top. Call at the end of build(). */
    protected void finish() {
        GridBagConstraints f = new GridBagConstraints();
        f.gridy = row++;
        f.weighty = 1;
        super.add(Box.createVerticalGlue(), f);
    }

    /** Clears and rebuilds the page, keeping the scroll position. */
    public void rebuild() {
        dispose();
        removeAll();
        row = 0;
        build();
        finish();
        revalidate();
        repaint();
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return r.height - 40; }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
}
