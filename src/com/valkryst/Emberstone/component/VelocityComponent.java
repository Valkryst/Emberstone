package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import lombok.Data;
import lombok.ToString;

/**
 * Represents the number of pixels/second that an entity can move on the x/y
 * axes.
 */
@Data
@ToString
public class VelocityComponent implements Component {
    /** Velocity on the x-axis. */
    private int x;
    /** Velocity on the y-axis. */
    private int y;

    /**
     * Constructs a new VelocityComponent.
     *
     * @param x
     *          Velocity on the x-axis.
     * @param y
     *          Velocity on the y-axis.
     */
    public VelocityComponent(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
}
