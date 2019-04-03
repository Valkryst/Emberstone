package com.valkryst.Emberstone.statistic;

import com.valkryst.Emberstone.mvc.component.ELabel;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Statistic {
    /** The type. */
    @Getter private final StatisticType type;

    /** The value. */
    @Getter private int value;

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

    public ELabel getLabel(final int textSize) {
        return new ELabel(type.getName() + ": " + value, textSize, Color.WHITE);
    }

    /**
     * Sets a new value.
     *
     * @param value
     *        The new value.
     */
    public void setValue(final int value) {
        this.value = value;
    }
}
