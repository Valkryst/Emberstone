package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the (x, y, z) position of an entity's top-left corner in the
 * world.
 */
@ToString
public class PositionComponent implements Component {
    /** Position on the x-axis. */
    @Getter private double x;
    /** Position on the y-axis. */
    @Getter private double y;
    /** Position on the z-axis. */
    @Getter @Setter private int z;

    /**
     * Constructs a new PositionComponent.
     *
     * @param x
     *          Position on the x-axis.
     * @param y
     *          Position on the y-axis.
     * @param z
     *          Position on the z-axis.
     */
    public PositionComponent(final double x, final double y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a new PositionComponent.
     *
     * @param x
     *          Position on the x-axis.
     * @param y
     *          Position on the y-axis.
     */
    public PositionComponent(final double x, final double y) {
        this(x, y, 0);
    }

    /**
     * Update the position on the x-axis.
     *
     * @param dx
     *          The change in x.
     */
    public void updateX(final double dx) {
        x += dx;
    }

    /**
     * Update the position on the y-axis.
     *
     * @param dy
     *          The change in y.
     */
    public void updateY(final double dy) {
        y += dy;
    }
}
