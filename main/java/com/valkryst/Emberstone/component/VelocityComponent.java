package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VelocityComponent implements Component {
    /** Velocity on the x-axis. */
    private float x;
    /** Velocity on the y-axis. */
    private float y;

    /**
     * Constructs a new {@code VelocityComponent}.
     *
     * @param x Velocity on the x-axis.
     * @param y Velocity on the y-axis.
     */
    public VelocityComponent(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
}
