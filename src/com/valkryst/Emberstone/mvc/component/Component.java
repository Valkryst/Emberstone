package com.valkryst.Emberstone.mvc.component;

import lombok.Getter;
import lombok.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public abstract class Component {
    /** Location of the component within its parent. */
    protected final Point position;

    /** The event listeners. */
    @Getter private final List<EventListener> eventListeners = new ArrayList<>();

    /**
     * Constructs a new Component.
     *
     * @param position
     *          Location of the component within its parent.
     */
    public Component(final @NonNull Point position) {
        this.position = position;
        createEventListeners();
    }

    /** Creates the component's event listeners. */
    protected void createEventListeners() {}

    /**
     * Draws the component on a graphics context.
     *
     * @param gc
     *          The graphics context.
     */
    public abstract void draw(final Graphics2D gc);

    /**
     * Determines whether a point intersects the component.
     *
     * @param point
     *          The point.
     *
     * @return
     *          Whether the point intersects this component.
     */
    public abstract boolean intersects(final Point point);

    /**
     * Adds an event listener to the component.
     *
     * @param eventListener
     *          The event listener.
     */
    public void addEventListener(final EventListener eventListener) {
        if (eventListener == null) {
            return;
        }

        if (eventListeners.contains(eventListener)) {
            return;
        }

        eventListeners.add(eventListener);
    }

    /**
     * Retrieves the x-axis position of the top-left corner of the component.
     *
     * @return
     *          The x-axis position.
     */
    public int getX() {
        return position.x;
    }

    /**
     * Retrieves the y-axis position of the top-left corner of the component.
     *
     * @return
     *          The y-axis position.
     */
    public int getY() {
        return position.y;
    }

    /**
     * Sets the new x-axis position of the top-left corner of the component.
     *
     * @param x
     *          The new x-axis position.
     */
    public void setX(final int x) {
        position.x = x;
    }

    /**
     * Sets the new y-axis position of the top-left corner of the component.
     *
     * @param y
     *          The new y-axis position.
     */
    public void setY(final int y) {
        position.y = y;
    }
}
