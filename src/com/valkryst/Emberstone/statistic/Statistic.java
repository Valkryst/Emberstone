package com.valkryst.Emberstone.statistic;

import com.valkryst.Emberstone.mvc.component.Label;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Statistic {
    /** The type. */
    @Getter private final StatisticType type;

    /** The value. */
    @Getter private int value;

    /** The runnable functions to run whenever the value is changed. */
    @Getter private final List<Runnable> runnables = new ArrayList<>();

    /**
     * Constructs a new Stat.
     *
     * @param type
     *          The type.
     *
     * @param value
     *          The value.
     */
    public Statistic(final StatisticType type, final int value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Sets a new value.
     *
     * @param value
     *        The new value.
     */
    public void setValue(final int value) {
        this.value = value;

        for (final Runnable runnable : runnables) {
            runnable.run();
        }
    }

    /**
     * Retrieves a label component, displaying the stat's name and value.
     *
     * @return
     *          The label component.
     */
    public Label getLabel() {
        return new Label(new Point(0, 0), type.getName() + ": " + value);
    }
}
