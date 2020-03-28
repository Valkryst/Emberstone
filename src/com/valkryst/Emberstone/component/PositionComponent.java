package com.valkryst.Emberstone.component;

import com.badlogic.ashley.core.Component;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class PositionComponent implements Component {
    /** Position on the x-axis. */
    @Getter private float x;
    /** Position on the y-axis. */
    @Getter private float y;
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
    public PositionComponent(final float x, final float y, final int z) {
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
    public PositionComponent(final float x, final float y) {
        this(x, y, 0);
    }

    /**
     * Update the position on the x-axis.
     *
     * @param dx
     *          The change in x.
     */
    public void updateX(final float dx) {
        x += dx;
    }

    /**
     * Update the position on the y-axis.
     *
     * @param dy
     *          The change in y.
     */
    public void updateY(final float dy) {
        y += dy;
    }
}
