package com.valkryst.Emberstone.mvc.controller;

import com.valkryst.Emberstone.mvc.view.View;
import lombok.Getter;

import java.awt.*;

public class Controller {
    /** The view. */
    @Getter public final View view;

    /**
     * Constructs a new Controller.
     *
     * @param view
     *          The view.
     */
    public Controller(final View view) {
        this.view = view;
    }

    /**
     * Updates the controller's state.
     *
     * @param deltaTime
     *          The delta time.
     */
    public void update(final double deltaTime) {}

    /**
     * Draws the view on a graphics context.
     *
     * @param gc
     *          The graphics context.
     */
    public void draw(final Graphics2D gc) {
        if (view == null) {
            return;
        }

        view.draw(gc);
    }

    /** Adds the view to the game's canvas. */
    public void addToCanvas() {
        view.addToCanvas();
    }

    /** Removes the view from the game's canvas. */
    public void removeFromCanvas() {
        view.removeFromCanvas();
    }
}
