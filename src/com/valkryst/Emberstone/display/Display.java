package com.valkryst.Emberstone.display;

import com.valkryst.Emberstone.Keyboard;
import com.valkryst.Emberstone.display.view.View;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Display {
    /** Singleton instance. */
    private final static Display INSTANCE = new Display();

    private final Frame frame = new Frame();

    /** Constructs a new Display. */
    private Display() {
        frame.setBackground(Color.BLACK);
        frame.setTitle("Emberstone");
        frame.setResizable(false);
        frame.setIgnoreRepaint(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                frame.dispose();
            }
        });
        frame.setVisible(true);
        frame.setPreferredSize(new Dimension(1920, 1080));
        frame.pack();
        frame.setLocationRelativeTo(null); // Must be called after pack()

        // todo Load, from the saved settings which renderer to initialize.
        // todo Init a renderer.
    }

    /**
     * Adds a view to the frame.
     *
     * @param view
     *          The view to add.
     */
    public void addView(final View view) {
        if (view != null) {
            frame.add(view);
            frame.revalidate();

            final var keyboard = Keyboard.getInstance();
            view.addKeyListener(Keyboard.getInstance());
            keyboard.resetStates();

            view.requestFocusInWindow();
        }
    }

    /**
     * Removes a view from the frame.
     *
     * @param view
     *          The view to remove.
     */
    public void removeView(final View view) {
        if (view != null) {
            frame.remove(view);
            frame.revalidate();

            final var keyboard = Keyboard.getInstance();
            view.removeKeyListener(keyboard);
            keyboard.resetStates();
        }
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return
     *          The singleton instance.
     */
    public static Display getInstance() {
        return INSTANCE;
    }
}
