package com.valkryst.Emberstone.mvc.view;

import com.valkryst.Emberstone.Game;
import com.valkryst.Emberstone.Settings;
import com.valkryst.Emberstone.mvc.component.Component;

import java.awt.*;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

public abstract class View {
    /** The components. */
    private final List<Component> components = new LinkedList<>();

    /**
     * Draws the view on a graphics context.
     *
     * @param gc
     *          The graphics context.
     */
    public void draw(final Graphics2D gc) {
        if (gc == null) {
            return;
        }

        for (final Component component : components) {
            component.draw(gc);
        }
    }

    /**
     * Adds a component to the view.
     *
     * @param component
     *          The component.
     */
    protected void addComponent(final Component component) {
        if (components.contains(component)) {
            return;
        }

        this.components.add(component);
    }

    /**
     * Removes a component from the view.
     *
     * @param component
     *          The component.
     */
    protected void removeComponent(final Component component) {
        components.remove(component);
    }

    public void addToCanvas() {
        final Settings settings = Settings.getInstance();

        if (settings.isDebugModeOn()) {
            System.out.println("Adding " + components.size() + " components for the " + this.getClass().getSimpleName() + " class.");
        }

        for (final Component component : components) {
            if (settings.isDebugModeOn()) {
                System.out.println("\tAdding " + component.getEventListeners().size() + " event listeners for a " + component.getClass().getSimpleName() + ".");
            }

            final Game game = Game.getInstance();
            for (final EventListener eventListener : component.getEventListeners()) {
                game.addEventListener(eventListener);
            }
        }
    }

    public void removeFromCanvas() {
        final Settings settings = Settings.getInstance();

        if (settings.isDebugModeOn()) {
            System.out.println("Removing " + components.size() + " components for the " + this.getClass().getSimpleName() + " class.");
        }

        for (final Component component : components) {
            if (settings.isDebugModeOn()) {
                System.out.println("\tRemoving " + component.getEventListeners().size() + " event listeners for a " + component.getClass().getSimpleName() + ".");
            }

            final Game game = Game.getInstance();
            for (final EventListener eventListener : component.getEventListeners()) {
                game.removeEventListener(eventListener);
            }
        }
    }
}
